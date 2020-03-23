create table books(
    id int auto_increment primary key,
    bookName varchar(20),
    authorName varchar(20),
    tableName varchar(20),
    state varchar(1) default "0",
    foreign key (table_name) references tb(name) on delete cascade
);
insert into books (bookName, authorName, tableName ) values ("test", "test", "test");

//state == 0 : 대여가능
//state == 1 : 대출중
//state == 2 : 반납완료