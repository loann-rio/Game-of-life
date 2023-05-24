package backend;

import java.util.HashMap;
import java.util.LinkedList;

public class Grid{
	
	// if the hashMap is iterated over, it cannot be modify, to avoid any errors
	// we keep track of when it can be modify, if isIterating is true, no modification are allowed
	private boolean isIterating = false;
	
	// initialize grid:
	private HashMap<Integer, HashMap<Integer, Cell>> grid = new HashMap<Integer, HashMap<Integer, Cell>>();
	
	/**
	 * Changes content value of the Cell at the coordinates specified in arguments
	 * 
	 * @param x coordinate on the x-axis (horizontal)
	 * @param y coordinate on the y-axis (vertical)
	 */
	public void toggleCell(int x, int y) {
		// get cell from the grid to check if it already exist or  
		Cell cell = getObjectCell(x, y);
		
		// if the cell doesn't exist, create it
		if (cell == null)
		{
			createNewCell(x, y, 1);
		}
		else 
		{
			// else toggle it's state
			int state = cell.toggleState();
			
			// if the cell was dead, create it's neighbors
			if (state == 1 && cell.neighborhood.getNbNeighbor() != 8)
			{
				addNeighborToCell(cell, x, y, state);
			}
		}
	}
	
	/**
	 * re-initialize the grid
	 */
	public void setGridToEmpty()
	{
		grid = new HashMap<Integer, HashMap<Integer, Cell>>();
	}
	
	/**
	 * @return the grid
	 */
	public HashMap<Integer, HashMap<Integer, Cell>> getGrid()
	{
		return grid;
	}
	
	private Cell getObjectCell(int x, int y)
	{
		// get the corresponding column from the hashMap
		HashMap<Integer, Cell> column = grid.get(x);
		
		// if this column does not exist (and so, the cell doesn't easer) create the cell
		if (column == null)
		{
			return null;
		}
		
		// else, get the single cell at position y
		return column.get(y);
	}
	
	/**
	 * every cell has it's neighborhood saved, when the cell is created, we have to set the neighbors
	 * @param newCell
	 * @param x
	 * @param y
	 * @param state
	 */
	private void addNeighborToCell(Cell newCell, int x, int y, int state)
	{
		// if it doesn't already have all 8 neighbors
		if (newCell.neighborhood.getNbNeighbor() != 8)
		{
			// for all the neighboring cells
			for (int i = x-1; i < x+2; i++) {
				for (int j = y-1; j < y+2; j++) {
					if (i!=x || j!=y)
					{
						Cell cell = getObjectCell(i, j);
						if (cell != null)
						{
							newCell.neighborhood.addNeighbor(cell);
							cell.neighborhood.addNeighbor(newCell);
						}
						else if (state != 0)
						{
							createNewCell(i, j, 0);
						}
					}	
				}
			}
		}
	}
	
	/**
	 * create a new cell at x, y position 
	 * @param x
	 * @param y
	 * @param state
	 */
	private void createNewCell(int x, int y, int state)
	{
		if (isIterating) { return; }
		Cell newCell = new Cell(state, x, y);
		
		
		// get the corresponding column from the hashMap
		HashMap<Integer, Cell> column = grid.get(x);
		
		// if this column does not exist create it
		if (column == null)
		{
			grid.put(x, new HashMap<Integer, Cell>());
		}
		
		grid.get(x).put(y, newCell);
		
		addNeighborToCell(newCell, x, y, state);		
	}
	
	/**
	 * get the value of a cell at coordinates
	 * @param x coordinate
	 * @param y coordinate
	 * @return the value of the cell
	 */
	public int getCell(int x, int y) {
		// get the corresponding column from the hashMap
		HashMap<Integer, Cell> column = grid.get(x);
				
		if (column == null) {
			return -1;		
		} else {
			// else, get the single cell at position y
			Cell cell =  column.get(y);
			
			if (cell == null) {
				return -1;
			} else {
				// finally, if the cell exist, toggle it
				return cell.getState();
			}
		}
	}

	
	/**
	 * set the value of a cell at coordinates
	 * @param x coordinate
	 * @param y coordinate
	 * @param val the value to set inside the cell
	 */
	public void setCell(int x, int y, int val) {
		Cell cell = getObjectCell(x, y);
		if (cell != null)
		{
			cell.setState(val);
			if (cell.getState() != 0 && cell.neighborhood.getNbNeighbor() != 8)
			{
				addNeighborToCell(cell, x, y, val);
			}
		} else {
			createNewCell(x, y, val);
		}
	}
	
	
	/**
	 * determine if any cell are alive
	 * @return is the board null
	 */
	public boolean isBoardNull() {
		return (grid.isEmpty());
	}
	
	public void removeCell(Cell cell)
	{
		cell.neighborhood.die(cell);
		grid.get(cell.getPos(0)).remove(cell.getPos(1));
	}
	
	/**
	 * Individual step of the Simulation, modifying the world from
	 * its state at time t to its state at time t+1
	 */
	public void makeStep() {
		
		LinkedList<Cell> cellToBeRemoved = new LinkedList<Cell>();
		LinkedList<Cell> newBornCellToAddNeighborTo = new LinkedList<Cell>();
		
		// first count neighbors
		isIterating = true;
		for (HashMap<Integer, Cell> column : grid.values())
		{
			for (Cell cell : column.values())
			{
				cell.countNeighbors();
			}
		}
		
		// update the state of the cells 
		for (HashMap<Integer, Cell> column : grid.values())
		{
			for (Cell cell : column.values())
			{
				int newBornOrDead = cell.update();
				
				// not proud of this, should be changed
				if (newBornOrDead == 1)
				{
					newBornCellToAddNeighborTo.add(cell);
				}
				
				else if (newBornOrDead == -1)
				{
					cellToBeRemoved.add(cell);
				}
			}
		}
		isIterating = false;
		
		
		for (Cell cellToRemove : cellToBeRemoved)
		{
			removeCell(cellToRemove);
		}
		
		for (Cell newBornCell : newBornCellToAddNeighborTo)
		{
			addNeighborToCell(newBornCell, newBornCell.getPos(0), newBornCell.getPos(1), 1);
		}
	}
	
	/**
	 * populates world with randomly living cells
	 * 
	 * @param chanceOfLife the probability, expressed between 0 and 1, 
	 * that any given cell will be living
	 */
	public void generateRandom(float chanceOfLife, int Xoffset, int Yoffset, int width, int height) {
		for (int x=Xoffset;x<width+Xoffset; x++)
		{
			for (int y=Yoffset; y<height+Yoffset; y++)
			{
				setCell(x, y, (Math.random() < chanceOfLife)?1:0);
			}
		}
	}
	
	public void populateLine(int y, String line)
	{
		for (int x=0; x<line.length()/2; x++)
		{
			int state = Character.getNumericValue(line.charAt(x*2));
			
			if (state != 0)
			{
				setCell(x, y, state);
			}
			
		}
	}
}
