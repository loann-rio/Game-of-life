package backend;

public class Cell {
	
	// position of the cell
	private int[] pos = new int[2];
	
	private int numberOfPossibleState = 5;
	
	// array containing the count of neighbors by type
	private int[] nbNeighbor = new int[numberOfPossibleState];
	
	// initialize class neighborhood;
	public Neighborhood neighborhood = new Neighborhood(); 
	
	// state of the cell
	private int state = 0;
	
	public Cell(int alive, int posX, int posY) {
		pos = new int[] {posX, posY};
		state = alive%numberOfPossibleState; // set the state with a limitation at the max number of possible state
	}
	
	/**
	 * 
	 * @param i 0->x, 1->y
	 * @return position 
	 */
	public int getPos(int i)
	{
		return pos[i];
	}
	
	/**
	 * @return the state of the cell
	 */
	public int getState() 
	{
		return state;
	}
	
	/**
	 * set state of the cell
	 * @param newState
	 */
	public void setState(int newState)
	{
		state = newState%numberOfPossibleState; // keep the number in possible range (0  to  numberOfPossibleState-1)
	}
	
	/**
	 * toggle state of the cell
	 * @return the new state
	 */
	public int toggleState()
	{
		state = (state+1)%numberOfPossibleState;
		return state;
	}
	
	/**
	 * update the number of neighbors in the neighborhood: tell the neighbors the state of the cell
	 */
	public void countNeighbors()
	{
		if (state != 0)
		{
			neighborhood.updateNbNeighbors(state);
		}
	}
	
	/**
	 * add neighbor to count
	 * @param typeOfNeighbor
	 */
	public void addNeighbor(int typeOfNeighbor)
	{
		// if the type of neighbor is valid 
		if ((typeOfNeighbor > 0) && (typeOfNeighbor < numberOfPossibleState))
		{ nbNeighbor[typeOfNeighbor-1] ++; }
	}
	
	/**
	 * 
	 * @return 0 -> do not do anything
	 *  1 -> cell is a new born, create it's neighborhood
	 *  -1 -> cell has to be unload
	 */
	public int update()
	{
		// we want to see if the cell will be alive for each type of population
		
		// we go backward to put state 1 as the most likely
		
		// first if there is two neighbor of the same state, the cell stay as it is for now.
		int oldState = state;
		
		// except if there is 2 neighbor of the same type to the alive cell, set it to 0
		
		if (state !=0 && nbNeighbor[state-1] != 2)
		{ 
			state = 0;
		}
			
		for (int type=0; type<numberOfPossibleState; type++)
		{
			if (nbNeighbor[type] == 3) 
			{
				state = type+1; // update to the new state
				nbNeighbor = new int[5]; // set the number of neighbor to 0
				return (oldState == 0 && state != 0)?1:0; // return
			}
		}
		
		// if the cell is dead we have to determine if it has any neighbor		
		for (int n : nbNeighbor)
		{
			if (n!=0) // if a neighbor was found return 0
			{
				nbNeighbor = new int[5];
				return 0;					
			}
		}
		
		// if no neighbors where found, return -1 and unload the cell
		nbNeighbor = new int[5];
		return -1;
	}
}
