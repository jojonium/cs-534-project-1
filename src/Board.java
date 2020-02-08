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
                    line = line.concat("  .  ");
                } else {
                    line = line.concat("  " + this.board[i][j] + "  ");
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

    /**
     * This method calculates the simple heuristic for the board (# queens attacking in/directly).
     * @param N (the dimension of the square board)
     */
    public int h(int N) {
        int h = 0;
        int[] queen_positions = this.getQueenPositions();

        for(int i = 0; i < N; i++) {
            int row_attack = this.attackRow(queen_positions[i], i, N);
            h += row_attack;
            int diag_attack = this.attackDiag(queen_positions[i], i, N);
            h += diag_attack;
        }

        return h;
    }

    /**
     * This method calculates how many queens a queen is attacking in the same row.
     * @param row (the row the queen is located in)
     *        col (the column the queen is located in)
     *        N (the dimension of the square board)
     * @return attacking (the number of queens being attacked)
     */
    private int attackRow(int row, int col, int N) {

        int attacking = 0;
        int[][] b = this.getBoard();

        for(int i = col; i < N; i++) {
            if (i != col && b[row][i] != 0) {
                attacking += 1;
            }
        }

        return attacking;
    }

    /**
     * This method calculates how many queens a queen is attacking on its diagonals.
     * @param row (the row the queen is located in)
     *        col (the column the queen is located in)
     *        N (the dimension of the square board)
     * @return attacking (the number of queens being attacked)
     */
    private int attackDiag(int row, int col, int N) {

        int attacking = 0;
        int[][] b = this.getBoard();
        int diag_row = row;

        // check positive-slope diagonal
        for(int i = col; i < (N-1); i++) {
            // early exit
            if(diag_row <= 0) { break; }

            if(b[diag_row-1][i+1] != 0) {
                attacking += 1;
            }
            diag_row -= 1;
        }

        // reset row
        diag_row = row;

        // check negative-slope diagonal
        for(int j = col; j < (N-1); j++) {
            // early exit
            if (diag_row >= (N-1)) { break; }

            if(b[diag_row+1][j+1] != 0) {
                attacking += 1;
            }
            diag_row += 1;
        }

        return attacking;
    }

}
