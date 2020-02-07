// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.Random;

public class Board {

    public Board() {}

    /**
     * This method places the queens on the board.
     * @param N (the size of the board, given by N x N).
     * @return board (the starting configuration of the board)
     */
    public int[][] generateBoard(int N) {
        int[][] board = new int[N][N];
        int[] positions = new int[N]; // holds positions to place queens
        int[] weights = new int[N]; // holds weights for each queen

        // fill in the position and weight arrays (one for each column)
        Random rand = new Random();
        for (int i = 0; i < N; i++) {
            int rand_row = rand.nextInt(N);
            int rand_weight = rand.nextInt(9) + 1;
            positions[i] = rand_row;
            weights[i] = rand_weight;
        }

        // now create the board using these values
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                if (positions[j] == i) {
                    board[i][j] = weights[j];
                } else {
                    board[i][j] = 0;
                }
            }
        }

        return board;
    }

    /**
     * This method prints the board.
     * @param   board (the board configuration)
     *          N (the size of the board, given by N x N).
     * @return  Nothing
     */
    public void printBoard(int[][] board, int N) {
        for(int i = 0; i < 5; i++) {
            String line = "";
            for (int j = 0; j < 5; j++) {
                if (board[i][j] == 0) {
                    line = line.concat(".");
                } else {
                    line = line.concat(String.valueOf(board[i][j]));
                }
            }
            System.out.println(line);
        }
    }

}
