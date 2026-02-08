CREATE TABLE EventType (
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
[Name] NVARCHAR(100) NOT NULL,
Active BIT NOT NULL DEFAULT 1,
CreatedOn DATETIME2 NOT NULL,
UpdatedOn DATETIME2
)
GO

CREATE TABLE [Event] (
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
Title NVARCHAR(200) NOT NULL,
EventTypeId INT NOT NULL,
Price DECIMAL(10,2) NOT NULL,
[Time] DATETIME2 NOT NULL,
[Location] NVARCHAR(500)  NOT NULL,
Active BIT NOT NULL DEFAULT 1,
CreatedOn DATETIME2 NOT NULL,
UpdatedOn DATETIME2,
FOREIGN KEY (EventTypeId) REFERENCES EventType(Id)
)
GO

CREATE TABLE Performer(
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
[Name] NVARCHAR(100) NOT NULL,
LastName NVARCHAR(100) NULL,
Active BIT NOT NULL DEFAULT 1,
CreatedOn DATETIME2 NOT NULL,
UpdatedOn DATETIME2
)
GO

CREATE TABLE EventPerformer (
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
EventId INT NOT NULL,
PerformerId INT NOT NULL,
FOREIGN KEY (EventId) REFERENCES [Event](Id),
FOREIGN KEY (PerformerId) REFERENCES Performer(Id)
)
GO

CREATE TABLE [dbo].[User](
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
Username NVARCHAR(50) NOT NULL,
PwdHash NVARCHAR(256) NOT NULL,
PwdSalt NVARCHAR(256) NOT NULL,
FirstName NVARCHAR(256) NOT NULL,
LastName NVARCHAR(256) NOT NULL,
Email NVARCHAR(256) NOT NULL,
Phone NVARCHAR(256) NULL,
[Role] INT NOT NULL DEFAULT 0,
Active BIT NOT NULL DEFAULT 1,
CreatedOn DATETIME2 NOT NULL,
UpdatedOn DATETIME2
)
GO

CREATE TABLE Reservation (
Id INT IDENTITY(1,1) NOT NULL PRIMARY KEY,
EventId INT NOT NULL,
TicketNumber INT NOT NULL,
UserId INT NOT NULL,
[Status] INT NOT NULL,
TotalPrice DECIMAL(10,2) NOT NULL,
CreatedOn DATETIME2 NOT NULL,
Active BIT NOT NULL DEFAULT 1,
UpdatedOn DATETIME2
FOREIGN KEY (EventId) REFERENCES [Event](Id),
FOREIGN KEY (UserId) REFERENCES [User](Id)
)
GO

CREATE TABLE LogEntry (
[Id] [int] IDENTITY(1,1) NOT NULL PRIMARY KEY,
CreatedOn [datetime2](7) NOT NULL,
[Level] [int] NOT NULL,
[Message] [nvarchar](3000) NOT NULL
)
GO

INSERT INTO EventType ([Name], Active, CreatedOn) VALUES 
('Festival', 1, SYSDATETIME()),
('Koncert', 1, SYSDATETIME()),
('Konferencija', 1, SYSDATETIME()),
('Radionica', 1, SYSDATETIME()),
('Seminar', 1, SYSDATETIME()),
('Izložba', 1, SYSDATETIME()),
('Kazalište', 1, SYSDATETIME()),
('Film', 1, SYSDATETIME()),
('Sajam', 1, SYSDATETIME()),
('Predavanje', 1, SYSDATETIME()),
('Stand-up', 1, SYSDATETIME()),
('Sport', 1, SYSDATETIME());

INSERT INTO Performer ([Name], LastName, Active, CreatedOn) VALUES 
('The', 'Weeknd', 1, SYSDATETIME()),
('Coldplay', NULL, 1, SYSDATETIME()),
('Imagine', 'Dragons', 1, SYSDATETIME()),
('Beyoncé', NULL, 1, SYSDATETIME()),
('Ed', 'Sheeran', 1, SYSDATETIME()),
('Rihanna', NULL, 1, SYSDATETIME()),
('Red', 'Hot Chili Peppers', 1, SYSDATETIME()),
('Maroon', '5', 1, SYSDATETIME()),
('Adele', NULL, 1, SYSDATETIME()),
('Kendrick', 'Lamar', 1, SYSDATETIME()),
('Billie', 'Eilish', 1, SYSDATETIME()),
('Foo', 'Fighters', 1, SYSDATETIME()),
('Taylor', 'Swift', 1, SYSDATETIME()),
('Dua', 'Lipa', 1, SYSDATETIME()),
('Post', 'Malone', 1, SYSDATETIME()),
('Travis', 'Scott', 1, SYSDATETIME()),
('Lizzo', NULL, 1, SYSDATETIME()),
('Shakira', NULL, 1, SYSDATETIME()),
('Green', 'Day', 1, SYSDATETIME()),
('Bruno', 'Mars', 1, SYSDATETIME()),
('Lady', 'Gaga', 1, SYSDATETIME()),
('Kanye', 'West', 1, SYSDATETIME()),
('Justi', 'Timberlake', 1, SYSDATETIME()),
('Ariana', 'Grande', 1, SYSDATETIME()),
('Drake', NULL, 1, SYSDATETIME()),
('Metallica', NULL, 1, SYSDATETIME()),
('Elon', 'Musk', 1, SYSDATETIME()),
('Sheryl', 'Sandberg', 1, SYSDATETIME()),
('Satya', 'Nadella', 1, SYSDATETIME()),
('Sundar', 'Pichai', 1, SYSDATETIME()),
('Tim', 'Cook', 1, SYSDATETIME()),
('Melinda', 'Gates', 1, SYSDATETIME()),
('Jack', 'Ma', 1, SYSDATETIME()),
('Susan', 'Wojcicki', 1, SYSDATETIME()),
('Reed', 'Hastings', 1, SYSDATETIME()),
('Marissa', 'Mayer', 1, SYSDATETIME()),
('Gordon', 'Ramsay', 1, SYSDATETIME()),
('Bob', 'Ross', 1, SYSDATETIME()),
('Serena', 'Williams', 1, SYSDATETIME()),
('Chris', 'Hadfield', 1, SYSDATETIME()),
('Brene', 'Brown', 1, SYSDATETIME()),
('Jamie', 'Oliver', 1, SYSDATETIME()),
('Shonda', 'Rhimes', 1, SYSDATETIME()),
('Mark', 'Rober', 1, SYSDATETIME()),
('Marie', 'Kondo', 1, SYSDATETIME()),
('Hans', 'Zimmer', 1, SYSDATETIME()),
('Daniel', 'Pink', 1, SYSDATETIME()),
('Angela', 'Duckworth', 1, SYSDATETIME()),
('Simon', 'Sinek', 1, SYSDATETIME()),
('Adam', 'Grant', 1, SYSDATETIME()),
('Malcolm', 'Gladwell', 1, SYSDATETIME()),
('Ai', 'Weiwei', 1, SYSDATETIME()),
('Yayoi', 'Kusama', 1, SYSDATETIME()),
('Banksy', NULL, 1, SYSDATETIME()),
('Damien', 'Hirst', 1, SYSDATETIME()),
('Jeff', 'Koons', 1, SYSDATETIME()),
('Ian', 'McKellen', 1, SYSDATETIME()),
('Helen', 'Mirren', 1, SYSDATETIME()),
('Patrick', 'Stewart', 1, SYSDATETIME()),
('Viola', 'Davis', 1, SYSDATETIME()),
('James', 'Earl Jones', 1, SYSDATETIME()),
('Leonardo', 'DiCaprio', 1, SYSDATETIME()),
('Meryl', 'Streep', 1, SYSDATETIME()),
('Tom', 'Hanks', 1, SYSDATETIME()),
('Scarlett', 'Johansson', 1, SYSDATETIME()),
('Denzel', 'Washington', 1, SYSDATETIME()),
('Richard', 'Branson', 1, SYSDATETIME()),
('Oprah', 'Winfrey', 1, SYSDATETIME()),
('Sara', 'Blakely', 1, SYSDATETIME()),
('Mark', 'Cuban', 1, SYSDATETIME()),
('Bill', 'Gates', 1, SYSDATETIME()),
('Jane', 'Goodall', 1, SYSDATETIME()),
('Neil', 'deGrasse Tyson', 1, SYSDATETIME()),
('Barack', 'Obama', 1, SYSDATETIME()),
('Malala', 'Yousafzai', 1, SYSDATETIME()),
('Kevin', 'Hart', 1, SYSDATETIME()),
('Dave', 'Chappelle', 1, SYSDATETIME()),
('Ali', 'Wong', 1, SYSDATETIME()),
('John', 'Mulaney', 1, SYSDATETIME()),
('Tiffany', 'Haddish', 1, SYSDATETIME()),
('Lionel', 'Messi', 1, SYSDATETIME()),
('LeBron', 'James', 1, SYSDATETIME()),
('Usain', 'Bolt', 1, SYSDATETIME()),
('Cristiano', 'Ronaldo', 1, SYSDATETIME());

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Ljetni beats festival', 1, 99.99, '2025-06-15 18:00:00', 'Ul. Vice Vukova 8, Zagreb', 1, SYSDATETIME()),
('Indie glazbeni festival', 1, 75.00, '2025-06-20 17:00:00', 'Jarunska ul. 5, Zagreb', 1, SYSDATETIME()),
('Jazz festivalska večer', 1, 80.00, '2025-06-25 18:30:00', 'Jarunska ul. 5, Zagreb', 1, SYSDATETIME()),
('Festival elektroničke glazbe', 1, 90.00, '2025-06-30 19:00:00', 'Jarunska ul. 5, Zagreb', 1, SYSDATETIME()),
('Rock & roll festival', 1, 85.00, '2025-07-05 18:00:00', 'Poljudsko šetalište 1, Split', 1, SYSDATETIME()),
('Ljetni vibes festival', 1, 70.00, '2025-07-10 17:00:00', 'Trg Riječke rezolucije 1, Rijeka', 1, SYSDATETIME()),
('Pop eksplozija festival', 1, 95.00, '2025-07-15 19:00:00', 'Ul. Vice Vukova 8, Zagreb', 1, SYSDATETIME()),
('Festival svjetla', 1, 60.00, '2025-07-20 20:00:00', 'Trg Ante Starčevića 1, Osijek', 1, SYSDATETIME()),
('Festival svjetske glazbe', 1, 100.00, '2025-07-25 18:30:00', 'Poljudsko šetalište 1, Split', 1, SYSDATETIME()),
('Urban beats festival', 1, 75.00, '2025-07-30 19:00:00', 'Jarunska ul. 5, Zagreb', 1, SYSDATETIME()),
('Festival narodne glazbe', 1, 55.00, '2025-08-05 17:00:00', 'Trg Riječke rezolucije 1, Rijeka', 1, SYSDATETIME()),
('Ljetni rock festival', 1, 90.00, '2025-08-10 18:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Festival harmonija', 1, 85.00, '2025-08-15 19:30:00', 'Maksimirska ul. 132, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Rock legende uživo', 2, 120.00, '2025-07-10 20:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Pop večer s Taylor Swift', 2, 130.00, '2025-07-15 20:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Hip-hop koncert', 2, 115.00, '2025-07-22 20:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Klasična koncertna večer', 2, 85.00, '2025-07-25 19:30:00', 'Trg Republike Hrvatske 15, Zagreb', 1, SYSDATETIME()),
('Rock koncert s Green Day', 2, 125.00, '2025-07-18 20:00:00', 'Trpimirova ul. 2, Rijeka', 1, SYSDATETIME()),
('Jazz večer uživo', 2, 95.00, '2025-07-28 19:00:00', 'Europske avenije 1, Osijek', 1, SYSDATETIME()),
('Pop zvijezde koncert', 2, 110.00, '2025-08-02 20:00:00', 'Ul. Vice Vukova 8, Zagreb', 1, SYSDATETIME()),
('Elektronički dance koncert', 2, 105.00, '2025-08-05 22:00:00', 'Poljudsko šetalište 1, Split', 1, SYSDATETIME()),
('Glazbena večer uživo', 2, 80.00, '2025-08-10 19:30:00', 'Trpimirova ul. 2, Rijeka', 1, SYSDATETIME()),
('Ljetna rock noć', 2, 90.00, '2025-08-12 20:00:00', 'Ul. Vice Vukova 8, Zagreb', 1, SYSDATETIME()),
('Pop eksplozija noć', 2, 95.00, '2025-08-15 21:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Hip-hop legende uživo', 2, 100.00, '2025-08-20 20:30:00', 'Jarunska ul. 5, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Konferencija tehnoloških inovatora', 3, 50.00, '2025-09-12 09:00:00', 'Riva 10, Rijeka', 1, SYSDATETIME()),
('Konferencija o liderstvu', 3, 55.00, '2025-09-15 08:30:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Obrazovna konferencija', 3, 45.00, '2025-09-18 09:00:00', 'Europske avenije 9, Osijek', 1, SYSDATETIME()),
('Konferencija poslovanja i inovacija', 3, 60.00, '2025-09-20 09:30:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Startup summit', 3, 65.00, '2025-09-22 09:00:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME()),
('Konferencija zdravlja i znanosti', 3, 55.00, '2025-09-25 10:00:00', 'Riva 10, Rijeka', 1, SYSDATETIME()),
('Globalni tehnološki forum', 3, 70.00, '2025-09-28 09:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Konferencija inovacijskih lidera', 3, 60.00, '2025-09-30 08:30:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME()),
('Konferencija financija i ekonomije', 3, 50.00, '2025-10-02 09:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Konferencija obrazovanja i karijera', 3, 55.00, '2025-10-05 09:00:00', 'Europske avenije 9, Osijek', 1, SYSDATETIME()),
('Konferencija tehnologije i umjetne inteligencije', 3, 65.00, '2025-10-08 09:00:00', 'Riva 10, Rijeka', 1, SYSDATETIME()),
('Summit poslovnog rasta', 3, 60.00, '2025-10-10 08:30:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Radionica kuhanja s Gordonom', 4, 30.00, '2025-08-05 14:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME()),
('Likovna radionica s Bobom Rossom', 4, 35.00, '2025-08-10 13:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME()),
('Kulinarska radionica s Jamiejem Oliverom', 4, 40.00, '2025-08-12 14:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME()),
('Umjetnička radionica s Marie Kondo', 4, 32.00, '2025-08-15 13:00:00', 'Korzo 12, Rijeka', 1, SYSDATETIME()),
('Plesna radionica s Markom Roberom', 4, 28.00, '2025-08-18 15:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME()),
('Radionica fotografije', 4, 25.00, '2025-08-20 10:00:00', 'Marmontova ul. 5, Split', 1, SYSDATETIME()),
('Radionica glazbene produkcije', 4, 30.00, '2025-08-22 14:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME()),
('Radionica joge', 4, 20.00, '2025-08-25 09:00:00', 'Europske avenije 7, Osijek', 1, SYSDATETIME()),
('Radionica kreativnog pisanja', 4, 25.00, '2025-08-28 10:00:00', 'Korzo 12, Rijeka', 1, SYSDATETIME()),
('Radionica programiranja za početnike', 4, 35.00, '2025-08-30 11:00:00', 'Vukovarska ul. 269D, Zagreb', 1, SYSDATETIME()),
('Fitness radionica', 4, 28.00, '2025-09-02 16:00:00', 'Poljudsko šetalište 1, Split', 1, SYSDATETIME()),
('Radionica glazbene kompozicije', 4, 30.00, '2025-09-05 14:00:00', 'Ilica 85, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Seminar osobnog razvoja', 5, 25.00, '2025-09-01 10:00:00', 'Trg Ante Starčevića 10, Osijek', 1, SYSDATETIME()),
('Motivacijski seminar s Brene Brown', 5, 30.00, '2025-09-05 09:30:00', 'Europske avenije 1, Osijek', 1, SYSDATETIME()),
('Poslovni seminar s Adamom Grantom', 5, 28.00, '2025-09-08 10:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Motivacijski seminar s Malcolmom Gladwellom', 5, 30.00, '2025-09-12 09:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Seminar zdravlja i dobrobiti', 5, 25.00, '2025-09-15 10:00:00', 'Riva 10, Rijeka', 1, SYSDATETIME()),
('Edukacijski seminar', 5, 22.00, '2025-09-18 09:00:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME()),
('Seminar financija', 5, 28.00, '2025-09-20 09:30:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Seminar liderstva', 5, 30.00, '2025-09-22 10:00:00', 'Europske avenije 1, Osijek', 1, SYSDATETIME()),
('Seminar tehnologije', 5, 35.00, '2025-09-25 09:00:00', 'Riva 10, Rijeka', 1, SYSDATETIME()),
('Seminar marketinga', 5, 28.00, '2025-09-28 10:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Seminar znanosti', 5, 25.00, '2025-10-01 09:30:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME()),
('Seminar inovacija', 5, 30.00, '2025-10-03 09:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Izložba moderne umjetnosti', 6, 15.00, '2025-07-20 11:00:00', 'Trg Republike Hrvatske 1, Zagreb', 1, SYSDATETIME()),
('Izložba fotografije', 6, 20.00, '2025-07-25 10:00:00', 'Korzo 28, Rijeka', 1, SYSDATETIME()),
('Izložba suvremene umjetnosti', 6, 25.00, '2025-07-28 11:00:00', 'Marmontova ul. 2, Split', 1, SYSDATETIME()),
('Izložba skulptura', 6, 18.00, '2025-07-30 10:00:00', 'Marmontova ul. 2, Split', 1, SYSDATETIME()),
('Izložba povijesne umjetnosti', 6, 20.00, '2025-08-02 11:00:00', 'Trg Republike Hrvatske 1, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Kazališna večer Shakespearea', 7, 40.00, '2025-10-15 19:00:00', 'Trg Gaje Bulata 1, Split', 1, SYSDATETIME()),
('Moderno plesno kazalište', 7, 50.00, '2025-10-20 19:30:00', 'Trg Republike Hrvatske 6, Zagreb', 1, SYSDATETIME()),
('Klasična kazališna predstava', 7, 45.00, '2025-10-25 19:00:00', 'Ul. Ivana Zajca 1, Rijeka', 1, SYSDATETIME()),
('Glazbena kazališna večer', 7, 48.00, '2025-10-28 19:30:00', 'Trg Republike Hrvatske 6, Zagreb', 1, SYSDATETIME()),
('Dramska večernja predstava', 7, 42.00, '2025-11-01 19:00:00', 'Trg Gaje Bulata 1, Split', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Premijera blockbustera', 8, 60.00, '2025-11-01 20:30:00', 'Branimirova ul. 29, Zagreb', 1, SYSDATETIME()),
('Premijera akcijskog filma', 8, 65.00, '2025-11-05 21:00:00', 'Poljička cesta 8, Split', 1, SYSDATETIME()),
('Romantična filmska večer', 8, 55.00, '2025-11-10 20:00:00', 'Savska cesta 41, Zagreb', 1, SYSDATETIME()),
('Premijera komedije', 8, 60.00, '2025-11-12 20:30:00', 'Krešimirova ul. 2, Rijeka', 1, SYSDATETIME()),
('Projekcija dramskog filma', 8, 50.00, '2025-11-15 19:00:00', 'Poljička cesta 8, Split', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Međunarodni sajam knjiga', 9, 10.00, '2025-09-18 09:00:00', 'Korzo 28, Rijeka', 1, SYSDATETIME()),
('Sajam startup tehnologija', 9, 15.00, '2025-09-20 10:00:00', 'Avenija Dubrovnik 15, Zagreb', 1, SYSDATETIME()),
('Sajam karijera', 9, 12.00, '2025-09-28 09:00:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME()),
('Sajam obrazovanja i karijera', 9, 14.00, '2025-09-28 09:00:00', 'Avenija Dubrovnik 15, Zagreb', 1, SYSDATETIME()),
('Trgovački sajam', 9, 18.00, '2025-10-05 10:00:00', 'Ulica Kneza Trpimira 2, Osijek', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Znanstveno predavanje s Neilom Tysonom', 10, 20.00, '2025-08-12 16:00:00', 'Savska cesta 25, Zagreb', 1, SYSDATETIME()),
('Astronomsko predavanje', 10, 18.00, '2025-08-18 17:00:00', 'Korzo 5, Rijeka', 1, SYSDATETIME()),
('Predavanje o liderstvu', 10, 22.00, '2025-08-20 16:00:00', 'Vukovarska ul. 207, Zagreb', 1, SYSDATETIME()),
('Predavanje o tehnologiji', 10, 25.00, '2025-08-25 15:00:00', 'Europske avenije 9, Osijek', 1, SYSDATETIME()),
('Povijesno predavanje', 10, 20.00, '2025-08-28 16:00:00', 'Domovinskog rata 2, Split', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Komičarska večer s Kevinom Hartom', 11, 35.00, '2025-09-25 21:00:00', 'Poljička cesta 8, Split', 1, SYSDATETIME()),
('Stand-up večer s Johnom Mulaneyjem', 11, 38.00, '2025-09-28 20:30:00', 'Savska cesta 41, Zagreb', 1, SYSDATETIME()),
('Komičarski specijal s Ali Wong', 11, 36.00, '2025-09-30 21:00:00', 'Krešimirova ul. 2, Rijeka', 1, SYSDATETIME()),
('Stand-up večer', 11, 32.00, '2025-10-02 20:00:00', 'Poljička cesta 8, Split', 1, SYSDATETIME()),
('Večer smijeha', 11, 35.00, '2025-10-05 21:00:00', 'Savska cesta 41, Zagreb', 1, SYSDATETIME());
GO

INSERT INTO [Event] (Title, EventTypeId, Price, [Time], [Location], [Active], CreatedOn) VALUES
('Utakmica Lige prvaka', 12, 150.00, '2025-10-10 18:30:00', 'Maksimirska cesta 128, Zagreb', 1, SYSDATETIME()),
('NBA utakmica', 12, 180.00, '2025-10-12 19:00:00', 'Ul. Zrinsko-frankopanska 210, Split', 1, SYSDATETIME()),
('Nogometna utakmica', 12, 140.00, '2025-10-15 17:30:00', 'Maksimirska cesta 128, Zagreb', 1, SYSDATETIME()),
('Tenisko prvenstvo', 12, 120.00, '2025-10-18 16:00:00', 'Perivoj kralja Tomislava 1, Osijek', 1, SYSDATETIME()),
('Atletsko natjecanje', 12, 110.00, '2025-10-20 15:00:00', 'Rujevica 10, Rijeka', 1, SYSDATETIME());
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(1,1),(1,2),(1,3),(2,4),(2,5),(3,6),(3,7),(4,8),(4,9),(5,10),
(5,11),(6,12),(6,13),(7,14),(7,15),(8,16),(8,17),(9,18),(9,19),(10,20),
(11,1),(12,2),(13,3);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(14,21),(14,22),(15,23),(15,24),(16,25),(16,26),(17,27),(17,28),(18,29),(18,30),
(19,31),(19,32),(20,33),(20,34),(21,35),(21,36),(22,37),(22,38),(23,39),(23,40),
(24,21),(25,22);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(26,41),(26,42),(27,43),(27,44),(28,45),(28,46),(29,47),(29,48),(30,49),(30,50),
(31,41),(32,42),(33,43),(34,44),(35,45),(36,46),(37,47);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(38,51),(38,52),(39,53),(39,54),(40,55),(40,56),(41,57),(41,58),(42,59),(42,60),
(43,51),(44,52),(45,53),(46,54),(47,55),(48,56),(49,57),(49,58);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(50,61),(50,62),(51,63),(51,64),(52,65),(52,66),(53,67),(53,68),(54,69),(54,70),
(55,61),(56,62),(57,63),(58,64),(59,65),(60,66),(61,67);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(62,71),(62,72),(63,73),(63,74),(64,75),(65,71),(66,72);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(67,76),(68,77),(69,78),(70,79),(71,80);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(72,81),(73,82),(74,83),(75,84),(76,42);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(77,45),(78,67),(79,68),(80,69),(81,70);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(82,70),(83,71),(84,72),(85,73),(86,74);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(87,75),(88,76),(89,77),(90,78),(91,79);
GO

INSERT INTO EventPerformer (EventId, PerformerId) VALUES
(92,80),(93,81),(94,82),(95,83),(96,84);
GO

INSERT INTO [User] (Username, PwdHash, PwdSalt, FirstName, LastName, Email, Phone, [Role], Active, CreatedOn)
VALUES ('admin', 'qA9DKZaQ9WN7ody4rzQjkr0tmCD2TBB6mt3Cq5tczQY=', 'D6KTNOWIibHjBgActzW6jQ==', 'admin', 'admin', 'admin@algebra.hr', '0994327290', 1, 1, SYSDATETIME())
INSERT INTO [User] (Username, PwdHash, PwdSalt, FirstName, LastName, Email, Phone, [Role], Active, CreatedOn)
VALUES ('dino', 'qA9DKZaQ9WN7ody4rzQjkr0tmCD2TBB6mt3Cq5tczQY=', 'D6KTNOWIibHjBgActzW6jQ==', 'dino', 'novosel', 'dino@algebra.hr', '0994327291', 0, 1, SYSDATETIME())


