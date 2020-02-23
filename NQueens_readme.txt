===============================
How to Run N-Queens
===============================
Our program is using Java, make sure you have Java installed.

If you have make installed:
	1) Run our Makefile
	   > make
	2) Run the program
	   > java Main [board] [algorithm] [heueristic]
	   Where the board is either the path of the file to run, or a number representing the size of a random board
	   Algorithm is either 1 for A* or 2 for Greedy Hill Climbing
	   Heuristic is H1 or H2

	   Example:  java Main test-board.csv 1 H1
		runs A* with heuristic 1 on test-board.csv
	   Example2: java Main 5 2 H2
		runs Greedy hill climbing with heuristic 2 on a random board of size 5

Else:
	1) compile the program
	   > javac Main.java
	2) Run the program
	   > java Main [board] [algorithm] [heueristic]
	   Where the board is either the path of the file to run, or a number representing the size of a random board
	   Algorithm is either 1 for A* or 2 for Greedy Hill Climbing
	   Heuristic is H1 or H2

	   Example:  java Main test-board.csv 1 H1
		runs A* with heuristic 1 on test-board.csv
	   Example2: java Main 5 2 H2
		runs Greedy hill climbing with heuristic 2 on a random board of size 5