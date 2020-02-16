import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.Random;

public class UrbanPlan {
	private int numIndustrial; //first line of input
	private int numCommercial; //second line of input
	private int numResidential;//third line of input
	
	private String[][] board;//rest of input
	
	public UrbanPlan(int numI, int numC, int numR, String[][] board) {
		this.numIndustrial = numI;
		this.numCommercial = numC;
		this.numResidential = numR;
		this.board = board;
	}
	
	
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
	}
	
	public void printBoard() {
		for(String[] row : board) {
			for(String col : row) {
				System.out.print(col+", ");
			}
			System.out.println();
		}
	}
}
