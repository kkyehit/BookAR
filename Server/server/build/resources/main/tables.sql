create table books(
    id int auto_increment primary key,
    bookName varchar(20),
    authorName varchar(20),
    tableName varchar(20),
    state varchar(1) default "0",
    foreign key (tableName) references tb(name) on delete cascade
);
insert into books (bookName, authorName, tableName ) values ("test", "test", "test");

create table tb(
    name varchar(20) primary key,
    x varchar(20) default "0.0",
    y varchar(20) default "0.0",
    z varchar(20) default "0.0"
)
//state == 0 : 대여가능
//state == 1 : 대출중
//state == 2 : 반납완료