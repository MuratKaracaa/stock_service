package com.karacam.stock_service.services;

import com.karacam.stock_service.models.TimeSeriesPeriods;
import com.karacam.stock_service.utils.TimeUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.SetOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;

@Service
public class RedisService {
    private final RedisTemplate<String, Object> redisTemplate;
    private final StringRedisSerializer stringRedisSerializer;
    private final GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer;
    private SetOperations<String, Object> setOperations;

    /**
     * The script runs OHLC queries on redis instance to fetch all the relative data in a single network call.
     * example keys are time_series_prefix:AAPL:1d:open,time_series_prefix:AAPL:1d:high,time_series_prefix:AAPL:1d:low,time_series_prefix:AAPL:1d:close
     */
    private final String TIME_SERIES_EXECUTION_SCRIPT = """
            local results = {}
            for i = 1, #KEYS do
                local result = redis.call('TS.RANGE', KEYS[i], ARGV[1], ARGV[2], ARGV[3])
                results[i] = result
            end
            return results
            """;
    ;

    private final String TIME_SERIES_PREFIX = "time_series_prefix:";
    private final String FIVE_MIN_BUCKET_SUFFIX = "5m";
    private final String DAILY_BUCKET_SUFFIX = "1d";
    private final String[] OHLC_SUFFICES = {"open", "high", "low", "close"};

    private final String TO_NOW_TIMESTAMP = "+";
    private final String LATEST_ARG = "LATEST";

    @PostConstruct
    public void init() {
        setOperations = redisTemplate.opsForSet();
    }

    @Autowired
    public RedisService(RedisTemplate<String, Object> redisTemplate_, StringRedisSerializer stringRedisSerializer_, GenericJackson2JsonRedisSerializer genericJackson2JsonRedisSerializer_) {
        this.redisTemplate = redisTemplate_;
        this.stringRedisSerializer = stringRedisSerializer_;
        this.genericJackson2JsonRedisSerializer = genericJackson2JsonRedisSerializer_;
    }

    public boolean checkAndSet(String setKey, String values) {
        Long insertedCount = setOperations.add(setKey, values);
        return insertedCount > 0;

    }

    /**
     * @param symbol
     * @param period
     * @return Returns a 3d list. The length of the first dimension will be 4 corresponding to OHLC in order because data is aggregated
     * on redis and queried with the lua script (field TIME_SERIES_EXECUTION_SCRIPT) in accordance with that.
     * The length of the second dimension will depend on the period parameter, WEEKLY will have 7 items,
     * DAILY will have however much data is present up to that time in 5 min buckets (configured at redis) etc
     * and the third dimension is basically a pair, first being the unix timestamp and second being the latest trading price
     */
    public List<List<List<Number>>> getTimeSeriesDataForStock(String symbol, TimeSeriesPeriods period) {
        Long fromTimeStamp = TimeUtil.getTimeSeriesTimeStamp(period);
        List<String> ochlKeys = prepareOHLCKeys(symbol, period);
        return executeTimeSeriesScript(ochlKeys, fromTimeStamp, period == TimeSeriesPeriods.DAILY);
    }

    /**
     * Because Lettuce does not support time series module operations, this function was created to execute a lua script
     * on redis to retrieve raw data. The most convenient way to pass all arguments to the lua script was using a string
     * serializer and a generic jackson serializer was sufficient to retrieve the timestamp price pair. Without these serializers
     * Lettuce cannot properly create objects from retrieved redis data.
     */
    private List<List<List<Number>>> executeTimeSeriesScript(List<String> keys, Long timestamp, boolean hasLatest) {
        return redisTemplate.execute(
                RedisScript.of(TIME_SERIES_EXECUTION_SCRIPT, List.class),
                stringRedisSerializer,
                (RedisSerializer) genericJackson2JsonRedisSerializer,
                keys,
                timestamp.toString(),
                TO_NOW_TIMESTAMP,
                hasLatest ? LATEST_ARG : null
        );
    }

    private List<String> prepareOHLCKeys(String symbol, TimeSeriesPeriods period) {
        String bucketSuffix = period == TimeSeriesPeriods.DAILY ? FIVE_MIN_BUCKET_SUFFIX : DAILY_BUCKET_SUFFIX;

        return Arrays.stream(OHLC_SUFFICES).map(suffix -> TIME_SERIES_PREFIX + symbol + ":" + bucketSuffix + ":" + suffix).toList();
    }
}
