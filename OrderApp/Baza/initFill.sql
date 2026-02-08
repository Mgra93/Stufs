CREATE DATABASE Cugomat
GO

USE Cugomat
GO

INSERT INTO Client([Name], Code, [Address], [Phone], [OIB], [Active], CreatedOn, LicenseExpiryTime, LocationSecret, WebPageUrl, VipDayActive, VipDayCode)
VALUES ('Caffe Bar Sunset', 'CBS', 'Zagrebačka ulica 204, Varaždin', '0911234567', '12345678901', 1, GETDATE(), DATEADD(YEAR, 1, GETDATE()), 'mojbar123', 'https://www.facebook.com/profile.php?id=61556095522110', 1, 1);

INSERT INTO [Role]([Name]) VALUES ('ROLE_ADMIN'), ('ROLE_WORKER'), ('ROLE_USER')

INSERT INTO [User] (Username, [Password], FirstName, LastName, Email, Phone, [RoleId], Active, CreatedOn)
VALUES ('pero', '$2a$10$HJb6D0XgOG7PXEskm8tIdOPMcIZWLOfxq11AMFomjfvkVpWdJ0cRK', 'Pero', 'Peric', 'pero.peric@gmail.com', '0994327290', 1, 1, GETDATE())

INSERT INTO [User] (Username, [Password], FirstName, LastName, Email, Phone, [RoleId], Active, CreatedOn)
VALUES ('dino', '$2a$10$HJb6D0XgOG7PXEskm8tIdOPMcIZWLOfxq11AMFomjfvkVpWdJ0cRK', 'Dino', 'Novosel', 'dino.novosel.93@gmail.com', '0994327291', 2, 1, GETDATE())


INSERT INTO [User] (Username, [Password], FirstName, LastName, Email, Phone, [RoleId], Active, CreatedOn)
VALUES ('nikola', '$2a$10$HJb6D0XgOG7PXEskm8tIdOPMcIZWLOfxq11AMFomjfvkVpWdJ0cRK', 'Nikola', 'Gundic', 'nikola.gundic@gmail.com', '0994327292', 3, 1, GETDATE())

INSERT INTO [User] (Username, [Password], FirstName, LastName, Email, Phone, [RoleId], Active, CreatedOn)
VALUES ('ana', '$2a$10$HJb6D0XgOG7PXEskm8tIdOPMcIZWLOfxq11AMFomjfvkVpWdJ0cRK', 'Ana', 'Anic', 'ana.anic@gmail.com', '0994327293', 3, 1, GETDATE())


INSERT INTO Category([Name], Active, CreatedOn, ClientId) 
VALUES
('Vina', 1, GETDATE(), 1),
('Topli napici', 1, GETDATE(), 1),
('Žestoka pića', 1, GETDATE(), 1),
('Piva', 1, GETDATE(), 1);

INSERT INTO [Product]([Name], Price, Active, CreatedOn, CategoryId, ClientId) 
VALUES
('Crno vino Merlot', 12.00, 1, GETDATE(), 1, 1),
('Bijelo vino Chardonnay', 11.00, 1, GETDATE(), 1, 1),
('Rose vino', 11.50, 1, GETDATE(), 1, 1),
('Crno vino Cabernet Sauvignon', 13.00, 1, GETDATE(), 1, 1),
('Bijelo vino Sauvignon Blanc', 12.50, 1, GETDATE(), 1, 1);

INSERT INTO [Product]([Name], Price, Active, CreatedOn, CategoryId, ClientId) 
VALUES
('Espresso', 1.50, 1, GETDATE(), 2, 1),
('Cappuccino', 2.00, 1, GETDATE(), 2, 1),
('Latte', 2.20, 1, GETDATE(), 2, 1),
('Zeleni čaj', 1.80, 1, GETDATE(), 2, 1),
('Topla čokolada', 2.50, 1, GETDATE(), 2, 1);

INSERT INTO [Product]([Name], Price, Active, CreatedOn, CategoryId, ClientId) 
VALUES
('Rakija šljiva', 3.50, 1, GETDATE(), 3, 1),
('Viski', 5.00, 1, GETDATE(), 3, 1),
('Gin', 4.50, 1, GETDATE(), 3, 1),
('Rum', 4.00, 1, GETDATE(), 3, 1),
('Votka', 3.50, 1, GETDATE(), 3, 1);

INSERT INTO [Product]([Name], Price, Active, CreatedOn, CategoryId, ClientId) 
VALUES
('Pivo lager', 2.00, 1, GETDATE(), 4, 1),
('Pilsner', 2.20, 1, GETDATE(), 4, 1),
('Craft IPA', 2.50, 1, GETDATE(), 4, 1),
('Stout', 2.80, 1, GETDATE(), 4, 1),
('Weissbier', 2.30, 1, GETDATE(), 4, 1);

SELECT * FROM [User]

SELECT * FROM RefreshToken

DELETE FROM RefreshToken
WHERE Id > 0

SELECT * FROM [Role]
SELECT * FROM [Product]
SELECT * FROM [User]

SELECT * FROM [Orders]

INSERT INTO Worker(ClientId, UserId)
VALUES(1, 1), (1,2)

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 34.50,0, 0, 'I1', 34.50, 1, 3);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (1, 1, 1), (1, 2, 1), (1, 3, 1);

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 27.00,0, 0, 'O2', 27.00, 1, 3);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (2, 4, 1), (2, 5, 1), (2, 6, 1);

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 4.20,0, 0, 'I2', 4.20, 1, 3);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (3, 7, 1), (3, 8, 1);

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 1.80, 0, 0, 'I3', 1.80, 1, 4);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (4, 9, 1);

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 6.00, 0, 0, 'O2', 6.00, 1, 4);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (5, 10, 1), (5, 11, 1);

INSERT INTO Orders(CreatedOn, FinalPrice, HasDiscount, [Status], TableCode, TotalPrice, ClientId, UserId)
VALUES(GETDATE(), 9.50, 0, 0, 'I5', 9.50, 1, 4);

INSERT INTO OrderProduct(OrderId, ProductId, [Quantity])
VALUES (6, 12, 1), (6, 13, 1);

SELECT * FROM [Product]
SELECT * FROM Worker


