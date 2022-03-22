create table games.escape_the_room (id bigint not null, date_of_run date, duration_of_run datetime, participant_email varchar(255), run_number integer, score integer, time_of_run time, primary key (id)) engine=InnoDB
create table escape_the_room (id integer not null, day_of_run varchar(255), end_time varchar(255), groupid integer, score double precision, start_time varchar(255), primary key (id)) engine=InnoDB
create table escape_the_room (id integer not null, age integer not null, email varchar(128) not null, first_name varchar(64) not null, gender integer not null, last_name varchar(64) not null, primary key (id)) engine=InnoDB
alter table escape_the_room add constraint UK_pp5bcms217a0clxdnsyw15aos unique (email)
create table hibernate_sequence (next_val bigint) engine=InnoDB
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
insert into hibernate_sequence values ( 1 )
