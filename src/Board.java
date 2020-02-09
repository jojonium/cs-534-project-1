// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.Random;
import java.util.stream.IntStream;

public class Board {

    private int[] queenPositions; // holds row position of each queen (index = column)
    private int[] queenWeights; // holds weight of each queen (index = column)

    public Board() {}

    public int[] getQueenPositions() {
        return queenPositions;
    }

    public int[] getWeights() {
        return queenWeights;
    }

    /**
     * This method places the queens on the board.
     * @param N (the size of the board, given by N x N).
     */
    public void generateBoard(int N) {
        this.queenPositions = new int[N];
        this.queenWeights = new int[N];

        // fill in the position and weight arrays (one for each column)
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            int rand_row = rand.nextInt(N);
            int rand_weight = rand.nextInt(9) + 1;
            this.queenPositions[i] = rand_row;
            this.queenWeights[i] = rand_weight;
        }

    }

    /**
     * This method prints the board.
     * @param   N (the size of the board, given by N x N).
     */
    public void printBoard(int N) {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();

        for(int i = 0; i < N; i++) {
            String line = "";
            for (int j = 0; j < N; j++) {
                if (queen_positions[j] != i) {
                    line = line.concat("  .  ");
                } else {
                    line = line.concat("  " + queen_weights[j] + "  ");
                }
            }
            System.out.println(line);
        }
    }

    /**
     * This method moves a queen on the board.
     * @param   col (the column the queen is in)
     *          new_row (the row to move to)
     */
    public void move_queen(int col, int new_row) {
        int[] queen_positions = this.getQueenPositions();
        queen_positions[col] = new_row;
    }

    /**
     * This method calculates the simple heuristic for the board (# queens attacking in/directly).
     * @param N (the dimension of the square board)
     */
    public int h(int N) {
        int h = 0;
        int[] queen_positions = this.getQueenPositions();

        // calculate how many queens we are attacking row- and diagonal-wise and add to h
        for(int i = 0; i < N; i++) {
            int[] row_attack = this.attackRow(queen_positions[i], i, N);
            h += IntStream.of(row_attack).sum();
            int[] diag_attack = this.attackDiag(queen_positions[i], i, N);
            h += IntStream.of(diag_attack).sum();
        }

        // divide by 2 since we double-count above
        return (h / 2);
    }

    /**
     * This method calculates the first heuristic for the board (lightest queen attacking in/directly).
     * @param N (the dimension of the square board)
     */
    public int h1(int N) {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();
        int lightest = queen_weights[0]; // is this okay?

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // calculate the # of queens this queen is attacking (in)directly
            int[] row_attack = this.attackRow(queen_positions[i], i, N);
            attacking += IntStream.of(row_attack).sum();
            int[] diag_attack = this.attackDiag(queen_positions[i], i, N);
            attacking += IntStream.of(diag_attack).sum();

            // update if we find a lighter attacking queen
            if (attacking > 0 && queen_weights[i] < lightest) {
                lightest = queen_weights[i];
            }
        }

        return lightest;
    }

    /**
     * This method calculates the second heuristic for the board (sum of lightest queens attacking in/directly).
     * @param N (the dimension of the square board)
     */
    public int h2(int N) {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();
        int h2 = 0;

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // calculate the # of queens this queen is row attacking (in)directly
            int[] row_attack = this.attackRow(queen_positions[i], i, N);
            attacking += IntStream.of(row_attack).sum();

            // sum for row attacks
            if (attacking > 0) {
                for(int j = 0; j < N; j++) {
                    // compare this queen to each of the ones it is attacking
                    if (row_attack[j] == 1) {
                        h2 += Math.min(queen_weights[i], queen_weights[j]);
                    }
                }
            }

            // calculate the # of queens this queen is diagonal attacking (in)directly
            int[] diag_attack = this.attackDiag(queen_positions[i], i, N);
            attacking += IntStream.of(diag_attack).sum();

            // sum for diagonal attacks
            if (attacking > 0) {
                for(int j = 0; j < N; j++) {
                    // compare this queen to each of the ones it is attacking
                    if (diag_attack[j] == 1) {
                        h2 += Math.min(queen_weights[i], queen_weights[j]);
                    }
                }
            }

        }

        // divide by 2 since we double-count above
        return (h2 / 2);
    }

    /**
     * This method calculates how many queens a queen is attacking in the same row.
     * @param row (the row the queen is located in)
     *        col (the column the queen is located in)
     *        N (the dimension of the square board)
     * @return attacking (the positions of the queens being attacked)
     */
    private int[] attackRow(int row, int col, int N) {

        int[] attacking = new int[N];
        int[] queen_positions = this.getQueenPositions();

        for(int i = 0; i < N; i++) {
            // check if the row is the same (and not self-attacking)
            if(i != col && queen_positions[i] == row) {
                attacking[i] = 1;
            } else {
                attacking[i] = 0;
            }
        }

        return attacking;
    }

    /**
     * This method calculates how many queens a queen is attacking on its diagonals.
     * @param row (the row the queen is located in)
     *        col (the column the queen is located in)
     *        N (the dimension of the square board)
     * @return attacking (the positions of the queens being attacked)
     */
    private int[] attackDiag(int row, int col, int N) {

        int[] queen_positions = this.getQueenPositions();
        int[] attacking = new int[N];

        for(int i = 0; i < N; i++) {
            // calculate if the row and column difference are the same (and not self-attacking)
            if( (i != col) && (Math.abs(queen_positions[i] - row) == Math.abs(i - col)) ) {
                attacking[i] = 1;
            } else {
                attacking[i] = 0;
            }
        }

        return attacking;
    }

    /**
     * This is the main method invoked.
     * @param args (the command-line arguments)
     */
    public static void main(String[] args) {

        Board b = new Board();
        b.generateBoard(5);

        // print initial board
        b.printBoard(5);

        System.out.println("The number of queens attacking is " + b.h(5));
        System.out.println("The weight of the lightest queen attacking is " + b.h1(5));
        System.out.println("The sum of the lightest attacking queen in each pair is " + b.h2(5));

    }

}
