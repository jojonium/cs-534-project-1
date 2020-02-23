===============================
How to Run Urban Planning
===============================
Our program is using Java, make sure you have Java installed.

If you have make installed:
	1) Run our Makefile
	   > make
	2) Run the program
	   > java UrbanPlan [board] [algorithm]
	   Where the board is the path of the file to test
	   Algorithm is either HC for hill climbing or GA for genetic algorithm

	   Example:  java UrbanPlan urban1.txt HC
		runs hill climbing on urban1.txt
	   Example2: java UrbanPlan urban2.txt GA
		runs a genetic algorithm on urban2.txt

Else:
	1) compile the program
	   > javac UrbanPlan.java
	2) Run the program
	   > java UrbanPlan [board] [algorithm]
	   Where the board is the path of the file to test
	   Algorithm is either HC for hill climbing or GA for genetic algorithm

	   Example:  java UrbanPlan urban1.txt HC
		runs hill climbing on urban1.txt
	   Example2: java UrbanPlan urban2.txt GA
		runs a genetic algorithm on urban2.txt