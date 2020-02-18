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
	private long finalTime;
	
	private String[][] board;//rest of input
	
	public String[][] getBoard() {
		return this.board;
	}
	
	public int getNumIndustrial() {
		return this.numIndustrial;
	}
	
	public int getNumCommercial() {
		return this.numCommercial;
	}
	
	public int getNumResidential() {
		return this.numResidential;
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
		//int currentScore = this.finalScore;
		String[][] curBest = new String[this.board.length][this.board[0].length];
		curBest = copy2dArray(curBest);
		
		String[][] tempBoard = new String[this.board.length][this.board[0].length];
		tempBoard = copy2dArray(tempBoard);
		
		String[][] semiFinalBoard = new String[this.board.length][this.board[0].length];
		semiFinalBoard = copy2dArray(semiFinalBoard);

        int industrialLeft = this.numIndustrial;
        int commercialLeft = this.numCommercial;
        int residentialLeft = this.numResidential;
        
        int temperature = 100; //high starting temperature for simulated annealing
        Random random = new Random();
        boolean annealFlag = false;
        int numTrials = 0;
        
		//possible moves include: add, remove, and move area
        boolean improvement = true;
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        while((currentTime - startTime < 10000)) {
        	if(temperature > 0 && numTrials % 100 == 0) {
        		//temperature--;
        		//System.out.println(temperature);
        	}
        	numTrials++;
        	industrialLeft = this.numIndustrial;
            commercialLeft = this.numCommercial;
            residentialLeft = this.numResidential;
            int testScore = 0;
            int currentScore = 0;
            improvement = true;
            curBest = copy2dArray(curBest);
        	while(improvement) {
            	improvement = false;
            	annealFlag = false;
            	String moveType = "I";
            	testScore = currentScore;
        		tempBoard = copy2dArray(tempBoard);
            	String[][] testBoard = new String[this.board.length][this.board[0].length];
            	testBoard = copy2dArray(tempBoard, testBoard);
    			for(int i=0;i<tempBoard.length;i++) {
    				for(int j=0;j<tempBoard[i].length;j++) {					
    					if(industrialLeft > 0 && !tempBoard[i][j].equals("X")) {
    						tempBoard = copy2dArray(curBest, tempBoard);
    						tempBoard[i][j] = "I";
    						int tempScore = calculateScore(tempBoard);
    						if(tempScore >= testScore) {
    							testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    							moveType = "I";
    						}else if(random.nextInt(1000) < temperature) {
    							testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    							moveType = "I";
    							annealFlag = true;
    							i = tempBoard.length;
    							break;
    						}
    					}
    					if(commercialLeft > 0 && !tempBoard[i][j].equals("X")) {
    						tempBoard = copy2dArray(curBest, tempBoard);
    						tempBoard[i][j] = "C";
    						int tempScore = calculateScore(tempBoard);
    						if(tempScore >= testScore) {
    							testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    							moveType = "C";
    						}else if(random.nextInt(1000) < temperature) {
    							testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    							moveType = "C";
    							annealFlag = true;
    							i = tempBoard.length;
    							break;
    						}
    					}
    					if(residentialLeft > 0 && !tempBoard[i][j].equals("X")) {
    						tempBoard = copy2dArray(curBest, tempBoard);
    						tempBoard[i][j] = "R";
    				        int tempScore = calculateScore(tempBoard);
    				        if(tempScore >= testScore) {
    				        	testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    				        	moveType = "R";
    				        }else if(random.nextInt(1000) < temperature) {
    							testScore = tempScore;
    				        	testBoard = copy2dArray(tempBoard, testBoard);
    							moveType = "R";
    							annealFlag = true;
    							i = tempBoard.length;
    							break;
    						}
    					}
    				}
    			}
    			if(testScore>currentScore || annealFlag) {
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
        	currentTime = System.currentTimeMillis();
        	if(currentScore > this.finalScore) {
        		this.finalScore = currentScore;
        		semiFinalBoard = copy2dArray(curBest, semiFinalBoard);
        		this.finalTime = currentTime - startTime;
        		//System.out.println(this.finalTime);
        	}
        }
		this.board = semiFinalBoard;
		
		System.out.println();
		printBoard();
		System.out.println("Final Score: " + finalScore);
		System.out.println("Time to find Solution: " + finalTime + " milliseconds, (" + (double)finalTime/1000 + " seconds).");
    	System.out.println("In 10 seconds " + numTrials + " trials happened");
	}
	
	/**
	 * calculates the score of a board
	 * uses the weights of the original board to determine build costs
	 * @param b the board to calculate the score of
	 * @return the score of the board
	 */
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
					}else if(this.board[i][j].equals("S")) {
						currentScore -= 1;
					}
				}else if(b[i][j].equals("C")) {
					currentScore -= 20 * checkRangeTwo("X", b, i, j);
					currentScore -= 4 * checkRangeTwo("C", b, i, j);
					currentScore += 4 * checkRangeThree("R", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}else if(this.board[i][j].equals("S")) {
						currentScore -= 1;
					}
				}else if(b[i][j].equals("R")) {
					currentScore -= 20 * checkRangeTwo("X", b, i, j);
					currentScore += 10 * checkRangeTwo("S", b, i, j);
					currentScore += 4 * checkRangeThree("C", b, i, j);
					currentScore -= 5 * checkRangeThree("I", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}else if(this.board[i][j].equals("S")) {
						currentScore -= 1;
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
	
	/**
	 * Copies the original board into an array
	 * @param dest the array to copy to
	 * @return the new board
	 */
	public String[][] copy2dArray(String[][] dest){
		for(int i=0;i<dest.length;i++) {
			System.arraycopy(this.board[i], 0, dest[i], 0, this.board[i].length);
		}
		return dest;
	}
	
	/**
	 * copies a 2d array from src to dest
	 * @param src the array to copy
	 * @param dest the array to copy into
	 * @return the new array
	 */
	public String[][] copy2dArray(String[][] src, String[][] dest){
		for(int i=0;i<dest.length;i++) {
			System.arraycopy(src[i], 0, dest[i], 0, src[i].length);
		}
		return dest;
	}
}
