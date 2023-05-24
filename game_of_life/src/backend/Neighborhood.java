package backend;

import java.util.LinkedList;

public class Neighborhood {
	LinkedList<Cell> neighbors = new LinkedList<Cell>();
	
	/**
	 * add a neighbor to the list
	 * @param cell
	 */
	public void addNeighbor(Cell cell)
	{
		if (!neighbors.contains(cell))
		{
			neighbors.add(cell);
		}
	}
	
	/**
	 * @return the number of loaded neighbors
	 */
	public int getNbNeighbor()
	{
		return neighbors.size();
	}
	
	/**
	 * remove a cell from the list
	 * @param cell
	 */
	public void removeCell(Cell cell)
	{
		neighbors.remove(cell);
	}
	
	/**
	 * increment the number of neighbor to the neighbors
	 * if no neighbor was found, delete the cell 
	 */
	public void updateNbNeighbors(int state)
	{
		for (Cell cell : neighbors)
		{
			cell.addNeighbor(state);
		}
	}
	
	/**
	 * when the cell die, remove it from neighbor's neighborhood
	 * @param cell
	 */
	public void die(Cell thisCell)
	{
		for (Cell cell : neighbors)
		{
			cell.neighborhood.removeCell(thisCell);
		}
	}
	
}
