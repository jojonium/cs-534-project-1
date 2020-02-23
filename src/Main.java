// Main class for Heavy N-Queens
// CS534 Assignment 1

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.File;
import java.util.ArrayList;
import java.util.Random;

public class Main {
	/**
	 * parses command-line arguments to get parameters for number of queens, which
	 * algorithm to use, and which heuristic function to use
	 */
	public static void main(String[] args) {
		int size;
		int[] queenPositions = null;
		int[] queenWeights = null;
		if (args.length < 3) {
			showUsage();
			System.exit(1);
			return;
		}

		try {
			// first see if the first argument is a number
			size = Integer.parseInt(args[0]);
		} catch (NumberFormatException nfe) {
			// first arg is not a number, see if it's a filename
			ArrayList<String> fileLines = new ArrayList<>();
			try {
				File file = new File(args[0]);
				BufferedReader br = new BufferedReader(new FileReader(file));
				String line;
				while ((line = br.readLine()) != null) {
					fileLines.add(line);
				}
				br.close();

				if (fileLines.size() < 1) {
					throw new Exception("File has no lines");
				}
				size = fileLines.size();
				// assume the board is a square
				queenPositions = new int[size];
				queenWeights = new int[size];

				// parse the file to get the board
				int row = 0;
				for (String l : fileLines) {
					int col = 0;
					for (char c : l.toCharArray()) {
						if (c == ',') {
							col++;
						} else if (c >= 48 && c <= 57) {
							// is a number
							queenPositions[col] = row;
							queenWeights[col] = c - 48;
						}
					}
					row++;
				}
			} catch (Exception e) {
				// got an error while trying to read the file
				System.err.println(e.toString());
				showUsage();
				System.exit(1);
				return;
			}
		}

		try {
			if (!args[1].equals("1") && !args[1].equals("2"))
				throw new Exception("algorithm must be '1' or '2'");
			final String algorithm = (args[1].equals("1")) ? "A*" : "Greedy hill climbing";
			if (!args[2].equals("H1") && !args[2].equals("H2"))
				throw new Exception("algorithm must be 'H1' or 'H2'");
			final String heuristic = args[2];

			// generate random board if a file wasn't specified
			if (queenPositions == null) {
				queenPositions = new int[size];
				queenWeights = new int[size];
				Random rand = new Random();
				//rand.setSeed(11);
				for (int i = 0; i < size; ++i) {
					queenPositions[i] = rand.nextInt(size);
					queenWeights[i] = rand.nextInt(9) + 1;
				}
			}

			Board b = new Board(queenPositions, queenWeights);
			System.out.println("Board size: " + size);
			System.out.println("Algorithm: " + algorithm);
			System.out.println("Heuristic: " + heuristic);
			System.out.println("Board:");
			b.printBoard(queenPositions);

			if (algorithm.equals("Greedy hill climbing")) {
				b.hillClimb((heuristic.equals("H1")) ? 1 : 2);
			} else {
				b.aStar((heuristic.equals("H1")) ? 1 : 2);
			}

			System.exit(0);
		} catch (Exception e) {
			System.err.println(e.toString());
			showUsage();
			System.exit(1);
		}
	}

	/**
	 * prints instructions for using command-line arugments
	 */
	private static void showUsage() {
		System.out.println("Usage:");
		System.out.println("\tjava Main FILENAME ALGORITHM HEURISTIC");
		System.out.println("\tjava Main SIZE ALGORITHM HEURISTIC\n");
		System.out.println("FILENAME: path to CSV board file");
		System.out.println("SIZE: number of queens");
		System.out.println("ALGORITHM: 1 for A*, 2 for greedy hill climbing");
		System.out.println("HEURISTIC: H1 or H2");
		System.out.println("Example:");
		System.out.println("\tjava Main 30 1 H2");
	}
}
