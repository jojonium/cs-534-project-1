import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class UrbanPlan {
	//max numbers not just numbers
	private int numIndustrial; //first line of input
	private int numCommercial; //second line of input
	private int numResidential;//third line of input
	
	private int finalScore;
	private long finalTime;
	
	private String[][] board;//rest of input
	
	/**
	 * Getter for board
	 * @return the original board
	 */
	public String[][] getBoard() {
		return this.board;
	}
	
	/**
	 * getter for numIndustrial
	 * @return returns the original number of industrial areas
	 */
	public int getNumIndustrial() {
		return this.numIndustrial;
	}
	
	/**
	 * getter for numCommercial
	 * @return the original number of commercial areas
	 */
	public int getNumCommercial() {
		return this.numCommercial;
	}
	
	/**
	 * getter for numResidential
	 * @return the original number of residential areas
	 */
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
		
		//parses input
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
		
		System.out.println("Initial map state:");
		up.printBoard();
		System.out.println("Initial Score: " + up.finalScore);
		//runs genetic algorithm or hill climbing
		if(args[1].equals("HC")) {
			up.hillClimb();
		}else if(args[1].equals("GA")){
			up.geneticAlgorithm();
		}else {
			System.out.println("please input an algorithm argument (GA, or HC)\nThe format for input is ./UrbanPlan [filename] [algorithm]");
		}
	}
	
	/**
	 * produces a string containing the state of the board
	 * @return a string of the state of the board
	 */
	public String boardContent() {
		String boardContent = "";
		for(String[] row : board) {
			for(String col : row) {
				boardContent += col + ", ";
			}
			boardContent += "\n";
		}
		return boardContent;
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
		//set up the boards needed for testing
		//note: there may be a better way to do this but I can't figure it out right now 
		//		and deleting anything breaks everything :'^(
		String[][] curBest = new String[this.board.length][this.board[0].length];
		curBest = copy2dArray(curBest);
		
		String[][] tempBoard = new String[this.board.length][this.board[0].length];
		tempBoard = copy2dArray(tempBoard);
		
		String[][] semiFinalBoard = new String[this.board.length][this.board[0].length];
		semiFinalBoard = copy2dArray(semiFinalBoard);
        
        int temperature = 100; //high starting temperature for simulated annealing
        Random random = new Random();
        boolean annealFlag = false;
        int numTrials = 0;
        
		//possible moves include: add, remove, and move area
        boolean improvement = true;
        long startTime = System.currentTimeMillis();
        long currentTime = startTime;
        
        //loop through trials until we hit 10 seconds
        while((currentTime - startTime < 10000)) {
        	//annealing starts after 3 seconds to have lots of randomness to encourage placement
        	if(temperature > 0 && numTrials % 1000 == 0 && currentTime-startTime>3000) {
        		temperature-=10;
        	}
        	numTrials++;
        	
        	//set & reset all counters to their initial values
        	int industrialLeft = this.numIndustrial;
            int commercialLeft = this.numCommercial;
            int residentialLeft = this.numResidential;
            int testScore = 0;
            int currentScore = 0;
            improvement = true;
            curBest = copy2dArray(curBest);
            
            //perform a hill climbing
        	while(improvement) {
        		//set some initial flags
            	improvement = false;
            	annealFlag = false;
            	String moveType = "I";
            	
            	//update the test score
            	testScore = currentScore;
            	
            	//reset the test board
        		tempBoard = copy2dArray(tempBoard);
            	String[][] testBoard = new String[this.board.length][this.board[0].length];
            	testBoard = copy2dArray(tempBoard, testBoard);
            	
            	//loop through all possible moves and pick the best one
    			for(int i=0;i<tempBoard.length;i++) {
    				for(int j=0;j<tempBoard[i].length;j++) {		
    					
    					//check if we can place an industrial area
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
    					
    					//check if we can place a commercial area
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
    					
    					//check if we can place a residential area
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
    			//check if the new move was better than previous or if we are annealing
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
        	
        	//check if this random iteration did better than any previous trials
        	if(currentScore > this.finalScore) {
        		this.finalScore = currentScore;
        		semiFinalBoard = copy2dArray(curBest, semiFinalBoard);
        		this.finalTime = currentTime - startTime;
        	}
        }
        
        //update the final board and print/export stats
		this.board = semiFinalBoard;
		printStats();
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
		
		//loop through the board
		for(int i=0;i<b.length;i++) {
			for(int j=0;j<b[i].length;j++) {
				//Check the score based on industrial areas (I)
				if(b[i][j].equals("I")) {
					currentScore += 2 * checkRangeTwo("I", b, i, j);
					currentScore -= 10 * checkRangeTwo("X", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}else if(this.board[i][j].equals("S")) {
						currentScore -= 1;
					}
				//Check the score based on industrial areas (I)
				}else if(b[i][j].equals("C")) {
					currentScore -= 20 * checkRangeTwo("X", b, i, j);
					currentScore -= 4 * checkRangeTwo("C", b, i, j);
					currentScore += 4 * checkRangeThree("R", b, i, j);
					if(!this.board[i][j].equals("X") && !this.board[i][j].equals("S")) {
						currentScore -= (2 + Integer.parseInt(this.board[i][j]));
					}else if(this.board[i][j].equals("S")) {
						currentScore -= 1;
					}
				//Check the score based on industrial areas (I)
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
	
	/**
	 * Prints the final results and outputs to a file
	 */
	public void printStats() {
		
		//print stats
		System.out.println();
		System.out.println("Optimal Urban Plan:");
		printBoard();
		System.out.println("Final Score: " + this.finalScore);
		System.out.println("Time to find Solution: " + this.finalTime + " milliseconds, (" + (double)this.finalTime/1000 + " seconds).");
		int[] usage = countUsage();
		System.out.println("This solution used " + usage[0] + " out of " + this.numIndustrial + " possible industrial areas.");
		System.out.println("This solution used " + usage[1] + " out of " + this.numCommercial + " possible commercial areas.");
		System.out.println("This solution used " + usage[2] + " out of " + this.numResidential + " possible residential areas.");
		
		//write stats to file
		try {
			FileWriter writer = new FileWriter("UrbanPlanResults.txt");
			writer.write("Optimal Urban Plan: \n");
			writer.write(boardContent());
			writer.write("Final Score: " + this.finalScore + "\n");
			writer.write("Time to find Solution: " + this.finalTime + " milliseconds, (" + (double)this.finalTime/1000 + " seconds)\n");
			writer.write("This solution used " + usage[0] + " out of " + this.numIndustrial + " possible industrial areas.\n");
			writer.write("This solution used " + usage[1] + " out of " + this.numCommercial + " possible commercial areas.\n");
			writer.write("This solution used " + usage[2] + " out of " + this.numResidential + " possible residential areas.\n");
			writer.close();
			System.out.println("Results written to UrbanPlanResults.txt");
		} catch (IOException e) {
			System.out.println("An error occurred writing the file.");
			e.printStackTrace();
		}
	}
	
	/**
	 * counts the number of industrial, commercial, and residential areas used
	 * @return an array containing [numIndustrial, numCommerical, numResidential]
	 */
	public int[] countUsage() {
		int industrialUsed = 0;
		int commericalUsed = 0;
		int residentialUsed = 0;
		for(int i=0;i<this.board.length;i++) {
			for(int j=0;j<this.board[i].length;j++) {
				switch(this.board[i][j]) {
					case "I":
						industrialUsed++;
						break;
					case "C":
						commericalUsed++;
						break;
					case "R":
						residentialUsed++;
						break;
				}
			}
		}
		return new int[] {industrialUsed, commericalUsed, residentialUsed};
	}

	/**
	 * Calculates the best urban plan by using genetic algorithm
	 * Updates the board
	 */
	public void geneticAlgorithm() {
		//k is number of boards we will generate, boardList is list of boards
		int k = 4;
		ArrayList<String[][]> boardList = new ArrayList<>();

		//generate k random boards and add to boardList
		for(int i = 0; i < k; i++) {
			String[][] generatedBoard = generateBoard();
			boardList.add(generatedBoard);
			printBoard(generatedBoard);
			System.out.println();
		}

		//shuffle list and take first two for parents
		//helper function for crossover
		//crossover is the cut in half and combine (+rules of combining to make sure board is valid)
		//
	}

	/**
	 * Generates a random board
	 * Returns a board
	 */
	public String[][] generateBoard() {
		String[][] randBoard = new String[this.board.length][this.board[0].length];

		//start with a copy of original board
		randBoard = copy2dArray(randBoard);

		//generate 3 random numbers for number of I, C, R to place down; upper bound should be max of item
		Random rand = new Random();
		int indRand = rand.nextInt(this.numIndustrial + 1);
		int comRand = rand.nextInt(this.numCommercial + 1);
		int resRand = rand.nextInt(this.numResidential + 1);

		//given an array of all the available locations, shuffle it
		ArrayList<int[]> freeLocations = getFreeLocations(randBoard);
		Collections.shuffle(freeLocations);
		int count = 0;

		//place I, R, C on map if conditions meet
		for(int i = 0; i < indRand && freeLocations.size() > 0; i++) {
			int[] coords = freeLocations.get(count);
			count++;
			randBoard[coords[0]][coords[1]] = "I";
		}
		for(int j = 0; j < resRand && freeLocations.size() > 0; j++) {
			int[] coords = freeLocations.get(count);
			count++;
			randBoard[coords[0]][coords[1]] = "R";
		}
		for(int k = 0; k < comRand && freeLocations.size() > 0; k++) {
			int[] coords = freeLocations.get(count);
			count++;
			randBoard[coords[0]][coords[1]] = "C";
		}

		return randBoard;
	}

	/**
	 * Gets a list of available locations on a given board
	 * Returns a list of available coordinates
	 */
	public ArrayList<int[]> getFreeLocations(String[][] tempBoard) {
		ArrayList<int[]> freeLocations = new ArrayList<>();
		for(int i = 0; i < tempBoard.length; i++) {
			for(int j = 0 ; j < tempBoard[i].length; j++) {

				//if current location is not X, add location as free
				if(!tempBoard[i][j].equals("X")) {
					freeLocations.add(new int[]{i, j});
				}
			}
		}

		return freeLocations;
	}
}
