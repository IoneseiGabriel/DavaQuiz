CREATE TABLE game (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(100) NOT NULL,
                      description VARCHAR(500),
                      status VARCHAR(16) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED')),
                      created_by BIGINT NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Index on ('created_by')
CREATE INDEX idx_game_created_by ON game (created_by);