import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class UrbanPlan {
	//max numbers not just numbers
	private int numIndustrial; //first line of input
	private int numCommercial; //second line of input
	private int numResidential;//third line of input
	
	private int finalScore;
	
	private String[][] board;//rest of input
	
	public String[][] getBoard() {
		return this.board;
	}
	
	/**
	 * Constructor for Urban Planning
	 * @param numI = max number of industrial areas
	 * @param numC = max number of commercial areas
	 * @param numR = max number of residential areas
	 * @param board = the starting board state
	 */
	public UrbanPlan(int numI, int numC, int numR, String[][] board) {
		this.numIndustrial = numI;
		this.numCommercial = numC;
		this.numResidential = numR;
		this.board = board;
	}
	
	/**
	 * parses the input and starts the hill climb or genetic algorithm
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		UrbanPlan up;
		
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
			int numIndustrial = Integer.parseInt(fileLines.get(0));
			int numCommercial = Integer.parseInt(fileLines.get(1));
			int numResidential = Integer.parseInt(fileLines.get(2));
			
			String[][] board = new String[fileLines.size()-3][fileLines.get(3).split(",").length];
			
			for(int i=3; i<=fileLines.size()-1;i++) {
				String[] vals = fileLines.get(i).split(",");
				for(int j=0; j<vals.length;j++) {
					board[i-3][j] = vals[j];
				}
			}

			up = new UrbanPlan(numIndustrial, numCommercial, numResidential, board);
			
		} catch (Exception e) {
			// got an error while trying to read the file
			System.err.println(e.toString());
			System.exit(1);
			return;
		}
		
		up.printBoard();
		System.out.println("Initial Score: " + up.finalScore);
		up.hillClimb();
		System.out.println();
		up.printBoard();
		System.out.println("Final Score: " + up.finalScore);
	}
	
	/**
	 * This method prints the current state of the board
	 */
	public void printBoard() {
		for(String[] row : board) {
			for(String col : row) {
				System.out.print(col+", ");
			}
			System.out.println();
		}
	}
	
	/**
	 * This method prints the current state of the board
	 * overloaded to take in a board
	 * 
	 * @param board the board a 2d array of strings (#s, X, and S)
	 */
	public void printBoard(String[][] board) {
		for(String[] row : board) {
			for(String col : row) {
				System.out.print(col+", ");
			}
			System.out.println();
		}
	}
	
	/**
	 * Calculates the best urban plan by using hill climbing
	 * Updates the board
	 */
	public void hillClimb() {
		int currentScore = this.finalScore;
		String[][] curBest = new String[this.board.length][this.board[0].length];
		curBest = copy2dArray(curBest);
		
		String[][] tempBoard = new String[this.board.length][this.board[0].length];
		tempBoard = copy2dArray(tempBoard);

        int industrialLeft = this.numIndustrial;
        int commercialLeft = this.numCommercial;
        int residentialLeft = this.numResidential;
        
		//possible moves include: add, remove, and move area
        boolean improvement = true;
        while(improvement) {
        	improvement = false;
        	String moveType = "I";
        	int testScore = currentScore;
        	String[][] testBoard = new String[this.board.length][this.board[0].length];
        	testBoard = copy2dArray(tempBoard, testBoard);
			for(int i=0;i<tempBoard.length;i++) {
				for(int j=0;j<tempBoard[i].length;j++) {					
					if(industrialLeft > 0 && !tempBoard[i][j].equals("X")) {
						tempBoard = copy2dArray(curBest, tempBoard);
						tempBoard[i][j] = "I";
						int tempScore = calculateScore(tempBoard);
						if(tempScore > testScore) {
							testScore = tempScore;
				        	testBoard = copy2dArray(tempBoard, testBoard);
							moveType = "I";
						}
					}
					if(commercialLeft > 0 && !tempBoard[i][j].equals("X")) {
						tempBoard = copy2dArray(curBest, tempBoard);
						tempBoard[i][j] = "C";
						int tempScore = calculateScore(tempBoard);
						if(tempScore > testScore) {
							testScore = tempScore;
				        	testBoard = copy2dArray(tempBoard, testBoard);
							moveType = "C";
						}
					}
					if(residentialLeft > 0 && !tempBoard[i][j].equals("X")) {
						tempBoard = copy2dArray(curBest, tempBoard);
						tempBoard[i][j] = "R";
				        int tempScore = calculateScore(tempBoard);
				        if(tempScore > testScore) {
				        	testScore = tempScore;
				        	testBoard = copy2dArray(tempBoard, testBoard);
				        	moveType = "R";
				        }
					}
				}
			}
			if(testScore>currentScore) {
				currentScore = testScore;
				curBest = copy2dArray(testBoard, curBest);
				improvement = true;
				switch(moveType) {
				case "I":
					industrialLeft--;
					break;
				case "C":
					commercialLeft--;
					break;
				case "R":
					residentialLeft--;
					break;
				}
			}
        }
		this.finalScore = calculateScore(curBest);
		this.board = curBest;
	}
	
	public int calculateScore(String[][] b) {
		int currentScore = 0;
		for(int i=0;i<b.length;i++) {
			for(int j=0;j<b[i].length;j++) {
				//I, C, R
				if(b[i][j].equals("I")) {
					currentScore += 2 * checkRangeTwo("I", b, i, j);
					currentScore -= 10 * checkRangeTwo("X", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}
				}else if(b[i][j].equals("C")) {
					currentScore -= 20 * checkRangeTwo("X", b, i, j);
					currentScore -= 4 * checkRangeTwo("C", b, i, j);
					currentScore += 4 * checkRangeThree("R", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}
				}else if(b[i][j].equals("R")) {
					currentScore -= 20 * checkRangeTwo("X", b, i, j);
					currentScore += 10 * checkRangeTwo("S", b, i, j);
					currentScore += 4 * checkRangeThree("C", b, i, j);
					currentScore -= 5 * checkRangeThree("I", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}
				}
			}
		}
		return currentScore;
	}
	
	/**
	 * Checks if two areas collide in a manhattan distance of 2 from an index
	 * @param a area type 
	 * @param board the board
	 * @param i i index
	 * @param j j index
	 * @return the number of occurences 
	 */
	
	//someone check this math please it's wonky
	public int checkRangeTwo(String a, String[][] board, int i, int j) {
		int occurences = 0;
		if(i-2 >= 0) {
			if(board[i-2][j].equals(a))	occurences++;
		}
		if(i-1 >= 0) {
			if(j-1>=0) {
				if(board[i-1][j-1].equals(a)) occurences++;
			}
			if(board[i-1][j].equals(a)) occurences++;
			if(j+1 < board[i].length) {
				if(board[i-1][j+1].equals(a)) occurences++;
			}
		}
		if(j-2>=0) {
			if(board[i][j-2].equals(a)) occurences++;
		}
		if(j-1>=0) {
			if(board[i][j-1].equals(a)) occurences++;
		}
		if(j+1<board[i].length){
			if(board[i][j+1].equals(a)) occurences++;
		}
		if(j+2<board[i].length) {
			if(board[i][j+2].equals(a)) occurences++;
		}
		if(i+1 < board.length) {
			if(j-1>=0) {
				if(board[i+1][j-1].equals(a)) occurences++;
			}
			if(board[i+1][j].equals(a)) occurences++;
			if(j+1<board[i].length) {
				if(board[i+1][j+1].equals(a)) occurences++;
			}
		}
		if(i+2<board.length) {
			if(board[i+2][j].equals(a)) occurences++;
		}
		return occurences;
	}
	
	/**
	 * Checks if two areas collide in a manhattan distance of 3 from an index
	 *  uses the check range 2 method
	 * @param a area type 
	 * @param board the board
	 * @param i i index
	 * @param j j index
	 * @return the number of occurences 
	 */
	public int checkRangeThree(String a, String[][] board, int i, int j) {
		int occurences = checkRangeTwo(a, board, i, j);
		if(i-3>=0) {
			if(board[i-3][j].equals(a)) occurences++;
		}
		if(i-2>=0) {
			if(j-1>=0) {
				if(board[i-2][j-1].equals(a)) occurences++;
			}
			if(j+1<board[i].length) {
				if(board[i-2][j+1].equals(a)) occurences++;
			}
		}
		if(i-1>=0) {
			if(j-2>=0) {
				if(board[i-1][j-2].equals(a)) occurences++;
			}
			if(j+2<board[i].length) {
				if(board[i-1][j+2].equals(a)) occurences++;
			}
		}
		if(j-3>=0) {
			if(board[i][j-3].equals(a)) occurences++;
		}
		if(j+3<board[i].length) {
			if(board[i][j+3].equals(a)) occurences++;
		}
		if(i+1<board.length) {
			if(j-2>=0) {
				if(board[i+1][j-2].equals(a)) occurences++;
			}
			if(j+2<board[i].length) {
				if(board[i+1][j+2].equals(a)) occurences++;
			}
		}
		if(i+2<board.length) {
			if(j-1>=0) {
				if(board[i+2][j-1].equals(a)) occurences++;
			}
			if(j+1<board[i].length) {
				if(board[i+2][j+1].equals(a)) occurences++;
			}
		}
		if(i+3<board.length) {
			if(board[i+3][j].equals(a)) occurences++;
		}
		return occurences;
	}
	
	public String[][] copy2dArray(String[][] dest){
		for(int i=0;i<dest.length;i++) {
			System.arraycopy(this.board[i], 0, dest[i], 0, this.board[i].length);
		}
		return dest;
	}
	
	public String[][] copy2dArray(String[][] src, String[][] dest){
		for(int i=0;i<dest.length;i++) {
			System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
		}
		return dest;
	}
}
