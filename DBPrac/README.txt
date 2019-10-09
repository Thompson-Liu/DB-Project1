The top-level class of our code is the Interpreter class in the package named "controllers". 

The logical operators are in the folder of logical operators, and physical operators are in the file of physical operators.

The folder Operators contains Logical OperatorFactory which generates the query plan, and the class PhysicalPlanBuilder will generate the physical operators plan.

To read and write on Binary input, we have a folder of fileIO, which contains tupleReader and tupleWriter that operates on Binary files as well as human readable files.