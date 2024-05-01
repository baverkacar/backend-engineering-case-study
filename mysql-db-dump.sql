CREATE TABLE IF NOT EXISTS `users` (
                                      `user_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                      `username` VARCHAR(255) NOT NULL UNIQUE,
                                      `email` VARCHAR(255) NOT NULL UNIQUE,
                                      `password` CHAR(60) NOT NULL,
                                      `country` VARCHAR(255) NOT NULL,
                                      `level` INT DEFAULT 1,
                                      `coins` INT DEFAULT 5000,
                                      `created_at` TIMESTAMP DEFAULT NOW(),
                                      `updated_at` TIMESTAMP DEFAULT NOW() ON UPDATE NOW()
);

CREATE TABLE IF NOT EXISTS `tournaments` (
                                             `tournament_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                             `start_time` DATETIME NOT NULL,
                                             `end_time` DATETIME NOT NULL,
                                             `status` VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS `tournament_groups` (
                                                  `group_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                  `tournament_id` BIGINT,
                                                  `user_id` BIGINT,
                                                  `score` INT DEFAULT 0,
                                                  `rank` INT,
                                                  `has_group_begun` BOOLEAN NOT NULL DEFAULT FALSE,
                                                  `created_at` TIMESTAMP DEFAULT NOW(),
                                                  `updated_at` TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),
                                                  FOREIGN KEY (`tournament_id`) REFERENCES `tournaments`(`tournament_id`),
                                                  FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`)
);

CREATE TABLE IF NOT EXISTS `tournament_rewards` (
                                                   `reward_id` BIGINT PRIMARY KEY AUTO_INCREMENT,
                                                   `user_id` BIGINT,
                                                   `tournament_id` BIGINT,
                                                   `coins_won` INT,
                                                   `claimed` BOOLEAN NOT NULL DEFAULT FALSE,
                                                   `created_at` TIMESTAMP DEFAULT NOW(),
                                                   `updated_at` TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),
                                                   FOREIGN KEY (`user_id`) REFERENCES `users`(`user_id`),
                                                   FOREIGN KEY (`tournament_id`) REFERENCES `tournaments`(`tournament_id`)
);

INSERT INTO `tournaments` (`start_time`, `end_time`, `status`)
VALUES ('2024-05-01 00:00:00', '2024-05-01 20:00:00', 'Active');

INSERT INTO users (username, email, password, country, level, coins, created_at, updated_at)
VALUES
    ('user1', 'user1@email.com', 'hashed_password', 'Turkey', 20, 1500, NOW(), NOW()),
    ('user2', 'user2@email.com', 'hashed_password', 'Turkey', 22, 1200, NOW(), NOW()),
    ('user3', 'user3@email.com', 'hashed_password', 'France', 23, 1300, NOW(), NOW()),
    ('user4', 'user4@email.com', 'hashed_password', 'France', 21, 1100, NOW(), NOW()),
    ('user5', 'user5@email.com', 'hashed_password', 'Germany', 24, 1600, NOW(), NOW()),
    ('user6', 'user6@email.com', 'hashed_password', 'Germany', 20, 1500, NOW(), NOW()),
    ('user7', 'user7@email.com', 'hashed_password', 'United Kingdom', 25, 1400, NOW(), NOW()),
    ('user8', 'user8@email.com', 'hashed_password', 'United Kingdom', 20, 1300, NOW(), NOW()),
    ('user9', 'user9@email.com', 'hashed_password', 'USA', 20, 1800, NOW(), NOW()),
    ('user10', 'user10@email.com', 'hashed_password', 'Italy', 20, 1600, NOW(), NOW());

INSERT INTO users (username, email, password, country, level, coins, created_at, updated_at)
VALUES ('lowleveluser', 'lowleveluser@email.com', 'hashed_password', 'Turkey', 15, 900, NOW(), NOW());

INSERT INTO tournaments (start_time, end_time, status)
VALUES ('2024-04-30 00:00:00', '2024-04-30 20:00:00', 'Completed');

