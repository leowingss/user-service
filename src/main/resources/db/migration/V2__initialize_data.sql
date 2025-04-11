INSERT INTO user_service.user (id, email, first_name, last_name, password, roles) VALUES (1, 'yusuke@yuyu.hakusho', 'Yusuke', 'Urameshi', '{bcrypt}$2a$10$rHF9RST7UadphQ.7dyQm3eu3ScBlm6.6wMQlE1W2fYCIsgpaYxrCe', 'USER');
INSERT INTO user_service.user (id, email, first_name, last_name, password, roles) VALUES (10, 'kurosaki@devdojo.academy', 'Kurosaki', 'Ichigo', '', 'USER');
INSERT INTO user_service.user (id, email, first_name, last_name, password, roles) VALUES (11, 'goku.son@dbz.com', 'Goku', 'Son', '', 'USER');
INSERT INTO user_service.user (id, email, first_name, last_name, password, roles) VALUES (13, 'william.suane@devdojo.academy', 'william', 'suane', '{bcrypt}$2a$10$kksxG9GtwQ/HeBts0.1FhONogzZI9WIqv1e4bd3gEV1808f8wWfoG', 'ADMIN');
INSERT INTO user_service.profile (id, description, name) VALUES (1, 'Regular user with regular permissions', 'Regular User');
INSERT INTO user_service.profile (id, description, name) VALUES (2, 'Administrator', 'Admin');
INSERT INTO user_service.user_profile (id, profile_id, user_id) VALUES (1, 1, 1);
INSERT INTO user_service.user_profile (id, profile_id, user_id) VALUES (2, 1, 10);
INSERT INTO user_service.user_profile (id, profile_id, user_id) VALUES (3, 2, 11);
