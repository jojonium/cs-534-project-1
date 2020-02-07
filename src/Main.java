// Main class
// CS534 Assignment 1

public class Main {

    // empty constructor
    public Main() {}

    // Main method
    public static void main(String[] args) {

        Board b = new Board();
        int[][] board = b.generateBoard(5);
        b.printBoard(board, 5);

    }
}
