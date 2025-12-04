-- Create table for HelloEntity
CREATE TABLE IF NOT EXISTS hello (
                                     id BIGINT PRIMARY KEY,
                                     message VARCHAR(255)
    );


-- User
CREATE TABLE IF NOT EXISTS users (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    password VARCHAR(255) NOT NULL
);

