CREATE SCHEMA IF NOT EXISTS GAMES;

CREATE TABLE GAMES.GAME ("ID" INTEGER not null primary key, "TITLE" VARCHAR(50) not null, "DESCRIPTIONHTML" VARCHAR(2000), "COVER" VARCHAR(200), "SCREENSHOT" VARCHAR(200));