package com.karacam.stock_service.utils;

import com.google.protobuf.Timestamp;
import com.karacam.stock_service.models.TimeSeriesPeriods;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.List;

public class TimeUtil {

    private static final ZoneOffset UTC_OFFSET = ZoneOffset.ofHours(3);
    private static final List<DayOfWeek> WEEK_END_DAYS = List.of(DayOfWeek.SATURDAY, DayOfWeek.SUNDAY);

    public static Instant protoTimestampToInstant(Timestamp timestamp) {
        return Instant.ofEpochSecond(timestamp.getSeconds(), timestamp.getNanos());
    }

    public static Timestamp instantToProtoTimestamp(Instant instant) {
        return Timestamp.newBuilder().setSeconds(instant.getEpochSecond()).setNanos(instant.getNano()).build();
    }

    public static String getToday() {
        return LocalDate.now().format(DateTimeFormatter.ofPattern("yyyyMMdd"));
    }

    public static String getToday(String format) {
        return LocalDate.now().format(DateTimeFormatter.ofPattern(format));
    }

    public static Long getTimeSeriesTimeStamp(TimeSeriesPeriods period) {
        LocalDate baseDate = LocalDate.now(UTC_OFFSET);

        if (WEEK_END_DAYS.contains(baseDate.getDayOfWeek())) {
            baseDate = baseDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.FRIDAY));
        }
        
        LocalDate date = switch (period) {
            case TimeSeriesPeriods.DAILY -> baseDate;
            case TimeSeriesPeriods.WEEKLY -> baseDate.minusWeeks(1);
            case TimeSeriesPeriods.MONTHLY -> baseDate.minusMonths(1);
            case TimeSeriesPeriods.SEMI_ANNUAL -> baseDate.minusMonths(6);
            case TimeSeriesPeriods.ANNUAL -> baseDate.minusYears(1);
            case TimeSeriesPeriods.FIVE_YEAR -> baseDate.minusYears(5);
        };

        return date.atStartOfDay().toInstant(UTC_OFFSET).toEpochMilli();
    }

    public static String formatUnixTimestamp(Long unixTimeStamp, String format) {
        return LocalDateTime.ofInstant(Instant.ofEpochMilli(unixTimeStamp), UTC_OFFSET).format(DateTimeFormatter.ofPattern(format));
    }
}
