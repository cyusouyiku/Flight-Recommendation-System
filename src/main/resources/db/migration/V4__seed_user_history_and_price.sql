-- 种子数据：用户搜索历史（用于热门航线、推荐）
INSERT INTO user_flight_history (user_id, departure_airport_code, arrival_airport_code, behavior_type, flight_number, created_at) VALUES
(1, 'PEK', 'PVG', 'SEARCH', NULL, CURRENT_TIMESTAMP),
(1, 'PEK', 'PVG', 'SEARCH', NULL, CURRENT_TIMESTAMP),
(1, 'PVG', 'CAN', 'SEARCH', NULL, CURRENT_TIMESTAMP),
(1, 'PEK', 'CTU', 'SEARCH', NULL, CURRENT_TIMESTAMP);

-- 价格历史（从航班表采集的模拟数据）
INSERT INTO flight_price (flight_number, departure_airport_code, arrival_airport_code, cabin_class, price, record_at) VALUES
('CA1234', 'PEK', 'PVG', 'Y', 680, CURRENT_TIMESTAMP),
('CA1234', 'PEK', 'PVG', 'Y', 650, DATEADD('DAY', -1, CURRENT_TIMESTAMP)),
('CA1234', 'PEK', 'PVG', 'Y', 700, DATEADD('DAY', -2, CURRENT_TIMESTAMP));
