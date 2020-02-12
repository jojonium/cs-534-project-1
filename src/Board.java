// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.ArrayList;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.stream.IntStream;

public class Board {

    private int[] queenPositions; // holds row position of each queen (index = column)
    private int[] queenWeights; // holds weight of each queen (index = column)
    private int N; // holds dimension of square board

    public Board(int[] positions, int[] weights) {
        this.queenPositions = positions;
        this.queenWeights = weights;
        this.N = queenPositions.length;
    }

    public int[] getQueenPositions() { return queenPositions; }

    public int[] getWeights() { return queenWeights; }

    public int getN() { return N; }

    /**
     * This method prints the board.
     */
    public void printBoard() {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();
        int N = this.getN();

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
     * This method moves a queen on the board.
     * @param   col (the column the queen is in)
     *          new_row (the row to move to)
     *          positions (the positions of the queens)
     * @return  positions (the new queen positions)
     */
    public int[] move_queen(int col, int new_row, int[] positions) {
        positions[col] = new_row;
        return positions;
    }

    /**
     * This method calculates the first heuristic for the board.
     * @return h (the weight of the lightest queen, squared)
     */
    public int h1(int[] positions) {
        int[] queen_weights = this.getWeights();
        int N = this.getN();
        int lightest_index = positions[0]; // keeps track of where the lightest queen is located
        int lightest_queen = 10; // always heavier than heaviest queen
        boolean any_attacking = false; // tracks if any queens are attacking

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // calculate the # of queens this queen is attacking (in)directly
            int[] row_attack = this.attackRow(positions[i], i, positions);
            attacking += IntStream.of(row_attack).sum();
            int[] diag_attack = this.attackDiag(positions[i], i, positions);
            attacking += IntStream.of(diag_attack).sum();

            // update if we find a lighter attacking queen
            if (attacking > 0 && queen_weights[i] < lightest_queen) {
                any_attacking = true;
                lightest_index = i;
                lightest_queen = queen_weights[lightest_index];
            }
        }

//        System.out.println("The weight of the lightest queen(s) attacking is " + lightest_queen);
//        System.out.println("The value of h1 is: " + (int)(Math.pow(lightest_queen, 2)));

        // return h1 if any queens are attacking, 0 otherwise
        int h1 = (int)Math.pow(queen_weights[lightest_index], 2);
        if (any_attacking) {
            return h1;
        } else {
            return 0;
        }
    }

    /**
     * This method calculates the second heuristic for the board (sum of lightest queens attacking in/directly).
     * @return hl_value (the sum of the weights of the lighter queen for each pair)
     */
    public int h2(int[] positions) {
        int[] queen_weights = this.getWeights();
        int N = this.getN();
        int h2 = 0;
        int hl_value = 0; // lightest queen will always be bigger
        int hl_index;
        boolean any_attacking = false; // tracks if any queens are attacking

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // flag to check if we are lighter than each queen we attack
            boolean always_lighter = true;

            // calculate the # of queens this queen is row attacking (in)directly
            int[] row_attack = this.attackRow(positions[i], i, positions);
            attacking += IntStream.of(row_attack).sum();

            // calculate the # of queens this queen is diagonal attacking (in)directly
            int[] diag_attack = this.attackDiag(positions[i], i, positions);
            attacking += IntStream.of(diag_attack).sum();

            // consider each queen we attacked
            if (attacking > 0) {

                // update attacking flag
                any_attacking = true;

                for(int j = 0; j < N; j++) {
                    // compare this queen to each of the ones it is attacking
                    if (row_attack[j] == 1) {
                        h2 += Math.pow(Math.min(queen_weights[i], queen_weights[j]), 2);

                        // if we are heavier than the other queen, we aren't HL
                        if(queen_weights[i] > queen_weights[j]) {
                            always_lighter = false;
                        }
                    }

                    if (diag_attack[j] == 1) {
                        h2 += Math.pow(Math.min(queen_weights[i], queen_weights[j]), 2);

                        // if we are heavier than the other queen, we aren't HL
                        if(queen_weights[i] > queen_weights[j]) {
                            always_lighter = false;
                        }
                    }
                }

                // update (or add to) HL queens list based on the "heaviest-lightest" rule
                if (queen_weights[i] > hl_value && always_lighter) {
                    hl_index = i;
                    hl_value = queen_weights[hl_index];
                }
            }
        }

        // divide by 2 since we double-count above
//        System.out.println("The weight of the heaviest-lightest queen(s) attacking is " + hl_value);
//        System.out.println("The value of h2 is: " + (h2 / 2));

        // return h2 if any queens are attacking, 0 otherwise
        if (any_attacking) {
            return (h2 / 2);
        } else {
            return 0;
        }
    }

    /**
     * This method performs A* on the board.
     * @param h_to_use (1 for heuristic 1, 2 for heuristic 2)
     */
    public void aStar(int h_to_use) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
        int lowest_h; // keeps track of lowest h value

        // calculate original heuristic of the board based on which one we are using
        if (h_to_use == 1) {
            lowest_h = this.h1(queen_positions);
        } else {
            lowest_h = this.h2(queen_positions);
        }

        // create a priority queue
        PriorityQueue<Integer> queue = new PriorityQueue<>();

        // perform the AStar search, using the queue to decide which node to expand next
        while(lowest_h != 0) {
            // pop the front of the queue
            int current_queen = queue.poll();

            // consider the queen's possible moves
            int h_index = queen_positions[current_queen];
            for (int i = 0; i < N; i++) {

                // create a clone of the queen positions for each possible move
                int[] temp_positions = new int[queen_positions.length];
                System.arraycopy(queen_positions, 0, temp_positions, 0, queen_positions.length);

                // move the queen in the temporary board
                temp_positions = this.move_queen(current_queen, i, temp_positions);

                // store the move if it produces the lowest h value
                int new_h;
                if (h_to_use == 1) {
                    new_h = this.h1(temp_positions);
                } else {
                    new_h = this.h2(temp_positions);
                }

                if (new_h < lowest_h) {
                    lowest_h = new_h;
                    h_index = i;
                }
            }

            // move the queen
            this.move_queen(current_queen, h_index);

            // print the board
            System.out.println("++++++++++++++++++++++++++");
            this.printBoard();
        }
    }

    /**
     * This method performs hill climbing on the board.
     * @param h_to_use (1 for heuristic 1, 2 for heuristic 2)
     */
    public void hillClimb(int h_to_use) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
        int lowest_h; // keeps track of lowest h value

        ArrayList<int[]> sideways_moves = new ArrayList<>();
        int sideways_moves_left = 5; // tells us how many sideways moves we have remaining

        // create a clone of the queen positions for each new piece
        int[] original_positions = new int[this.getQueenPositions().length];
        System.arraycopy(this.getQueenPositions(), 0, original_positions, 0, this.getQueenPositions().length);

        while(true) {
            // calculate heuristic of the board based on which one we are using
            if (h_to_use == 1) {
                lowest_h = this.h1(queen_positions);
            } else {
                lowest_h = this.h2(queen_positions);
            }

            // we win if no queens are attacking
            if(lowest_h == 0) break;

            // keep track of whether we improved
            boolean improvement = false;

            // check possible moves we can make
            int current_queen = queen_positions[0];
            int h_index = queen_positions[0];
            for (int index = 0; index < N; index++) {

                // create a clone of the queen positions for each new piece
                int[] temp_positions = new int[this.getQueenPositions().length];
                System.arraycopy(this.getQueenPositions(), 0, temp_positions, 0, this.getQueenPositions().length);

                // for each queen, consider each of its moves
                for (int j = 0; j < N; j++) {
                    // skip over the position the queen is already in
                    if (j != queen_positions[index]) {
                        temp_positions = this.move_queen(index, j, temp_positions);

                        // move the queen that produces the lowest heuristic value
                        int new_h;
                        if (h_to_use == 1) {
                            new_h = this.h1(temp_positions);
                        } else {
                            new_h = this.h2(temp_positions);
                        }

                        if (new_h < lowest_h) {
                            lowest_h = new_h;
                            current_queen = index;
                            h_index = j;
                            improvement = true;
                            sideways_moves.clear();
                        } else if (new_h == lowest_h) {
                            // add a sideways move given by (queen column, new queen row in that column)
                            int[] move_values = new int[]{index, j};
                            sideways_moves.add(move_values);
                        }
                    }
                }
            }

            // break if we didn't improve and are out of sideways moves
            if (!improvement && sideways_moves_left == 0) {
                break;
            } else if (!improvement && sideways_moves_left > 0) {
                // if we still have sideways moves remaining, perform a sideways move
                sideways_moves_left -= 1;
                Random rand = new Random();
                int[] selected_move_vals = sideways_moves.get(rand.nextInt(sideways_moves.size()));
                this.move_queen(selected_move_vals[0], selected_move_vals[1]);
                System.out.println("Performed a sideways move.");
            } else {
                // perform the move with the minimum h value
                this.move_queen(current_queen, h_index);
            }

            // clear sideways moves
            sideways_moves.clear();

            // print the board
            System.out.println("++++++++++++++++++++++++++");
            this.printBoard();
        }

    }

    /**
     * This method calculates how many queens a queen is attacking in the same row.
     * @param row (the row the queen is located in)
     *        col (the column the queen is located in)
     * @return attacking (the positions of the queens being attacked)
     */
    private int[] attackRow(int row, int col, int[] positions) {

        int N = this.getN();
        int[] attacking = new int[N];

        for(int i = 0; i < N; i++) {
            // check if the row is the same (and not self-attacking)
            if(i != col && positions[i] == row) {
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
     * @return attacking (the positions of the queens being attacked)
     */
    private int[] attackDiag(int row, int col, int[] positions) {

        int N = this.getN();
        int[] attacking = new int[N];

        for(int i = 0; i < N; i++) {
            // calculate if the row and column difference are the same (and not self-attacking)
            if( (i != col) && (Math.abs(positions[i] - row) == Math.abs(i - col)) ) {
                attacking[i] = 1;
            } else {
                attacking[i] = 0;
            }
        }

        return attacking;
    }

}
