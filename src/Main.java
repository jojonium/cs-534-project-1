// Main class
// CS534 Assignment 1

public class Main {

    // empty constructor
    public Main() {}

    // Main method
    public static void main(String[] args) {

        Board b = new Board();
        b.generateBoard(5);

        // print initial board
        b.printBoard(5);

        System.out.println("The simple heuristic for this board is " + b.h(5));

    }
}
