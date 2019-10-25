-- SELECT *
-- FROM TestTwo
-- ORDER BY TestTwo.A, TestTwo.G;
-- SELECT *
-- FROM Reserves
-- ORDER BY Reserves.G


-- SELECT *
-- FROM Sailors;
-- SELECT Sailors.A
-- FROM Sailors;
-- SELECT Boats.F, Boats.D
-- FROM Boats;
-- SELECT Reserves.G, Reserves.H
-- FROM Reserves;
-- SELECT *
-- FROM Sailors
-- WHERE Sailors.B >= Sailors.C;
-- SELECT Sailors.A
-- FROM Sailors
-- WHERE Sailors.B >= Sailors.C
-- SELECT Sailors.A
-- FROM Sailors
-- WHERE Sailors.B >= Sailors.C AND Sailors.B < Sailors.C;
-- SELECT *
-- FROM Sailors, Reserves
-- WHERE Sailors.A = Reserves.G;

-- SELECT *
-- FROM Reserves, Boats
-- WHERE Reserves.H = Boats.D
-- ORDER BY Reserves.H;
-- SELECT *
-- FROM Sailors, Reserves, Boats
-- WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D;
-- SELECT *
-- FROM Sailors, Reserves, Boats
-- WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D AND Sailors.B < 150;
-- SELECT DISTINCT *
-- FROM Sailors;
-- SELECT *
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A < S2.A;
-- SELECT B.F, B.D
-- FROM Boats B
-- ORDER BY B.D;
-- SELECT *
-- FROM Sailors S, Reserves R, Boats B
-- WHERE S.A = R.G AND R.H = B.D
-- ORDER BY S.C;
-- SELECT DISTINCT *
-- FROM Sailors S, Reserves R, Boats B
-- WHERE S.A = R.G AND R.H = B.D
-- ORDER BY S.C;


--======================================================--
-- SELECT *
-- FROM Sailors, Reserves
-- WHERE Sailors.A = Reserves.G and Sailors.C = Reserves.H;


SELECT *
FROM testRelation0 as t0, testRelation1 as t1, testRelation2 as t2
WHERE t0.A = t1.D AND t0.B = t2.J

-- SELECT t0.A, t1.D
-- FROM testRelation0 as t0, testRelation1 as t1
-- WHERE t0.B = t1.E


-- SELECT *
-- FROM testRelation0 as t0, testRelation1 as t1, testRelation2 as t2
-- WHERE t0.A = t1.F AND t0.B = t2.I AND t1.E = 3

-- SELECT *
-- FROM Sailors, Reserves
-- WHERE Sailors.A = Reserves.G and Sailors.C = Reserves.H;
