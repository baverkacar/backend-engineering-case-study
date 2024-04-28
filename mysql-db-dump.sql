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