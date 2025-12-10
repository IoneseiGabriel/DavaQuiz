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

-- User
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

 -- File
CREATE TABLE files (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      content BLOB NOT NULL,
                      name VARCHAR(100) UNIQUE NOT NULL,
                      url VARCHAR(150) UNIQUE NOT NULL,
                      content_type VARCHAR(50) NOT NULL
);

CREATE INDEX idx_file_url ON files (url);