-- SELECT * FROM Sailors S WHERE S.A = 900;
-- SELECT * FROM Sailors S WHERE S.A < 999;
-- SELECT * FROM Sailors S, Reserves R WHERE S.A < 20 AND R.J < 1000 AND R.L >= 20;
SELECT S2.C, R.I FROM Sailors S1, Reserves R, Sailors S2 WHERE S1.A = R.J AND R.J = S2.A AND S1.A = S2.A; 
-- SELECT S.A, R.I FROM Sailors S, Reserves R WHERE S.A = R.I; 
-- SELECT * FROM Sailors S WHERE S.A < 20;
-- SELECT * FROM Reserves R WHERE R.J < 1000 AND R.J >= 20;