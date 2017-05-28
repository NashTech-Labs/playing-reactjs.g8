# --- Sample dataset

# --- !Ups

insert into employee (id,name,address,designation) values (1,'Anand Kumar Singh', 'Knoldus Software', 'Sr. Software Consultant');
insert into employee (id,name,address,designation) values (2,'Supriya', 'Knoldus Software','Sr. Software Consultant');
insert into employee (id,name,address,designation) values (3,'Jyoti', 'Knoldus Software','Software Consultant');
insert into employee (id,name,address,designation) values (4,'Mayank', 'Knoldus Software','Sr. Software Consultant');

# --- !Downs

delete from employee;
