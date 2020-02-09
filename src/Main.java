// Main class for Heavy N-Queens
// CS534 Assignment 1

public class Main {
	/**
	 * parses command-line arguments to get parameters for number of queens,
	 * which algorithm to use, and which heuristic function to use
	 */
	public static void main(String[] args) {
		if (args.length < 3) {
			showUsage();
			System.exit(1);
			return;
		}
		try {
			final int size = Integer.parseInt(args[0]);
			if (size < 1)
				throw new Exception("size must be >= 1");
			if (!args[1].equals("1") && !args[1].equals("2"))
				throw new Exception("algorithm must be '1' or '2'");
			final String algorithm = (args[1].equals("1")) ? "A*" : "Greedy hill climbing";
			if (!args[2].equals("H1") && !args[2].equals("H2"))
				throw new Exception("algorithm must be 'H1' or 'H2'");
			final String heuristic = args[2];

			System.out.println("Board size: " + size);
			System.out.println("Algorithm: " + algorithm);
			System.out.println("Heuristic: " + heuristic);
			System.exit(0);
			return;
		} catch (Exception e) {
			System.err.println(e.toString());
			showUsage();
			System.exit(1);
			return;
		}
	}

	/**
	 * prints instructions for using command-line arugments
	 */
	private static void showUsage() {
		System.out.println("Usage:");
		System.out.println("\t./nqueens SIZE ALGORITHM HEURISTIC\n");
		System.out.println("SIZE: number of queens");
		System.out.println("ALGORITHM: 1 for A*, 2 for greedy hill climbing");
		System.out.println("HEURISTIC: H1 or H2");
		System.out.println("Example:");
		System.out.println("\t./nqueens 30 1 H2");
	}
}
