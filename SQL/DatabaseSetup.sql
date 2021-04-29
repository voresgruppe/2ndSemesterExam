CREATE TABLE [User] (
    [id] INT NOT NULL IDENTITY(1, 1) PRIMARY KEY,
    [username] VARCHAR(64) NOT NULL,
    [password] VARCHAR(128) NOT NULL,
    [isAdmin] BIT NOT NULL DEFAULT 0
);

CREATE TABLE [UserView] (
    [id] INT NOT NULL IDENTITY(1, 1) PRIMARY KEY,
    [userID] INT NOT NULL,
    [startX] INT NOT NULL,
    [startY] INT NOT NULL,
    [endX] INT NOT NULL,
    [endY] INT NOT NULL,
    [type] VARCHAR(64) NOT NULL,
    [source] VARCHAR(256) NOT NULL
    FOREIGN KEY ([userID]) REFERENCES [User](id)
);