-- Yuechen Zhao
-- 1126125
-- CSE 344 Homework2

-- Question 1: List the first and last names of all the actors who played in the movie
-- 'Officer 444'.
-- Actual output: 13 rows

SELECT a.fname AS FirstName, a.lname AS LastName
FROM Actor a, Casts c, Movie m
WHERE a.id = c.pid AND c.mid = m.id AND m.name = 'Officer 444';



-- Question 2: List all the directors who directed a 'Film-Noir' movie in a leap year.
-- Actual output: 113 rows

SELECT d.id AS DirectorID, d.fname AS D_Fname, d.lname AS D_Lname,
	m.name AS MovieName, m.year AS year
FROM Directors d, Movie_directors md, Movie m, Genre g
WHERE d.id = md.did AND md.mid = m.id AND md.mid = g.mid AND 
	g.genre = 'Film-Noir' AND m.year % 4 = 0;
	
	
	
-- Question 3: List all the actors who acted in a film before 1900 and also in a film
-- after 2000.
-- Actual output: 53 rows

-- Explanation: I ran the question 3 query and saw a tuple starting with 'Queen Alex'.
-- So I tried running
-- SELECT *
-- FROM Actor a, Casts c, Movie m
-- WHERE a.id = c.pid AND c.mid = m.id AND a.fname LIKE 'Queen Alex%';

-- Then I got the column for 'role' all showing 'Herself'. Then I tried many other queens
-- and kings, and they have the similar result. I think these people could be very
-- common-sense public figures (eg. Kings or Queens), so that directors added the King's
-- or Queen's image from old pictures into their movie, so kings or queens are all playing
-- themselves. This explains why they appear in a wide range of time.

SELECT a.fname, a.lname
FROM Actor a, Casts c1, Movie m1, Casts c2, Movie m2
where a.id = c1.pid AND c1.mid = m1.id AND a.id = c2.pid AND
	c2.mid = m2.id AND m1.year < 1900 AND m2.year > 2000
GROUP BY a.fname, a.lname;



-- Question 4: List all directors who directed 500 movies or more, in descending order
-- of the number of movies they directed.
-- Actual output: 47 rows

SELECT d.fname, d.lname, COUNT(DISTINCT md.mid)
FROM Directors d, Movie_directors md
WHERE d.id = md.did
GROUP BY d.fname, d.lname
HAVING COUNT(DISTINCT md.mid) >= 500
ORDER BY COUNT(DISTINCT md.mid) DESC;



-- Question 5: We want to find actors that played five or more roles in the same movie
-- during the year 2010.
-- Actual output: 24 rows

SELECT a.fname, a.lname, m.name, COUNT(DISTINCT role)
FROM Actor a, Casts c, Movie m
WHERE a.id = c.pid AND c.mid = m.id AND m.year = 2010
GROUP BY a.id, m.id
HAVING COUNT(DISTINCT role) >= 5;