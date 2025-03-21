insert into user (id,email,first_name,last_name) values (1,'leo@email.com','Leonardo','Santos');
insert into user (id,email,first_name,last_name) values (2,'fulano@email.com','Fulano','Silva');
insert into profile (id,description,name) values (1, 'Manages everything','Admin');
insert into user_profile (id,user_id,profile_id) values (1, 1, 1);
insert into user_profile (id,user_id,profile_id) values (2, 2, 1);