# Stock Service

A Spring Boot microservice that provides real-time stock data, order management, and time series analytics. The system handles stock information retrieval, order processing, and execution reporting through event-driven architecture.

## Overview

This Stock Service processes incoming stock orders via Kafka, manages order lifecycle, and provides REST APIs for stock data and order information. It uses Redis for time series data and PostgreSQL for persistent storage.

## Core Components

### 1. Order Service

- Consumes incoming orders from Kafka topics
- Processes execution reports and updates order status
- Implements SHA-256 hash-based deduplication using Redis sets
- Manages order lifecycle (NEW → PARTIALLY_FULFILLED → FULFILLED)

### 2. Stock Service

- Retrieves stock information from PostgreSQL
- Fetches time series OHLC data from Redis using Lua scripts
- Supports multiple time periods (DAILY, WEEKLY, MONTHLY, etc.)

### 3. Redis Service

- Manages time series data with different time buckets (5m, 1d)
- Handles set operations for order deduplication
- Executes custom Lua scripts for batch OHLC queries

## API Endpoints

### Stock Endpoints

- `GET /stock/get-one-stock?symbol={symbol}` - Get single stock information
- `GET /stock/get-multiple-stocks?symbols={symbol1,symbol2}` - Get multiple stocks (max 20)
- `GET /stock/get-time-series?symbol={symbol}&period={period}` - Get OHLC time series data

### Order Endpoints

- `GET /order/get-order-info-short-list?userId={userId}` - Get user's order summary
- `GET /order/get-order-info?userId={userId}&orderId={orderId}` - Get detailed order information

## Performance Features

### High Throughput

- Batch processing of orders and execution reports
- Redis-based caching for frequently accessed data
- Optimized database queries with proper indexing

### Low Latency

- Direct Redis time series queries using Lua scripts
- Efficient Protocol Buffer serialization for Kafka messages
- Single network calls for complete OHLC datasets

### Reliability

- Transactional outbox pattern for reliable message processing
- Duplicate order prevention using Redis sets
- Order status tracking with execution history

## Technical Stack

- Spring Boot
- PostgreSQL
- Redis
- Apache Kafka

## Configuration

Configurable via `application.properties`:

- Database connection settings
- Kafka bootstrap servers and consumer groups
- Redis connection details
- JPA/Hibernate settings
