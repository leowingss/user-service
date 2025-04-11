CREATE TABLE `profile` (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `description` varchar(255) NOT NULL,
                           `name` varchar(255) NOT NULL,
                           PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user` (
                        `id` bigint NOT NULL AUTO_INCREMENT,
                        `email` varchar(255) NOT NULL,
                        `first_name` varchar(255) NOT NULL,
                        `last_name` varchar(255) NOT NULL,
                        `password` varchar(255) NOT NULL,
                        `roles` varchar(255) NOT NULL,
                        PRIMARY KEY (`id`),
                        UNIQUE KEY `UKob8kqyqqgmefl0aco34akdtpe` (`email`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;

CREATE TABLE `user_profile` (
                                `id` bigint NOT NULL AUTO_INCREMENT,
                                `profile_id` bigint NOT NULL,
                                `user_id` bigint NOT NULL,
                                PRIMARY KEY (`id`),
                                KEY `FKqfbftbxicceqbmvj87g9be2qn` (`profile_id`),
                                KEY `FK6kwj5lk78pnhwor4pgosvb51r` (`user_id`),
                                CONSTRAINT `FK6kwj5lk78pnhwor4pgosvb51r` FOREIGN KEY (`user_id`) REFERENCES `user` (`id`),
                                CONSTRAINT `FKqfbftbxicceqbmvj87g9be2qn` FOREIGN KEY (`profile_id`) REFERENCES `profile` (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci

