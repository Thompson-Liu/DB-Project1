-- SELECT *
-- FROM Sailors;
-- SELECT *
-- FROM Sailors
-- WHERE Sailors.A>3 AND Sailors.b<1000 AND 1>3;
-- SELECT Country.C
-- FROM Country;
-- SELECT *
-- FROM City;
-- SELECT City.cityid
-- FROM City;
-- SELECT Country.N
-- FROM Country
-- WHERE Country.C<2 AND Country.A;
-- SELECT Sailors.A, Sailors.C, Sailors.B, *
-- FROM Sailors;
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
-- FROM Sailors A, Sailors B

SELECT A.A
FROM Sailors A, Sailors B
WHERE A.A<3;
-- SELECT DISTINCT Reserves.H
-- FROM Reserves, Sailors;




-- SELECT TEMP.countrycode
-- FROM (SELECT CL.countrycode as countryCode, MAX( COUNT(CL.language))AS count
--   FROM CountryLanguage AS CL
--   GROUP BY CL.countrycode)AS TEMP
-- WHERE TEMP.countrycode=CL.countrycode;