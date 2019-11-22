----------  ===============        P 4          =============== -----------------
SELECT DISTINCT S.A, R.G
FROM Sailors S, Boats B, Reserves R
WHERE S.B=R.G AND S.A = B.D AND R.H<> B.D AND R.H<100
ORDER BY S.A;

-- SELECT S.A FROM Sailors S, Reserves R
-- WHERE S.B = R.G AND R.H < 100 AND S.A >= 9050;

-- SELECT *
-- FROM testRelation3, testRelation2, testRelation1;
-- WHERE testRelation3.P = testRelation2.M AND testRelation2.M= testRelation1.J;



SELECT S.A
FROM Sailors S, Reserves R
WHERE S.B = R.G AND R.H<100 AND S.A>=9050;

SELECT *
FROM Reserves R, Sailors S
WHERE R.A = 100 AND R.A = S.B;

SELECT *
FROM Reserves R, Sailors S, testRelation1 T, testRelation2 U
WHERE R.A<>U.B AND R.A=S.B AND S.C=T.D AND R.A=2 AND T.D=T.X AND U.Y<>42;

----------------============================   P 1    ===========================-----------

-- SELECT *
-- FROM Reserves
-- WHERE Reserves.A<>Reserves.B AND Reserves.A=3;

-- SELECT *
-- FROM Sailors S
-- WHERE S.A =5 AND S.B=S.A;

-- SELECT *
-- FROM Reserves
-- WHERE Sailors.A = Reserves.G;

-- SELECT S.A, S.C, S.B
-- FROM Sailors S;

-- SELECT *
-- FROM Sailors S
-- ORDER BY S.B, S.C, S.A;

-- SELECT S.A
-- FROM Sailors S
-- WHERE S.A < 10000;

-- SELECT DISTINCT R.H
-- FROM Reserves R
-- WHERE R.G=1;

-- SELECT DISTINCT Sailors.A
-- FROM Sailors, Reserves
-- WHERE Sailors.A = Reserves.G;

-- SELECT *
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A = S2.A
-- ORDER BY S1.B,S1.C,S1.A;

-- SELECT S1.B
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A = S2.B;

-- SELECT *
-- FROM Sailors S1, Boats B, Sailors S2
-- WHERE S1.A =  S2.A AND S1.A=B.E AND B.E=S2.A AND S1.A>=8000;

-- SELECT *
-- FROM Sailors S1, Reserves R, Sailors S2
-- WHERE S1.A = R.G AND R.G=S2.A AND R.G=164;

-- SELECT S1.A
-- FROM Sailors S1, Reserves R, Sailors S2
-- WHERE S1.A = R.G AND R.G=S2.A AND S1.A>=9000;

-- SELECT Sailors.B
-- FROM Sailors
-- WHERE Sailors.A > 9000 AND 1>3;

-- SELECT Sailors.B
-- FROM Sailors
-- WHERE Sailors.A = 10000;

-- SELECT Sailors.B
-- FROM Sailors
-- WHERE Sailors.A > 9000;


-- SELECT R.G
-- FROM Reserves R
-- ORDER BY R.G;

-- SELECT DISTINCT Reserves.H
-- FROM Reserves;

-- SELECT DISTINCT S.A
-- FROM Sailors S, Boats B
-- WHERE S.A < 2;

-- SELECT *
-- FROM Reserves
-- ORDER BY Reserves.H;

-- SELECT DISTINCT S.C
-- FROM Sailors S
-- WHERE S.B>100
-- ORDER BY S.C;

-- SELECT *
-- FROM Sailors;


-- SELECT *
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A < S2.A;
-- SELECT DISTINCT R.G
-- FROM Reserves R;


-- SELECT A.A, B.B, R.H
-- FROM Sailors A, Sailors B, Reserves R
-- WHERE A.A<1000 AND A.A = B.G
-- ORDER BY B.B;





---------------------================   P2 SMJ BNLJ EXTERNAL SORT    =============----------
-- SELECT *
-- FROM Sailors S, Reserves R, Boats B
-- WHERE S.A = R.G AND R.H = B.D
-- ORDER BY S.C;
-- SELECT DISTINCT *
-- FROM Sailors S, Reserves R, Boats B
-- WHERE S.A = R.G AND R.H = B.D
-- ORDER BY S.C;
-- SELECT *
-- FROM Sailors, Reserves, Boats
-- WHERE Sailors.A = Reserves.G AND Reserves.H = Boats.D
-- ORDER BY Sailors.A, Reserves.H;



-------------------===============   P3   test index scan   ==================-----------------
-- SELECT *
-- FROM Sailors, Boats B
-- WHERE Sailors.A>9000 AND Boats.D<50;

-- SELECT *
-- FROM Sailors
-- WHERE Sailors.A!=10000 ;

-- SELECT *
-- FROM testRelation0
-- WHERE testRelation0.B<=910 AND testRelation0.C>=600 OR testRelation0.C!=600 
-- AND testRelation0.A<800;


-- SELECT testRelation0.A, testRelation0.C
-- FROM testRelation0
-- WHERE testRelation0.A > 500 OR testRelation0.B < 100;


-- SELECT *
-- FROM testRelation0 t0, testRelation1 t1
-- WHERE t0.A > 200 AND t1.E > 400 AND t0.A <= t1.F;


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

-- SELECT *
-- FROM Sailors S, Boats B
-- WHERE Sailors.A=3202 AND B.D<50
-- ORDER BY S.A, S.B;