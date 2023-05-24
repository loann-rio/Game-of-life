package backend;

import java.io.BufferedReader;
import java.io.BufferedWriter;

import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import java.util.ArrayList;
import java.util.HashMap;


public class FileLoader {
	
	private Grid Maingrid; // get the main grid
	
	public void setGrid(Grid grid)
	{
		Maingrid = grid;
	}
	
	/**
	 * load file, the file can have two different type: text or csv
	 * @param fileName
	 * @return the range of the window
	 */
	public int[] loadFile(String fileName) {
			
		Maingrid.setGridToEmpty();
		int[] rangeWindow = new int[4]; // size of the window
		
		try {
			BufferedReader fileContent = new BufferedReader(new FileReader(fileName));
			ArrayList<String> lines = new ArrayList<String> ();
			
			// fill lines from file
			String line = fileContent.readLine();
			
			// text file start with t while excel start with a number
			if (line.charAt(0) == 't')
			{
				rangeWindow = loadTextFile(line);
			} 
			else
			{
				while (line != null)
				{
					lines.add(line);
					line = fileContent.readLine();
				}
				
				rangeWindow = loadExcelFile(lines);
			}
			
			fileContent.close();
			
		} catch (Exception e) {
			e.printStackTrace();
		}	
		
		return rangeWindow;
	}
	
	/**
	 * create a file of type "t;posX1;posY1;val1;posX2;posY2;val2......."
	 */
	public String convertGridToArrayOfStringForTextFile()
	{
		
		// the 'text file' start with a t to be differentiated to other type of file, 
		// in the future a more complex ID could be necessary to be sure the file is the right type
		String GridInString = "t;";
		
		HashMap<Integer, HashMap<Integer, Cell>> grid = Maingrid.getGrid();
		
		for (HashMap<Integer, Cell> column : grid.values())
		{
			for (Cell cell : column.values())
			{
				if (cell.getState() != 0)
				{
					GridInString += cell.getPos(0) + ";" + cell.getPos(1) + ";" + cell.getState() + ";";
				}
			}
		}
		return GridInString;
	}
	
	/**
	 * convert Grid To Array Of String For Excel File
	 * @return the grid as an array of string
	 */
	public String[] convertGridToArrayOfStringForExcelFile()
	{
		
		HashMap<Integer, HashMap<Integer, Cell>> grid = Maingrid.getGrid();
		
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;
		
		for (HashMap<Integer, Cell> column : grid.values())
		{
			for (Cell cell : column.values())
			{
				if (cell.getState() != 0)
				{
					if (cell.getPos(0)<minX) { minX = cell.getPos(0); }
					if (cell.getPos(1)<minY) { minY = cell.getPos(1); }
					if (cell.getPos(0)>maxX) { maxX = cell.getPos(0); }
					if (cell.getPos(1)>maxY) { maxY = cell.getPos(1); }
				}
			}
		}
		
		// TODO (but will never)
		
		return new String[0];
	}
	
	/**
	 * save the grid as an CSV file 
	 * @param fileName
	 */
	public void writeExcelFile(String fileName) {
		
		String[] content = convertGridToArrayOfStringForExcelFile();
		
		FileWriter csvWriter;
		try {
			csvWriter = new FileWriter(fileName);
			for (String row : content) {
				csvWriter.append(row);
				csvWriter.append("\n");
			}
			csvWriter.flush();
			csvWriter.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * save a String in a file
	 * @param fileName
	 * @param text
	 * @throws IOException
	 */
	public void WriteTextFile(String fileName, String text)
		throws IOException {
			// open a new file 
		    BufferedWriter writer = new BufferedWriter(new FileWriter(fileName));
		    // write the text on the file
		    writer.write(text);
		    // close the file
		    writer.close();
	}
	
	/**
	 * get new grid from CSV file
	 * @param fileContent
	 * @return size of the loaded model
	 */
	public int[] loadExcelFile(ArrayList<String> fileContent) {
		int y = 0;
		for (String line : fileContent)
		{
			Maingrid.populateLine(y, line);
			y++;
		}
		return new int[] { 0, 0, fileContent.get(0).length()/2, fileContent.size() };
	}
	
	/**
	 * load a text file on the grid
	 * @param line
	 * @return the top left, w and h of the new image for zAO to be able to center it
	 */
	public int[] loadTextFile(String line)
	{
		String[] separated = line.split(";");
		
		int minX = Integer.MAX_VALUE;
		int minY = Integer.MAX_VALUE;
		int maxX = Integer.MIN_VALUE;
		int maxY = Integer.MIN_VALUE;

		for (int pos = 1; pos<separated.length; pos+=3)
		{
			int x = Integer.parseInt(separated[pos]);
			int y = Integer.parseInt(separated[pos+1]);
			int val = Integer.parseInt(separated[pos+2]);

			Maingrid.setCell(x, y, val);
			
			if (x<minX) { minX = x; }
			if (y<minY) { minY = y; }
			if (x>maxX) { maxX = x; }
			if (y>maxY) { maxY = y; }
		}
		
		return new int[] { minX-5, minY-5, maxX+5, maxY+5 } ;
	}

	/** write the grid to a file
	* for now it can only do it as text file and not CSV 
	* so when save as CSV is clicked it save the file as a text anyway
	* should be changed
	*/
	public void writeFile(String fileName) {
		try {
			String txt = convertGridToArrayOfStringForTextFile();
			WriteTextFile(fileName, txt);		
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
