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

        for (int i = 0; i < 5; i++) {
            System.out.println();
            // move queen in first column down one row (wraparound for last row)
            int position = b.getQueenPositions()[0];
            b.move_queen(0, position, (position + 1) % 5);

            // print updated board
            b.printBoard(5);
        }

    }
}
