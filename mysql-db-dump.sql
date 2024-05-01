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
