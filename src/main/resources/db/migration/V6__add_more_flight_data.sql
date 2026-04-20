-- Flyway 迁移 V6：添加更多航班数据（扩大数据量）

-- 1. 添加更多机场（国际机场）
INSERT INTO airport (code, name, city, country, timezone) VALUES
('HKG', '香港国际机场', '香港', '中国', 'Asia/Hong_Kong'),
('TPE', '台北桃园国际机场', '台北', '中国', 'Asia/Taipei'),
('NRT', '东京成田国际机场', '东京', '日本', 'Asia/Tokyo'),
('KIX', '大阪关西国际机场', '大阪', '日本', 'Asia/Tokyo'),
('ICN', '首尔仁川国际机场', '首尔', '韩国', 'Asia/Seoul'),
('SIN', '新加坡樟宜机场', '新加坡', '新加坡', 'Asia/Singapore'),
('BKK', '曼谷素万那普机场', '曼谷', '泰国', 'Asia/Bangkok'),
('KUL', '吉隆坡国际机场', '吉隆坡', '马来西亚', 'Asia/Kuala_Lumpur'),
('SYD', '悉尼金斯福德史密斯机场', '悉尼', '澳大利亚', 'Australia/Sydney'),
('MEL', '墨尔本机场', '墨尔本', '澳大利亚', 'Australia/Melbourne'),
('LAX', '洛杉矶国际机场', '洛杉矶', '美国', 'America/Los_Angeles'),
('JFK', '纽约肯尼迪国际机场', '纽约', '美国', 'America/New_York'),
('LHR', '伦敦希思罗机场', '伦敦', '英国', 'Europe/London'),
('CDG', '巴黎戴高乐机场', '巴黎', '法国', 'Europe/Paris'),
('FRA', '法兰克福机场', '法兰克福', '德国', 'Europe/Berlin'),
('DXB', '迪拜国际机场', '迪拜', '阿联酋', 'Asia/Dubai'),
('DEL', '德里英迪拉甘地国际机场', '德里', '印度', 'Asia/Kolkata'),
('BOM', '孟买贾特拉帕蒂希瓦吉国际机场', '孟买', '印度', 'Asia/Kolkata');

-- 2. 添加更多航空公司（国内+国际）
INSERT INTO airline (code, name) VALUES
('FM', '上海航空'),
('ZH', '深圳航空'),
('MF', '厦门航空'),
('9C', '春秋航空'),
('KN', '中国联合航空'),
('JD', '首都航空'),
('G5', '华夏航空'),
('KY', '昆明航空'),
('8L', '祥鹏航空'),
('GS', '天津航空'),
('OQ', '重庆航空'),
('GJ', '长龙航空'),
('JL', '日本航空'),
('NH', '全日空航空'),
('KE', '大韩航空'),
('OZ', '韩亚航空'),
('SQ', '新加坡航空'),
('TG', '泰国国际航空'),
('CX', '国泰航空'),
('BR', '长荣航空'),
('AA', '美国航空'),
('UA', '美国联合航空'),
('DL', '达美航空'),
('BA', '英国航空'),
('AF', '法国航空'),
('LH', '德国汉莎航空'),
('EK', '阿联酋航空'),
('QR', '卡塔尔航空'),
('EY', '阿提哈德航空');

-- 3. 添加更多航班（未来3个月，各种航线组合）
-- 函数：生成随机航班数据
-- 先删除可能存在的函数
DROP ALIAS IF EXISTS GENERATE_FLIGHT_DATA;
CREATE ALIAS GENERATE_FLIGHT_DATA AS $$
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;
@CODE
void generateFlightData(Connection conn) throws SQLException {
    Random random = new Random(42); // 固定种子保证可重复
    DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    // 航线组合
    String[][] domesticRoutes = {
        {"PEK", "PVG"}, {"PEK", "SHA"}, {"PEK", "CAN"}, {"PEK", "SZX"}, {"PEK", "CTU"},
        {"PEK", "CKG"}, {"PEK", "KMG"}, {"PEK", "XIY"}, {"PEK", "HGH"}, {"PEK", "HKG"},
        {"PVG", "CAN"}, {"PVG", "SZX"}, {"PVG", "CTU"}, {"PVG", "KMG"}, {"PVG", "XIY"},
        {"PVG", "HGH"}, {"PVG", "HKG"}, {"CAN", "CTU"}, {"CAN", "KMG"}, {"CAN", "XIY"},
        {"CAN", "HGH"}, {"SZX", "CTU"}, {"SZX", "KMG"}, {"SZX", "XIY"}, {"SZX", "HGH"}
    };

    String[][] internationalRoutes = {
        {"PEK", "NRT"}, {"PEK", "KIX"}, {"PEK", "ICN"}, {"PEK", "SIN"}, {"PEK", "BKK"},
        {"PEK", "KUL"}, {"PEK", "SYD"}, {"PEK", "LAX"}, {"PEK", "JFK"}, {"PEK", "LHR"},
        {"PVG", "NRT"}, {"PVG", "KIX"}, {"PVG", "ICN"}, {"PVG", "SIN"}, {"PVG", "BKK"},
        {"PVG", "KUL"}, {"PVG", "SYD"}, {"PVG", "LAX"}, {"PVG", "JFK"}, {"PVG", "LHR"},
        {"CAN", "SIN"}, {"CAN", "BKK"}, {"CAN", "KUL"}, {"CAN", "SYD"}, {"CAN", "LAX"},
        {"HKG", "NRT"}, {"HKG", "KIX"}, {"HKG", "ICN"}, {"HKG", "SIN"}, {"HKG", "BKK"},
        {"HKG", "KUL"}, {"HKG", "SYD"}, {"HKG", "LAX"}, {"HKG", "JFK"}, {"HKG", "LHR"}
    };

    // 航空公司代码
    String[] domesticAirlines = {"CA", "MU", "CZ", "HU", "3U", "FM", "ZH", "MF", "9C", "KN"};
    String[] internationalAirlines = {"JL", "NH", "KE", "OZ", "SQ", "TG", "CX", "BR", "AA", "UA", "DL", "BA", "AF", "LH", "EK"};

    // 飞机型号
    String[] aircraftTypes = {"A320", "A321", "A330", "A350", "B737", "B738", "B777", "B787", "A380"};

    // 舱位等级
    String[] cabinClasses = {"Y", "C", "F"}; // 经济舱、商务舱、头等舱

    // 机场名称映射（简化）
    java.util.Map<String, String> airportNames = new java.util.HashMap<>();
    airportNames.put("PEK", "北京首都国际机场");
    airportNames.put("PVG", "上海浦东国际机场");
    airportNames.put("SHA", "上海虹桥国际机场");
    airportNames.put("CAN", "广州白云国际机场");
    airportNames.put("SZX", "深圳宝安国际机场");
    airportNames.put("CTU", "成都天府国际机场");
    airportNames.put("CKG", "重庆江北国际机场");
    airportNames.put("KMG", "昆明长水国际机场");
    airportNames.put("XIY", "西安咸阳国际机场");
    airportNames.put("HGH", "杭州萧山国际机场");
    airportNames.put("HKG", "香港国际机场");
    airportNames.put("TPE", "台北桃园国际机场");
    airportNames.put("NRT", "东京成田国际机场");
    airportNames.put("KIX", "大阪关西国际机场");
    airportNames.put("ICN", "首尔仁川国际机场");
    airportNames.put("SIN", "新加坡樟宜机场");
    airportNames.put("BKK", "曼谷素万那普机场");
    airportNames.put("KUL", "吉隆坡国际机场");
    airportNames.put("SYD", "悉尼金斯福德史密斯机场");
    airportNames.put("MEL", "墨尔本机场");
    airportNames.put("LAX", "洛杉矶国际机场");
    airportNames.put("JFK", "纽约肯尼迪国际机场");
    airportNames.put("LHR", "伦敦希思罗机场");
    airportNames.put("CDG", "巴黎戴高乐机场");
    airportNames.put("FRA", "法兰克福机场");
    airportNames.put("DXB", "迪拜国际机场");
    airportNames.put("DEL", "德里英迪拉甘地国际机场");
    airportNames.put("BOM", "孟买贾特拉帕蒂希瓦吉国际机场");

    PreparedStatement pstmt = conn.prepareStatement(
        "INSERT INTO flight (departure_time, arrival_time, duration_minutes, effective_date, " +
        "flight_number, airline_code, cabin_class, aircraft_type, stops, is_direct, " +
        "departure_airport, arrival_airport, departure_airport_code, arrival_airport_code, " +
        "price, original_price, available_seats, status, created_at, updated_at) " +
        "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP)"
    );

    int flightCount = 0;

    // 生成未来90天的航班
    for (int dayOffset = 0; dayOffset < 90; dayOffset++) {
        LocalDate date = LocalDate.now().plusDays(dayOffset);

        // 每天生成一些国内航班
        for (int i = 0; i < 30; i++) {
            String[] route = domesticRoutes[random.nextInt(domesticRoutes.length)];
            String depCode = route[0];
            String arrCode = route[1];

            // 生成航班时间（每天6:00-22:00之间）
            int hour = 6 + random.nextInt(16);
            int minute = random.nextInt(60);
            LocalDateTime depTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, minute);

            // 飞行时间：1-4小时
            int duration = 60 + random.nextInt(180);
            LocalDateTime arrTime = depTime.plusMinutes(duration);

            // 航班号
            String airline = domesticAirlines[random.nextInt(domesticAirlines.length)];
            String flightNum = airline + (1000 + random.nextInt(9000));

            // 舱位等级
            String cabinClass = cabinClasses[random.nextInt(cabinClasses.length)];

            // 价格：根据距离和舱位计算
            int basePrice = 300 + random.nextInt(700);
            int priceMultiplier = 1;
            if (cabinClass.equals("C")) priceMultiplier = 3;
            if (cabinClass.equals("F")) priceMultiplier = 6;
            int price = basePrice * priceMultiplier;
            int originalPrice = price + (int)(price * 0.2 * random.nextDouble());

            // 可用座位
            int seats = 20 + random.nextInt(180);

            pstmt.setString(1, depTime.format(timeFormatter));
            pstmt.setString(2, arrTime.format(timeFormatter));
            pstmt.setInt(3, duration);
            pstmt.setString(4, date.format(dateFormatter));
            pstmt.setString(5, flightNum);
            pstmt.setString(6, airline);
            pstmt.setString(7, cabinClass);
            pstmt.setString(8, aircraftTypes[random.nextInt(aircraftTypes.length)]);
            pstmt.setInt(9, 0); // 直飞
            pstmt.setBoolean(10, true);
            pstmt.setString(11, airportNames.get(depCode));
            pstmt.setString(12, airportNames.get(arrCode));
            pstmt.setString(13, depCode);
            pstmt.setString(14, arrCode);
            pstmt.setInt(15, price);
            pstmt.setInt(16, originalPrice);
            pstmt.setInt(17, seats);
            pstmt.setString(18, "SCHEDULED");

            pstmt.addBatch();
            flightCount++;

            if (flightCount % 100 == 0) {
                pstmt.executeBatch();
            }
        }

        // 每天生成一些国际航班
        for (int i = 0; i < 15; i++) {
            String[] route = internationalRoutes[random.nextInt(internationalRoutes.length)];
            String depCode = route[0];
            String arrCode = route[1];

            // 生成航班时间
            int hour = random.nextInt(24);
            int minute = random.nextInt(60);
            LocalDateTime depTime = LocalDateTime.of(date.getYear(), date.getMonthValue(), date.getDayOfMonth(), hour, minute);

            // 飞行时间：2-12小时
            int duration = 120 + random.nextInt(600);
            LocalDateTime arrTime = depTime.plusMinutes(duration);

            // 航班号
            String airline = internationalAirlines[random.nextInt(internationalAirlines.length)];
            String flightNum = airline + (100 + random.nextInt(900));

            // 舱位等级
            String cabinClass = cabinClasses[random.nextInt(cabinClasses.length)];

            // 价格：国际航班更贵
            int basePrice = 1000 + random.nextInt(4000);
            int priceMultiplier = 1;
            if (cabinClass.equals("C")) priceMultiplier = 3;
            if (cabinClass.equals("F")) priceMultiplier = 6;
            int price = basePrice * priceMultiplier;
            int originalPrice = price + (int)(price * 0.3 * random.nextDouble());

            // 可用座位
            int seats = 50 + random.nextInt(300);

            pstmt.setString(1, depTime.format(timeFormatter));
            pstmt.setString(2, arrTime.format(timeFormatter));
            pstmt.setInt(3, duration);
            pstmt.setString(4, date.format(dateFormatter));
            pstmt.setString(5, flightNum);
            pstmt.setString(6, airline);
            pstmt.setString(7, cabinClass);
            pstmt.setString(8, aircraftTypes[random.nextInt(aircraftTypes.length)]);
            pstmt.setInt(9, 0); // 直飞
            pstmt.setBoolean(10, true);
            pstmt.setString(11, airportNames.get(depCode));
            pstmt.setString(12, airportNames.get(arrCode));
            pstmt.setString(13, depCode);
            pstmt.setString(14, arrCode);
            pstmt.setInt(15, price);
            pstmt.setInt(16, originalPrice);
            pstmt.setInt(17, seats);
            pstmt.setString(18, "SCHEDULED");

            pstmt.addBatch();
            flightCount++;

            if (flightCount % 100 == 0) {
                pstmt.executeBatch();
            }
        }
    }

    pstmt.executeBatch();
    pstmt.close();
}
$$;

-- 调用函数生成航班数据
CALL GENERATE_FLIGHT_DATA();

-- 4. 添加更多用户搜索历史
INSERT INTO user_flight_history (user_id, departure_airport_code, arrival_airport_code, behavior_type, flight_number, created_at)
SELECT
    1 as user_id,
    CASE
        WHEN MOD(id, 10) = 0 THEN 'PEK'
        WHEN MOD(id, 10) = 1 THEN 'PVG'
        WHEN MOD(id, 10) = 2 THEN 'CAN'
        WHEN MOD(id, 10) = 3 THEN 'SZX'
        WHEN MOD(id, 10) = 4 THEN 'CTU'
        WHEN MOD(id, 10) = 5 THEN 'KMG'
        WHEN MOD(id, 10) = 6 THEN 'XIY'
        WHEN MOD(id, 10) = 7 THEN 'HGH'
        WHEN MOD(id, 10) = 8 THEN 'HKG'
        ELSE 'SHA'
    END as departure_airport_code,
    CASE
        WHEN MOD(id, 10) = 0 THEN 'PVG'
        WHEN MOD(id, 10) = 1 THEN 'CAN'
        WHEN MOD(id, 10) = 2 THEN 'SZX'
        WHEN MOD(id, 10) = 3 THEN 'CTU'
        WHEN MOD(id, 10) = 4 THEN 'KMG'
        WHEN MOD(id, 10) = 5 THEN 'XIY'
        WHEN MOD(id, 10) = 6 THEN 'HGH'
        WHEN MOD(id, 10) = 7 THEN 'HKG'
        WHEN MOD(id, 10) = 8 THEN 'PEK'
        ELSE 'CAN'
    END as arrival_airport_code,
    'SEARCH' as behavior_type,
    NULL as flight_number,
    DATEADD('DAY', -MOD(id, 30), CURRENT_TIMESTAMP) as created_at
FROM SYSTEM_RANGE(1, 500);

-- 5. 添加更多价格历史记录
INSERT INTO flight_price (flight_number, departure_airport_code, arrival_airport_code, cabin_class, price, record_at)
SELECT
    f.flight_number,
    f.departure_airport_code,
    f.arrival_airport_code,
    f.cabin_class,
    -- 模拟价格波动：基础价格 ± 20%
    CAST(f.price * (0.8 + (RAND() * 0.4)) AS INT) as price,
    DATEADD('DAY', -days_ago, f.departure_time) as record_at
FROM flight f
CROSS JOIN (VALUES (0), (1), (2), (3), (4), (5), (6), (7), (8), (9), (10), (11), (12), (13), (14)) as days(days_ago)
WHERE f.departure_time > CURRENT_TIMESTAMP
AND RAND() < 0.3  -- 随机选择30%的航班生成价格历史
LIMIT 5000;

-- 6. 添加更多用户（测试用户）
INSERT INTO app_user (username, password, email, phone, created_at, updated_at) VALUES
('user1', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user1@example.com', '13800138001', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user2', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user2@example.com', '13800138002', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user3', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user3@example.com', '13800138003', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user4', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user4@example.com', '13800138004', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('user5', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'user5@example.com', '13800138005', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP),
('premium_user', '$2a$10$N.zmdr9k7uOCQb376NoUnuTJ8iAt6Z5EHsM8lE9lBOsl7iKTVKIUi', 'premium@example.com', '13900139000', CURRENT_TIMESTAMP, CURRENT_TIMESTAMP);

-- 7. 添加用户反馈数据
INSERT INTO user_feedback (user_id, flight_id, feedback_type, feedback_text, ai_analysis, confidence_score, metadata, created_at, updated_at)
SELECT
    u.id as user_id,
    f.id as flight_id,
    CASE MOD(ROW_NUMBER() OVER (), 3)
        WHEN 0 THEN 'LIKE'
        WHEN 1 THEN 'DISLIKE'
        ELSE 'NEUTRAL'
    END as feedback_type,
    CASE MOD(ROW_NUMBER() OVER (), 3)
        WHEN 0 THEN '航班准时，服务很好'
        WHEN 1 THEN '价格有点高，座位空间小'
        ELSE '中规中矩，没什么特别'
    END as feedback_text,
    '{"analysis": "AI分析结果", "confidence": 0.8}' as ai_analysis,
    0.7 + (RAND() * 0.3) as confidence_score,
    '{"source": "web", "device": "mobile"}' as metadata,
    DATEADD('DAY', -MOD(ROW_NUMBER() OVER (), 30), CURRENT_TIMESTAMP) as created_at,
    DATEADD('DAY', -MOD(ROW_NUMBER() OVER (), 30), CURRENT_TIMESTAMP) as updated_at
FROM app_user u
CROSS JOIN flight f
WHERE u.username LIKE 'user%'
AND RAND() < 0.05  -- 每个用户对5%的航班有反馈
LIMIT 1000;