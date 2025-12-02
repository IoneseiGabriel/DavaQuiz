CREATE TABLE game (
                      id BIGINT AUTO_INCREMENT PRIMARY KEY,
                      title VARCHAR(100) NOT NULL,
                      description VARCHAR(500),
                      status VARCHAR(16) NOT NULL CHECK (status IN ('DRAFT', 'PUBLISHED')),
                      created_by BIGINT NOT NULL,
                      created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                      updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

ALTER TABLE game ALTER COLUMN id RESTART WITH 6;

-- Index on ('created_by')
CREATE INDEX idx_game_created_by ON game (created_by);

CREATE TABLE question (
                          id BIGINT AUTO_INCREMENT  PRIMARY KEY,
                          game_id BIGINT NOT NULL REFERENCES game(id) ON DELETE CASCADE,
                          text VARCHAR(500) NOT NULL,
                          image_url VARCHAR(1024),

    -- Store JSON or comma-separated values as text for H2
                          options VARCHAR(2000),

                          correct_option_index INT,

    -- Just keep the index >= 0 rule
                          CHECK (
                              correct_option_index IS NULL OR correct_option_index >= 0
                              )
);

ALTER TABLE question ALTER COLUMN id RESTART WITH 6;

-- Index on game_id
CREATE INDEX idx_question_game_id ON question (game_id);


