CREATE TABLE IF NOT EXISTS `users` (
                                      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `username` VARCHAR(255) NOT NULL UNIQUE,
                                      `email` VARCHAR(255) NOT NULL UNIQUE,
                                      `password` CHAR(60) NOT NULL,
                                      `country` VARCHAR(255) NOT NULL,
                                      `level` INT DEFAULT 1,
                                      `coins` INT DEFAULT 5000,
                                      `created_at` TIMESTAMP DEFAULT NOW(),
                                      `updated_at` TIMESTAMP DEFAULT NOW() ON UPDATE NOW(),
                                      PRIMARY KEY (`id`)
);

CREATE TABLE IF NOT EXISTS `tournaments` (
                                             `tournament_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                             `start_time` DATETIME NOT NULL,
                                             `end_time` DATETIME NOT NULL,
                                             `status` VARCHAR(20) NOT NULL
);

CREATE TABLE IF NOT EXISTS `tournament_groups` (
                                                  `group_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                                  `tournament_id` INT UNSIGNED NOT NULL,
                                                  `user_id` INT UNSIGNED NOT NULL,
                                                  `score` INT DEFAULT 0,
                                                  `rank` INT,
                                                  `has_group_begun` BOOLEAN NOT NULL DEFAULT FALSE,
                                                  FOREIGN KEY (`tournament_id`) REFERENCES `tournaments`(`tournament_id`),
                                                  FOREIGN KEY (`user_id`) REFERENCES `users`(`id`)
);

CREATE TABLE IF NOT EXISTS `tournament_rewards` (
                                                   `reward_id` INT UNSIGNED AUTO_INCREMENT PRIMARY KEY,
                                                   `user_id` INT UNSIGNED NOT NULL,
                                                   `tournament_id` INT UNSIGNED NOT NULL,
                                                   `coins_won` INT,
                                                   `claimed` BOOLEAN NOT NULL DEFAULT FALSE,
                                                   FOREIGN KEY (`user_id`) REFERENCES `users`(`id`),
                                                   FOREIGN KEY (`tournament_id`) REFERENCES `tournaments`(`tournament_id`)
);

INSERT INTO `tournaments` (`start_time`, `end_time`, `status`)
VALUES ('2024-05-01 00:00:00', '2024-05-01 20:00:00', 'Active');