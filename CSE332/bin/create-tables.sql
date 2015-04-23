-- Yuechen Zhao
-- 1126125
-- CSE 344 Homework 2

.header on
.mode column
PRAGMA foreign_keys=ON;

CREATE TABLE Actor(
	id INTEGER,
	fname VARCHAR(30),
	lname VARCHAR(30),
	gender VARCHAR(1),
	PRIMARY KEY (id)
);

CREATE TABLE Movie(
	id INTEGER,
	name VARCHAR(150),
	year INTEGER,
	PRIMARY KEY (id)
);

CREATE TABLE Directors(
	id INTEGER,
	fname VARCHAR(30),
	lname VARCHAR(30),
	PRIMARY KEY (id)
);

CREATE TABLE Casts(
	pid INTEGER REFERENCES Actor(id),
	mid INTEGER REFERENCES Movie(id),
	role VARCHAR(50)
);

CREATE TABLE Movie_directors(
	did INTEGER REFERENCES Directors(id),
	mid INTEGER REFERENCES Movie(id)
);

CREATE TABLE Genre(
	mid INTEGER,
	genre VARCHAR(50)
);

.import actor.txt Actor
.import directors.txt Directors
.import genre.txt Genre
.import movie.txt Movie
.import casts.txt Casts
.import movie_directors.txt Movie_directors