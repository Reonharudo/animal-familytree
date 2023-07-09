-- insert initial test data
-- the IDs are hardcoded to enable references between further test data
-- negative IDs are used to not interfere with user-entered data and allow clean deletion of test data

DELETE
FROM owner
where id < 0;

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-20, 'Herry', 'Miller', 'test0502@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-21, 'Prince', 'Miller', null);

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-22, 'Arasaka', 'Miller', null);

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-23, 'Alice', 'Wonder', null);

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-24, 'Dio', 'Herbert', null);

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-25, 'Andrew', 'Wilson', 'andrew.wilson@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-26, 'Olivia', 'Davis', 'olivia.davis@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-27, 'David', 'Garcia', 'david.garcia@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-28, 'Sophia', 'Rodriguez', 'sophia.rodriguez@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-29, 'James', 'Martinez', 'james.martinez@gmail.com');

INSERT INTO OWNER (ID, FIRST_NAME, LAST_NAME, EMAIL)
VALUES (-30, 'Ava', 'Hernandez', 'ava.hernandez@gmail.com');

DELETE
FROM horse
where id < 0;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-10, 'Prinzessin Peach', 'eating mushrooms', '1980-10-10', 'FEMALE', null, null, -30)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-1, 'Wendy', null, '2000-12-12', 'FEMALE', -10, null, -30)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-2, 'Test Mother', 'The famous mother', '2020-09-10', 'FEMALE', -1, null, -22)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-3, 'Test Father', 'The famous father', '2022-09-10', 'MALE', null, null, -22)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-5, 'Test Son', 'I like horses', '2022-10-10', 'MALE', -2, -3, -20)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-6, 'Xi Kim', 'Currently watching dramas', '2020-11-11', 'FEMALE', -2, null, -29)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_Id)
VALUES (-7, 'Yuki Herbert', 'My hobby is singing', '2022-10-10', 'FEMALE', -2, null, -29)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-9, 'König Arthur I', 'My hobby is to reign', '1980-05-10', 'MALE', null, null, -24)
;

INSERT INTO horse (id, name, description, date_of_birth, sex, female_parent_id, male_parent_id, owner_id)
VALUES (-8, 'König Arthur II', 'Army of horses', '2000-10-10', 'MALE', null, -9, null)
;