// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.Random;

public class Board {

    private int[][] board; // holds the board values
    private int[] queenPositions; // holds row position of each queen (index = column)
    private int[] queenWeights; // holds weight of each queen (index = column)

    public Board() {}

    public int[][] getBoard() {
        return board;
    }

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
        this.board = new int[N][N];
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

        // now create the board using these values
        for (int j = 0; j < N; j++) {
            for (int i = 0; i < N; i++) {
                if (this.queenPositions[j] == i) {
                    this.board[i][j] = this.queenWeights[j];
                } else {
                    this.board[i][j] = 0;
                }
            }
        }
    }

    /**
     * This method prints the board.
     * @param   N (the size of the board, given by N x N).
     */
    public void printBoard(int N) {
        for(int i = 0; i < N; i++) {
            String line = "";
            for (int j = 0; j < N; j++) {
                if (this.board[i][j] == 0) {
                    line = line.concat(".");
                } else {
                    line = line.concat(String.valueOf(this.board[i][j]));
                }
            }
            System.out.println(line);
        }
    }

    /**
     * This method moves a queen on the board.
     * @param   col (the column the queen is in)
     *          old_row (the row before moving)
     *          new_row (the row after moving)
     */
    public void move_queen(int col, int old_row, int new_row) {
        // update new position
        int weight = this.queenWeights[col];
        this.board[new_row][col] = weight;
        this.board[old_row][col] = 0;
        this.queenPositions[col] = new_row;
    }

}
