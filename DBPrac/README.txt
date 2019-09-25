The top-level class of our code is the Interpreter class in the package named "controllers". 

In order to extract Join conditions from the WHERE clause, we used left-deep join. The structure looks like this:

((((sigmaA × sigma B) × sigma C) × sigma D) × ...)

where each "sigmaX" is a selection condition from the WHERE clause and "×" represents cross product. 

Further explanation on our JOIN logic can be found in comments in our code (at the top of the file EvaluateWhere.java). We also included a "project-writeup.pdf" file to illustrate our design and overall model structure. 