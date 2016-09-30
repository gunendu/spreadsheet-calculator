import java.util.LinkedHashSet;
import java.util.Scanner;
import java.util.Set;
import java.util.Stack;
import java.util.logging.Logger;

public class Spreadsheet {

  private static final Logger LOGGER = Logger.getLogger(Spreadsheet.class.getName());
  private static final int STATUS_CODE_FAILURE = 1;

  public class Cell {
		boolean IsEvaluated;
		String cellContent;
    Double evaluatedValue;
		boolean IsCurrentEvaluation;

		public Cell(String cellContent) {
			this.cellContent = cellContent;
			this.IsCurrentEvaluation = false;
			this.IsEvaluated = false;
		}
	}

  Cell[][] sheetCells;
	private int row;
	private int col;

  private Double evaluateCell(Cell sheetCell,Set<Cell> currentEvaluationStack) throws IllegalArgumentException {
		 if(currentEvaluationStack == null)
		 {
			 currentEvaluationStack = new LinkedHashSet<Cell>();
		 }

		if(sheetCell.IsEvaluated)
		{

		}
		else if(!sheetCell.IsEvaluated && !currentEvaluationStack.contains(sheetCell))
		{
			currentEvaluationStack.add(sheetCell);
			String[] fields = sheetCell.cellContent.split(" ");
	    Stack<Double> operands = new Stack<Double>();
      double op1,op2;
      for(int i=0;i<fields.length;i++) {
          String s = fields[i];
          if(s.equals("+")) {
            op1 = operands.pop();
            op2 = operands.pop();
            operands.push(op2 + op1);
          }
          else if (s.equals("*")) {
            op1 = operands.pop();
            op2 = operands.pop();
            operands.push(op2 * op1);
          }
          else if (s.equals("/")) {
          	op1 = operands.pop();
            op2 = operands.pop();
            if (op1 == 0) {
					      throw new IllegalArgumentException("Error: Cannot divide by 0");
				    }
          	operands.push( op2 / op1);
          }
          else if (s.equals("-")) {
          	op1 = operands.pop();
            op2 = operands.pop();
          	operands.push( op2 - op1);
          }
          else if (s.equals("++")) {
            op1 = operands.pop();
            operands.push(++op1);
          }
          else if (s.equals("--")) {
            op1 = operands.pop();
            operands.push(--op1);
          }
          else if (isNumber(s)) {
            operands.push(Double.parseDouble(s));
          }
          else {
          	Cell anotherCell = getCell(s);
          	operands.push(evaluateCell(anotherCell,currentEvaluationStack));
          }
      }
      sheetCell.evaluatedValue = operands.pop();
      sheetCell.IsEvaluated = true;
		}
    else {
      LOGGER.severe("Circular Dependency "+sheetCell.cellContent);
			System.exit(STATUS_CODE_FAILURE);
		}
		return sheetCell.evaluatedValue;
	}

  private static boolean isNumber(String s) {
     try {
          Double.parseDouble(s);
          return true;
        }
        catch (NumberFormatException e) {
          return false;
        }
  }

  private Cell getCell(String s) {
		try {
        int x = (int)s.charAt(0) % 65;
        int y = Integer.parseInt(s.substring(1,s.length()))-1;
		    return sheetCells[x][y];
		 }
     catch (NumberFormatException e) {
       LOGGER.severe("Data format error" + s);
			 System.exit(1);
		}
		return null;
	}

  private static void populateCellValues(Spreadsheet spreadSheet) {
    try
    {
    Scanner inputScanner = new Scanner(System.in);
    spreadSheet.sheetCells= null;
    String[] fields = null;
    int[] size = new int[2];
    if (inputScanner.hasNextLine()) {
      fields = inputScanner.nextLine().split(" ");
      if (fields.length != 2) {
        throw new IllegalArgumentException("Invalid Size");
      }
      else {
        for (int i = 0; i < fields.length; i++) {
          size[i] = Integer.parseInt(fields[i]);
        }
        spreadSheet.sheetCells = new Cell[size[1]][size[0]];
        spreadSheet.col = size[0];
        spreadSheet.row = size[1];
      }
    }

    int rowIndex = 0,colIndex = 0,cellCount=0;
    while (inputScanner.hasNextLine()) {
      String line = inputScanner.nextLine();
      if (line.isEmpty())
        break;
      spreadSheet.sheetCells[rowIndex][colIndex] = spreadSheet.new Cell(line);
      cellCount++;
      colIndex++;
      if(colIndex==spreadSheet.col)
      {
        colIndex = 0;
        rowIndex++;
      }
    }

    if (cellCount != size[0]*size[1])
      throw new IllegalArgumentException("Cell dont match");
    }
    catch(Exception e){
        LOGGER.severe("Error reading values");
        System.exit(1);
    }
  }

  public static void main(String[] args){
		try {
  		Spreadsheet spreadSheet = new Spreadsheet();
  		populateCellValues(spreadSheet);
  		for (int i = 0; i < spreadSheet.row; i++) {
  			for (int j = 0; j < spreadSheet.col; j++) {
  				spreadSheet.evaluateCell(spreadSheet.sheetCells[i][j],null);
  			}
  		}
      System.out.println(spreadSheet.col +" "+spreadSheet.row);
  		for (int i = 0; i < spreadSheet.row; i++) {
  			for (int j = 0; j < spreadSheet.col; j++) {
  				if(i==spreadSheet.row-1 && j==spreadSheet.col-1) {
  					System.out.format("%.5f", spreadSheet.sheetCells[i][j].evaluatedValue);
          }
  				else {
  				  System.out.format("%.5f%n", spreadSheet.sheetCells[i][j].evaluatedValue);
          }
  			}
  		}
	 }
   catch(Exception e) {
      LOGGER.severe("Error occurrend in main:"+e.getMessage());
   }
	}

}
