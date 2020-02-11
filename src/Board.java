// Board class for Heavy N-Queens
// CS534 Assignment 1

import java.util.ArrayList;
import java.util.Scanner;
import java.util.Random;
import java.util.stream.IntStream;

public class Board {

    private int[] queenPositions; // holds row position of each queen (index = column)
    private int[] queenWeights; // holds weight of each queen (index = column)
    private int N; // holds dimension of square board

    public Board() {}

    public int[] getQueenPositions() {
        return queenPositions;
    }

    public int[] getWeights() {
        return queenWeights;
    }

    public int getN() { return N; }

    /**
     * This method places the queens on the board based on a file specifying weights and positions.
     * @param queenPositions (the positions of the queens)
     *        queenWeights (the weights of the queens)
     */
    public void generateBoardFromFile(int[] queenPositions, int[] queenWeights) {
        this.N = queenPositions.length;
        this.queenPositions = new int[this.N];
        this.queenWeights = new int[this.N];

        // fill in the position and weight arrays (one for each column)
        for (int i = 0; i < N; i++) {
            this.queenPositions[i] = queenPositions[i];
            this.queenWeights[i] = queenWeights[i];
        }

    }

    /**
     * This method places the queens on the board.
     * @param N (the size of the board, given by N x N).
     */
    public void generateRandomBoard(int N) {
        this.N = N;
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
     * @return  new_positions (the new queen positions)
     */
    public int[] move_queen(int col, int new_row, int[] positions) {
        positions[col] = new_row;
        return positions;
    }

    /**
     * This method calculates the simple heuristic for the board (# queens attacking in/directly).
     * @param positions (the positions of the queens)
     */
    public int h(int[] positions) {
        int h = 0;
        int N = this.getN();

        // calculate how many queens we are attacking row- and diagonal-wise and add to h
        for(int i = 0; i < N; i++) {
            int[] row_attack = this.attackRow(positions[i], i);
            h += IntStream.of(row_attack).sum();
            int[] diag_attack = this.attackDiag(positions[i], i);
            h += IntStream.of(diag_attack).sum();
        }

        // divide by 2 since we double-count above
        return (h / 2);
    }

    /**
     * This method calculates the first heuristic for the board.
     * @return lightest_indices (indices of lightest queens attacking in/directly)
     */
    public ArrayList<Integer> h1() {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();
        int N = this.getN();
        ArrayList<Integer> lightest_indices = new ArrayList<>();
        int lightest_index; // keeps track of where the lightest queen is located
        int lightest_value = 10; // always heavier than heaviest queen

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // calculate the # of queens this queen is attacking (in)directly
            int[] row_attack = this.attackRow(queen_positions[i], i);
            attacking += IntStream.of(row_attack).sum();
            int[] diag_attack = this.attackDiag(queen_positions[i], i);
            attacking += IntStream.of(diag_attack).sum();

            // update if we find a lighter attacking queen
            if (attacking > 0 && queen_weights[i] < lightest_value) {
                lightest_indices.clear();
                lightest_index = i;
                lightest_value = queen_weights[lightest_index];
                lightest_indices.add(i);
            } else if (attacking > 0 && queen_weights[i] == lightest_value) {
                lightest_indices.add(i);
            }
        }

        System.out.println("The weight of the lightest queen(s) attacking is " + lightest_value);
        System.out.println("The value of h1 is: " + (int)(Math.pow(lightest_value, 2)));

        return lightest_indices;
    }

    /**
     * This method calculates the second heuristic for the board (sum of lightest queens attacking in/directly).
     * @return hl_indices (the indices of the heaviest queens that are lighter in all their pairs)
     */
    public ArrayList<Integer> h2() {
        int[] queen_positions = this.getQueenPositions();
        int[] queen_weights = this.getWeights();
        int N = this.getN();
        int h2 = 0;
        int hl_value = 0; // lightest queen will always be bigger
        int hl_index;
        ArrayList<Integer> hl_indices = new ArrayList<>();

        for(int i = 0; i < N; i++) {
            // initialize how many queens we are attacking
            int attacking = 0;

            // flag to check if we are lighter than each queen we attack
            boolean always_lighter = true;

            // calculate the # of queens this queen is row attacking (in)directly
            int[] row_attack = this.attackRow(queen_positions[i], i);
            attacking += IntStream.of(row_attack).sum();

            // calculate the # of queens this queen is diagonal attacking (in)directly
            int[] diag_attack = this.attackDiag(queen_positions[i], i);
            attacking += IntStream.of(diag_attack).sum();

            // consider each queen we attacked
            if (attacking > 0) {
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

                // finally, update HL queens list if are heavier, and were always the lightest in each pair
                if (queen_weights[i] > hl_value && always_lighter) {
                    hl_indices.clear();
                    hl_index = i;
                    hl_value = queen_weights[hl_index];
                    hl_indices.add(i);
                } else if (queen_weights[i] == hl_value && always_lighter) {
                    hl_indices.add(i);
                }
            }
        }

        // divide by 2 since we double-count above
        System.out.println("The weight of the heaviest-lightest queen(s) attacking is " + hl_value);
        System.out.println("The value of h2 is: " + (h2 / 2));

        return hl_indices;
    }

    /**
     * This method performs hill climbing on the board.
     * @param h_to_use (1 for heuristic 1, 2 for heuristic 2)
     */
    public void hillClimb(int h_to_use) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
        ArrayList<Integer> candidates; // keeps track of candidate queens for movement

        while(true) {
            // calculate heuristic of the board based on which one we are using
            if (h_to_use == 1) {
                candidates = this.h1();
            } else {
                candidates = this.h2();
            }
            System.out.println("Candidates for movement: " + candidates.toString());

            // we win if the candidate list is empty
            if(candidates.size() == 0) break;

            // keep track of whether we improved
            boolean improvement = false;

            // check possible moves we can make
            int current_queen = candidates.get(0);
            int h = this.h(queen_positions);
            int h_index = queen_positions[current_queen];
            for (Integer index : candidates) {

                // create a clone of the queen positions for each new piece
                int[] temp_positions = new int[this.getQueenPositions().length];
                System.arraycopy(this.getQueenPositions(), 0, temp_positions, 0, this.getQueenPositions().length);

                // for each minimum queen, consider each of its moves
                for (int j = 0; j < N; j++) {
                    temp_positions = this.move_queen(index, j, temp_positions);

                    // move the queen that produces the lowest h value
                    int new_h = this.h(temp_positions);
                    if (new_h < h) {
                        h = new_h;
                        h_index = j;
                        current_queen = index;
                        improvement = true;
                    }
                }
            }

            // break if we didn't improve
            if (!improvement) break;

            // perform the move with the minimum h value
            this.move_queen(current_queen, h_index);

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
    private int[] attackRow(int row, int col) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
        int[] attacking = new int[N];

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
     * @return attacking (the positions of the queens being attacked)
     */
    private int[] attackDiag(int row, int col) {

        int[] queen_positions = this.getQueenPositions();
        int N = this.getN();
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

        // start by making a new Board instance
        Board b = new Board();

        // get user input for heuristic
        Scanner scanner = new Scanner(System.in);

        System.out.println("Want a random board? (yes/no)");
        String random = scanner.nextLine();
        if (random.equals("yes")) {
            System.out.println("Enter dimension of the square board (>= 1): ");
            int N = Integer.parseInt(scanner.nextLine());
            b.generateRandomBoard(N);
        } else {
            // read queen positions and weights from file (hardcoded for now)
            int[] queenPositions = new int[]{0, 3, 4, 1, 0};
            int[] queenWeights = new int[]{4, 6, 3, 4, 7};
            b.generateBoardFromFile(queenPositions, queenWeights);
        }

        // prompt for desired heuristic
        System.out.println("Enter heuristic you want to use: ");
        int h = Integer.parseInt(scanner.nextLine());
        if (h != 1 && h != 2) {
            System.out.println("Invalid heuristic value");
        }

        // print initial board
        System.out.println("+++++ INITIAL BOARD +++++");
        b.printBoard();

        // perform hill climbing
        b.hillClimb(h);

    }

}
