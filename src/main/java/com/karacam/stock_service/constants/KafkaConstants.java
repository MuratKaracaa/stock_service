package com.karacam.stock_service.constants;

public class KafkaConstants {
    public static final String incoming_order_topic = "incoming_orders";
    public static final String execution_report_topic = "execution_reports";
    public static final String incoming_order_consumer_group_id = "stock_service_incoming_order_consumer";
    public static final String execution_report_consumer_group_id = "stock_service_execution_report_consumer";
}
