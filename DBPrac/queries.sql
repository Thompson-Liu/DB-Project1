SELECT *
FROM Country;
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

-- SELECT A.A
-- FROM Sailors A, Sailors B
-- WHERE A.A<3;
-- SELECT A.A, B.B, R.H
-- FROM Sailors A, Sailors B, Reserves R
-- WHERE A.A<3 AND A.A = B.G
-- ORDER BY B.B;
-- SELECT DISTINCT Reserves.H
-- FROM Reserves, Sailors;



-- Piazza Question
-- SELECT  TEMP.maxper, TEMP.countryCode
-- FROM (SELECT CL.countrycode as countryCode, MAX(CL.Percentage)AS maxper
--   FROM CountryLanguage AS CL
--   GROUP BY CL.countrycode)AS TEMP, CountryLanguage
-- WHERE TEMP.countrycode=CountryLanguage.countrycode AND TEMP.maxper = CountryLanguage.Percentage;

-- Q4
-- SELECT CO.name, TEMP.count
-- FROM Country CO LEFT JOIN(
--   SELECT C.countrycode, COUNT(*) AS count
--   FROM City C
--   WHERE C.population>500000
--   GROUP BY C.countrycode) AS TEMP
-- ON CO.code = TEMP.countryCode;
 