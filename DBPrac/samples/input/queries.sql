SELECT * FROM Sailors, Reserves WHERE Sailors.A = Reserves.O;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.B = Reserves.M AND Reserves.J = Boats.F;
SELECT * FROM Sailors, Reserves, Boats WHERE Sailors.B = Reserves.M AND Reserves.J = Boats.F AND Sailors.B < 150 AND Boats.G > 1000;