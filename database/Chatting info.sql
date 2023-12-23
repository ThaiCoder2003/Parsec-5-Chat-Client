CREATE DATABASE chat_info;

USE chat_info;

CREATE TABLE users
(
	username varchar(255),
    password varchar(255),
    constraint pk_users primary key (username)
);

CREATE TABLE chat_history
(
    sender varchar(255),
    receiver varchar(255),
    date varchar(255),
    content_type varchar(255),
    content varchar(255)
)