SELECT *
FROM Sailors S
WHERE S.A<=10000 AND S.A>=10000;

-- SELECT *
-- FROM Sailors, Boats B
-- WHERE Sailors.A>9000 AND Boats.D<50;
SELECT *
FROM Sailors
WHERE Sailors.A<10000 AND Sailors.A>=10000;

-- SELECT *
-- FROM testRelation0
-- WHERE testRelation0.B<=910 AND testRelation0.C>=600 OR testRelation0.C!=600 AND testRelation0.A<800;


-- SELECT testRelation0.A, testRelation0.C
-- FROM testRelation0
-- WHERE testRelation0.A > 500 OR testRelation0.B < 100;


-- SELECT *
-- FROM testRelation0 t0, testRelation1 t1
-- WHERE t0.A > 200 AND t1.E > 400 AND t0.A <= t1.F;

SELECT *
FROM Sailors S, Boats B
WHERE S.A=3202 AND S.A>=400 AND B.D<50
ORDER BY S.A, S.B;

-- SELECT *
-- FROM Sailors S, Boats
-- WHERE S.A=3202 AND S.A>=400 AND Boats.D<50
-- ORDER BY Sailors.A, Sailors.B;

-- SELECT *
-- FROM Sailors
-- WHERE Sailors.A=3202
-- ORDER BY Sailors.A;

-- SELECT *
-- FROM Sailors
-- WHERE Sailors.A<=10000 AND Sailors.A>=10000;

SELECT *
FROM Sailors
WHERE Sailors.A >= 5000 AND Sailors.B < Sailors.C