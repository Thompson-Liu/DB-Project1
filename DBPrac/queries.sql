<<<<<<< HEAD
SELECT DISTINCT S.A
FROM Sailors S
ORDER BY S.B
WHERE Reserves.A <=3;
=======
-- SELECT *
-- FROM Sailors;
-- SELECT Sailors.A
-- FROM Sailors;
-- SELECT S.A
-- FROM Sailors S;
-- SELECT * FROM Sailors S WHERE S.A < 3;
SELECT *
FROM Sailors, Reserves
WHERE Sailors.A = Reserves.G;
-- SELECT *
-- FROM Sailors S1, Sailors S2
-- WHERE S1.A < S2.A;
-- SELECT DISTINCT R.G
-- FROM Reserves R;
-- SELECT *
-- FROM Sailors
-- ORDER BY Sailors.B;
>>>>>>> 88f8ccc9570a72aac926d5c6efd35fac983d8698
