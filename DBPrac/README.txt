Push Select Conditions:

Refactor logical and physical query plans:
We refactored our logicalOperatorFactory to construct query plans.

TableStas: 
we refactored our catalog to maintain more information of join tuple.

Join Optimizer:
We compute the cost of join in Dynamic Programing in bottom-up sequence. The data structure we use
