CREATE DATABASE CRUD;
USE CRUD;

CREATE TABLE PROFILE_ (
USERNAME VARCHAR(40) PRIMARY KEY,
PASSWORD_ VARCHAR(40),
EMAIL VARCHAR(40) UNIQUE ,
USER_CODE INT AUTO_INCREMENT UNIQUE,
NAME_ VARCHAR(40),
TELEPHONE VARCHAR(9),
CONSTRAINT CHK_TELEPHONE
        CHECK (TELEPHONE REGEXP '^[0-9]{9}$'),
SURNAME VARCHAR(40)
);

CREATE TABLE USER_ (
USERNAME VARCHAR(40) PRIMARY KEY,
GENDER VARCHAR(40),
CARD_NUMBER VARCHAR(24),
CONSTRAINT CHK_CARD_NUMBER
        CHECK (CARD_NUMBER REGEXP '^[A-Z]{2}[0-9]{22}$'),
FOREIGN KEY (USERNAME) REFERENCES PROFILE_ (USERNAME)
);

CREATE TABLE ADMIN_ (
USERNAME VARCHAR(40) PRIMARY KEY,
CURRENT_ACCOUNT VARCHAR(40),
FOREIGN KEY (USERNAME) REFERENCES PROFILE_ (USERNAME)
);

INSERT INTO PROFILE_ (USERNAME, PASSWORD_, EMAIL, USER_CODE, NAME_, TELEPHONE, SURNAME)
VALUES
('jlopez', 'pass123', 'jlopez@example.com', 101, 'Juan', '987654321', 'Lopez'),
('mramirez', 'pass456', 'mramirez@example.com', 102, 'Maria', '912345678', 'Ramirez'),
('cperez', 'pass789', 'cperez@example.com', 103, 'Carlos', '934567890', 'Perez'),
('asanchez', 'qwerty', 'asanchez@example.com', 104, 'Ana', '900112233', 'Sanchez'),
('rluna', 'zxcvbn', 'rluna@example.com', 105, 'Rosa', '955667788', 'Luna');

INSERT INTO USER_ (USERNAME, GENDER, CARD_NUMBER)
VALUES
('jlopez', 'Masculino', 'AB1234567890123456789012'),
('mramirez', 'Femenino', 'ZX9081726354891027364512'),
('cperez', 'Masculino', 'LM0011223344556677889900');

INSERT INTO ADMIN_ (USERNAME, CURRENT_ACCOUNT)
VALUES
('asanchez', 'CTA-001'),
('rluna', 'CTA-002');

CREATE TABLE COMPANY_(
Url text,
C_name varchar(30),
NIF varchar(9)not null primary key,
location text);

CREATE TABLE PRODUCT_(
Price double,
Product_type enum('Cloth', 'Shoe'),
Descript text,
Product_ID varchar(15) not null primary key,
Img text,
NIF varchar(9)not null,
foreign key (NIF) references COMPANY_(NIF)
);

CREATE TABLE SIZE_(
Stock int,
label varchar (10),
Product_ID varchar(15) not null,
foreign key (Product_ID) references PRODUCT_(Product_ID));

Insert into COMPANY_ (Url, C_name, NIF, location) VALUES
("https://www.youtube.com/watch?v=2NbBi5I7DB8", "Yara", "123456789", "Palermo, Sizilia"),("https://www.youtube.com/watch?v=2NbBi5I7DB8", "Ñoldan", "321654987", "Wailuku, Hawái");

Insert into PRODUCT_ (Price, Product_type, Descript, Product_ID, Img, NIF) Values
(15.99, "Cloth", "a blu shert","aaa111","/src/images/baldinkent.png","123456789"),(20.99, "Shoe", "a blu sue","111aaa","/src/images/imgYara_shoe.png","123456789"),
(14.59, "Cloth", "a cul shert", "bbb222","/src/images/Negra_tengo_el_alma.png","321654987"),(14.59, "Shoe", "a cul sue", "222bbb","/src/images/Ñoldan.png","321654987");

Insert into SIZE_(Stock,label,Product_ID)Values
(0,"XXL","aaa111"),(4,"XL","aaa111"),(5,"L","aaa111"),(8,"M","aaa111"),(9,"S","aaa111"),(0,"XS","aaa111"),
(2,"XXL","aaa111"),(0,"XL","aaa111"),(3,"L","aaa111"),(4,"M","aaa111"),(6,"S","aaa111"),(0,"XS","aaa111"),
(2,"XXL","aaa111"),(0,"XL","aaa111"),(0,"L","aaa111"),(7,"M","aaa111"),(0,"S","aaa111"),(9,"XS","aaa111"),
(0,"XXL","aaa111"),(0,"XL","aaa111"),(3,"L","aaa111"),(7,"M","aaa111"),(0,"S","aaa111"),(1,"XS","aaa111");
