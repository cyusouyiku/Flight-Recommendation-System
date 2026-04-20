-- Flyway 迁移 V5：添加用户反馈表
CREATE TABLE IF NOT EXISTS user_feedback (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT,
    flight_id BIGINT,
    feedback_type VARCHAR(32) NOT NULL,
    feedback_text TEXT,
    ai_analysis TEXT,
    confidence_score DECIMAL(3,2),
    metadata TEXT, -- H2不支持JSON类型，改为TEXT
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

-- 创建索引（H2语法）
CREATE INDEX IF NOT EXISTS idx_user_feedback_user_id ON user_feedback(user_id);
CREATE INDEX IF NOT EXISTS idx_user_feedback_feedback_type ON user_feedback(feedback_type);
CREATE INDEX IF NOT EXISTS idx_user_feedback_created_at ON user_feedback(created_at);

-- 添加外键约束（可选）
-- H2支持外键，但注意app_user和flight表必须存在
ALTER TABLE user_feedback
ADD CONSTRAINT IF NOT EXISTS fk_user_feedback_user
FOREIGN KEY (user_id) REFERENCES app_user(id) ON DELETE SET NULL;

ALTER TABLE user_feedback
ADD CONSTRAINT IF NOT EXISTS fk_user_feedback_flight
FOREIGN KEY (flight_id) REFERENCES flight(id) ON DELETE SET NULL;