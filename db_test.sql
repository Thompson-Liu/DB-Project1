-- testing JOIN
--1.
SELECT *
FROM Sailors, Boats;

--2. seq
SELECT *
FROM Boats, Sailors;

--3. 3join
SELECT *
FROM Sailors, Boats, Reserves;

--4. 4join
SELECT *
FROM Sailors, Boats, Reserves, Schedule;

--5. output with where
SELECT Sailors.A
FROM Sailors, Boats
WHERE Sailors.A=Boats.E;

--6. 2output
SELECT Sailors.A, Boats.E
FROM Sailors, Boats
WHERE Sailors.A=Boats.E;

--7. 3output
SELECT Sailors.A, Boats.E, Sailors.C
FROM Sailors, Boats
WHERE Sailors.A=Boats.E;

--8. sequence of select
SELECT Sailors.C, Sailors.A, Boats.E
FROM Sailors, Boats
WHERE Sailors.A=Boats.E;

--9. ALIAS
-- testing where with numbers
SELECT *
FROM Sailors S, Boats
WHERE Sailors.A>=2;

--10. alias but not in select or where
SELECT Sailors.A
FROM Sailors S, Boats
WHERE Sailors.S<1;

--11. alias and regular in where
SELECT Sailors.A
FROM Sailors S, Boats
WHERE Sailors.S=2 AND 3>1;

--12. alias only in where
SELECT Sailors.A
FROM Sailors S, Boats
WHERE S.A=3;

--13. alias only in SELECT
SELECT S.A
FROM Sailors S, Boats
WHERE Sailors.A=3;

--14. alias only in both
SELECT S.A
FROM Sailors S, Boats
WHERE S.A=3;

--15. alias using keyword AS
SELECT S.A
FROM Sailors AS S, Boats AS B
WHERE S.A=3;

--16. alias using keyword AS
SELECT S.A, B.A
FROM Sailors AS S, Boats B
WHERE S.A=3;

--17. alias self-join
SELECT *
FROM Sailors S, Sailors S2;

--18. outputing same columns twice 
SELECT B.A, B.A
FROM Sailors AS S, Boats B
WHERE S.A=3;

--19. outputing same columns twice
SELECT S.A, S.A
FROM Sailors AS S, Boats B
WHERE S.A=3;





-- Invalid Query not breaking the whole program test
SELECT *
FROM Sailors, Sailors

SELECT *
FROM Sailors S, Sailors

SELECT Reserves.A
FROM Sailors S, Boats



