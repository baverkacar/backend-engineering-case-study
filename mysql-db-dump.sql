CREATE TABLE IF NOT EXISTS `user` (
                                      `id` INT UNSIGNED NOT NULL AUTO_INCREMENT,
                                      `username` VARCHAR(255) NOT NULL UNIQUE,
                                      `email` VARCHAR(255) NOT NULL UNIQUE,
                                      `password_hash` CHAR(60) NOT NULL,
                                      `country` ENUM('Turkey', 'United States', 'United Kingdom', 'France', 'Germany') NOT NULL,
                                      `level` INT NOT NULL DEFAULT 1,
                                      `coins` INT NOT NULL DEFAULT 5000,
                                      `created_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                      `updated_at` TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`)
);