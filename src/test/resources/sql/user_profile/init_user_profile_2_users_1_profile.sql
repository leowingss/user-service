insert into user (id,email,first_name,last_name, roles, password) values (1,'leo@email.com','Leonardo','Santos', 'USER', '{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK');
insert into user (id,email,first_name,last_name, roles, password) values (2,'fulano@email.com','Fulano','Silva', 'USER', '{bcrypt}$2a$10$i3XHDbx392pMTnB0KPe6E.eQ9iChjt2haKEw2kQ5F1Alyc2xSb/FK');
insert into profile (id,description,name) values (1, 'Manages everything','Admin');
insert into user_profile (id,user_id,profile_id) values (1, 1, 1);
insert into user_profile (id,user_id,profile_id) values (2, 2, 1);