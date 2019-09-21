SELECT *
FROM Sailors;
SELECT *
FROM Sailors
WHERE Sailors.A>3 AND Sailors.b<1000 AND 1<3;
-- SELECT Country.C
-- FROM Country;
-- SELECT *
-- FROM City;
-- SELECT City.cityid
-- FROM City;
-- SELECT Country.N
-- FROM Country
-- WHERE Country.C<2 AND Country.A;
SELECT Sailors.A
FROM Sailors;
-- SELECT S.A
-- FROM Sailors S;
-- SELECT *
-- FROM Sailors S
-- WHERE S.A < 3;
-- SELECT *
-- FROM Sailors, Reserves
-- WHERE Sailors.A = Reserves.G;
-- SELECT *
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A < S2.A;
-- SELECT DISTINCT R.G
-- FROM Reserves R;
-- SELECT *
-- FROM Sailors
-- ORDER BY Sailors.B;
SELECT DISTINCT Reserves.H
FROM Reserves, Sailors;
