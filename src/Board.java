// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.*;
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
     * @param positions (the queen positions on the board)
     */
    public void printBoard(int[] positions) {
        int[] queen_weights = this.getWeights();
        int N = this.getN();

        for(int i = 0; i < N; i++) {
            String line = "";
            for (int j = 0; j < N; j++) {
                if (positions[j] != i) {
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
        int lowest_total_cost; // keeps track of lowest heuristic value
        String best_board; // keeps track of our best board
        int nodes_expanded = 0; // keeps track of how many nodes we have expanded

        // keeps track of how much time has elapsed since we started
        long startTime = System.currentTimeMillis();

        // calculate original heuristic of the board based on which one we are using
        int h;
        if (h_to_use == 1) {
            h = this.h1(queen_positions);
        } else {
            h = this.h2(queen_positions);
        }

        // create a mapping of board string representations to AStar values
        // Example: "10230" represents [1, 0, 2, 3, 0] for each queen's row position on the board (index = column)
        Map<String,Integer> h_map = new HashMap<>();

        // initialize the mapping with the initial board and h value
        h_map.put(this.board_to_string(queen_positions), h);
        lowest_total_cost = h;
        best_board = this.board_to_string(queen_positions);

        // create a priority queue to keep track of which boards are the most promising
        // compares map entries by value to determine which board is at the front of the node
        PriorityQueue<Map.Entry<String, java.lang.Integer>> queue =
                new PriorityQueue<>(Map.Entry.comparingByValue());

        // initialize the queue with the initial board configuration
        queue.addAll(h_map.entrySet());

        // perform the AStar search, using the queue to decide which node to expand next
        while(queue.size() > 0) {

            // if the time elapsed is more than 10 seconds, break
            long currentTime = System.currentTimeMillis();
            if ( (currentTime - startTime) > 10000) break;

            // pop the front of the queue and convert key (a String as described at the definition of h_map) to an integer array
            Map.Entry<String, Integer> current_board = queue.poll();
            assert current_board != null;
            int[] new_positions = this.string_to_board(current_board.getKey());

            // increment the number of expanded nodes
            nodes_expanded += 1;

            // if h value is 0, we win
            if(h_to_use == 1) {
                if (this.h1(new_positions) == 0) break;
            } else {
                if (this.h2(new_positions) == 0) break;
            }

            // consider all possible board moves and add to queue
            for (int i = 0; i < N; i++) {

                // create a clone of the queen positions for each possible move
                int[] temp_positions = new int[new_positions.length];
                System.arraycopy(new_positions, 0, temp_positions, 0, new_positions.length);

                for (int j = 0; j < N; j++) {

                    if (j != new_positions[i]) {
                        // move the queen in the temporary board
                        temp_positions = this.move_queen(i, j, temp_positions);

                        // calculate the new h value
                        int new_h;
                        if (h_to_use == 1) {
                            new_h = this.h1(temp_positions);
                        } else {
                            new_h = this.h2(temp_positions);
                        }

                        // add these values to hash map and queue if they don't exist yet
                        String board_string = this.board_to_string(temp_positions);
                        if(!h_map.containsKey(board_string)) {
                            h_map.put(board_string, new_h);
                            queue.add(new AbstractMap.SimpleEntry<>(board_string, new_h));
                        }

                        // store best board we have
                        if (new_h < lowest_total_cost) {
                            lowest_total_cost = new_h;
                            best_board = this.board_to_string(temp_positions);
                        }
                    }
                }
            }

//            // print the board
//            System.out.println("++++++++++++++++++++++++++");
//            this.printBoard(new_positions);
        }

        // at the end, print final board and statistics
        long endTime = System.currentTimeMillis();
        System.out.println("Final board configuration:");
        int[] best_board_positions = this.string_to_board(best_board);
        this.printBoard(best_board_positions);
        this.print_statistics(startTime, endTime, best_board_positions, queen_positions, h_to_use, nodes_expanded);

    }

    /**
     * This method performs hill climbing on the board.
     * @param h_to_use (1 for heuristic 1, 2 for heuristic 2)
     */
    public void hillClimb(int h_to_use) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
        int lowest_h; // keeps track of lowest h value
        int nodes_expanded = 0; // keeps track of how many nodes we expanded

        ArrayList<int[]> sideways_moves = new ArrayList<>();
        int total_sideways_moves = 5; // keeps track of how many sideways moves are available at each iteration
        int sideways_moves_left = 5; // tells us how many sideways moves we have remaining

        // create a clone of the queen positions for each new piece
        int[] original_positions = new int[this.getQueenPositions().length];
        System.arraycopy(this.getQueenPositions(), 0, original_positions, 0, this.getQueenPositions().length);

        // keeps track of how much time has elapsed since we started
        long startTime = System.currentTimeMillis();

        while(true) {

            // if the time elapsed is more than 10 seconds, break
            long currentTime = System.currentTimeMillis();
            if ( (currentTime - startTime) > 10000) break;

            // increment the number of nodes expanded
            nodes_expanded += 1;

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

            // restart if we didn't improve and are out of sideways moves
            if (!improvement && sideways_moves_left == 0) {
                total_sideways_moves += 1;
                sideways_moves_left = total_sideways_moves;
                System.arraycopy(original_positions, 0, queen_positions, 0, this.getQueenPositions().length);
                //System.out.println("We got stuck --- starting over.");
            } else if (!improvement && sideways_moves_left > 0) {
                // if we still have sideways moves remaining, perform a sideways move
                sideways_moves_left -= 1;
                Random rand = new Random();
                // TODO sometimes get an IllegalArgumentException when rand.nextInt returns 0...need to check if sideways_moves is not empty?
                int[] selected_move_vals = sideways_moves.get(rand.nextInt(sideways_moves.size()));
                this.move_queen(selected_move_vals[0], selected_move_vals[1]);
                //System.out.println("Performed a sideways move.");
            } else {
                // perform the move with the minimum h value
                this.move_queen(current_queen, h_index);
            }

            // clear sideways moves
            sideways_moves.clear();
        }

        // at the end, print statistics
        long endTime = System.currentTimeMillis();
        System.out.println("Final board configuration:");
        this.printBoard(queen_positions);
        this.print_statistics(startTime, endTime, queen_positions, original_positions, h_to_use, nodes_expanded);

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

    /**
     * This method calculates the total cost of a solution by comparing the distances between the original and final positions.
     * @param final_positions (the final board configuration)
     *        original_positions (the original board configuration)
     * @return total_movement_cost (the total cost of movement for the given solution)
     */
    private int calculate_movement_cost(int[] final_positions, int[] original_positions) {
        int[] queen_weights = this.getWeights();

        // calculate the cost of movement
        int total_movement_cost = 0;
        for(int i = 0; i < N; i++) {
            total_movement_cost += (int)(Math.pow(queen_weights[i], 2)) * Math.abs(final_positions[i] - original_positions[i]);
        }

        return total_movement_cost;
    }

    /**
     * This method calculates the depth of the search tree.
     * @param final_positions (the final board configuration)
     *        original_positions (the original board configuration)
     * @return total_positions_different (the search tree depth, based on how many queens moved)
     */
    private int calculate_search_depth(int[] final_positions, int[] original_positions) {

        // calculate how many positions are different from the original board configuration
        int total_positions_different = 0;
        for(int i = 0; i < N; i++) {
            if (final_positions[i] != original_positions[i]) {
                total_positions_different += 1;
            }
        }

        return total_positions_different;
    }

    /**
     * This method converts an integer array representation of a board to a string representation.
     * @param positions (i.e. [0, 4, 0, 3, 2] = "04032")
     * @return pos_string (the string representation)
     */
    private String board_to_string(int[] positions) {
        StringBuilder pos_string = new StringBuilder();
        for (int position : positions) {
            pos_string.append(position).append(",");
        }
        // remove final comma
        return pos_string.toString().substring(0, pos_string.length() - 1);
    }

    /**
     * This method converts a string representation of a board to an integer array representation.
     * @param board_string (i.e. "04032" = [0, 4, 0, 3, 2])
     * @return positions (the integer array representation of the board)
     */
    private int[] string_to_board(String board_string) {
        String[] board_array = board_string.split(",");
        int length = board_array.length;
        int[] positions = new int[length];
        for(int i = 0; i < length; i++) {
            positions[i] = Integer.parseInt(board_array[i]);
        }

        return positions;
    }

    /**
     * This method prints statistics related to the simulation.
     * @param startTime (the starting time of the simulation)
     *        endTime (the ending time of the simulation)
     *        final_positions (the final board configuration)
     *        original_positions (the original board configuration)
     *        h_to_use (1 for first heuristic, 2 for second heuristic)
     *        nodes_expanded (the number of nodes we expanded in our search)
     */
    private void print_statistics(long startTime, long endTime, int[] final_positions, int[] original_positions, int h_to_use, int nodes_expanded) {
        long time_elapsed = endTime - startTime;
        System.out.println("Total time elapsed before finding a solution (milliseconds): " + time_elapsed);
        if (h_to_use == 1) {
            System.out.println("Final heuristic value of board: " + this.h1(final_positions));
        } else {
            System.out.println("Final heuristic value of board: " + this.h2(final_positions));
        }
        System.out.println("Total solution cost: " + this.calculate_movement_cost(final_positions, original_positions));
        System.out.println("Number of nodes expanded: " + nodes_expanded);
        System.out.println("Minimum search depth of tree: " + this.calculate_search_depth(final_positions, original_positions));
    }

}
