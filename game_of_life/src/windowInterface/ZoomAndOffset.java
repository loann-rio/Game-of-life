package windowInterface;


public class ZoomAndOffset {
	// of set is stored as a float to be able to keep track of sum of small variation
	// but is use as an integer as we don't want to cut a cell in half
	private int Xoffset = 0;
	private int Yoffset = 0;
	
	private float floatXoffset = 0;
	private float floatYoffset = 0;
	
	// w and h in number of visible cell
	private int widthGrid = 20;
	private int heightGrid = 20;
	
	// size of the grid on the window:
	private int widthWindow;
	private int heightWindow;
	
	// save starting position of the mouse during a drag of the grid
	private float startPosXOffset;
	private float startPosyOffset;
	
	// temporary offset during drag
	private float tempOffsetX = 0;
	private float tempOffsetY = 0;
	
	// cell size
	float sizeCell;

	
	public ZoomAndOffset(int newwidthWindow, int newheightWindow)
	{
		widthWindow = newwidthWindow;
		heightWindow = newheightWindow;
		
		sizeCell = (float) widthWindow / (float) widthGrid;
	}
	
	public int getTempOffsetX()
	{
		return (int) tempOffsetX;
	}
	
	public int getTempOffsetY()
	{
		return (int) tempOffsetY;
	}
	
	public void setStartDragNDrop(int x, int y)
	{
		startPosXOffset = x;
		startPosyOffset = y;
	}
	
	public void updateDragOffset(float x, float y)
	{
		tempOffsetX = (startPosXOffset-x)/sizeCell;
		tempOffsetY = (startPosyOffset-y)/sizeCell;
	}
	
	public void endOfDrag()
	{
		Xoffset += (int) tempOffsetX;
		Yoffset += (int) tempOffsetY;
		tempOffsetX = 0;
		tempOffsetY = 0;
	}
	
	/**
	 * change the offset by x and y 
	 * @param x
	 * @param y
	 */
	public void changeOffset(float x, float y)
	{
		
		Xoffset += (int) (x + floatXoffset);
		Yoffset += (int) (y + floatYoffset);
		
		floatXoffset = (x + floatYoffset) - (int) (x + floatYoffset);
		floatYoffset = (y + floatYoffset) - (int) (y + floatYoffset);
		
	}
	
	public int[] globalPosOfMouseToGridPos(int posMouseX, int posMouseY)
	{
		int x = (int) Math.floor((Xoffset + posMouseX/sizeCell));
		int y = (int) Math.floor(Yoffset + posMouseY/sizeCell);
		
		return new int[] { x, y }; 
	}
	
	/**
	 * when user zoom on the grid, we want to change it's size depending on the speed of the mouse wheel
	 * the offset has to be adapted to the position of the mouse to have a zoom on the mouse 
	 * @param changes: nb btw -3 and 3, the number is the speed of the mouse and negative is to zoom
	 * @param posMouseX
	 * @param posMouseY
	 */
	public void zoom(int changes, int posMouseX, int posMouseY)
	{
		
		int[] originalPos = globalPosOfMouseToGridPos(posMouseX, posMouseY); 
		
		widthGrid += changes * (float) (1f+widthGrid/40f);
		
		if (widthGrid < 5)
		{
			widthGrid = 5;
		}
		
		sizeCell = (float) widthWindow / (float) widthGrid;
		heightGrid = (int) (heightWindow/sizeCell) + 1;
		
		if (sizeCell <= 1)
		{
			widthGrid = widthWindow;
			sizeCell = (float) widthWindow / (float) widthGrid;
			heightGrid = (int) (heightWindow/sizeCell) + 1;
		}
		
		int[] newPos = globalPosOfMouseToGridPos(posMouseX, posMouseY); 
		
		// to have a zoom centered, the position of the mouse before the zoom has to be the same as after:
		
		Xoffset += originalPos[0] - newPos[0] ;
		Yoffset += originalPos[1] - newPos[1] ;
		
		
	}
	
	public float getCellSize()
	{
		return sizeCell;
	}
	
	public int getXoffset()
	{
		return (int) Xoffset;
	}
	
	public int getYoffset()
	{
		return (int) Yoffset;
	}
	
	public int getWidth()
	{
		return widthGrid;
	}
	
	public int getHeight()
	{
		return heightGrid;
	}
	
	/**
	 * set the parameters for the size of the window when changed and update height in consequence
	 * @param w width
	 * @param h height
	 */
	public void setSizeWindow(int w, int h)
	{
		widthWindow = w;
		heightWindow = h;
		
		sizeCell = (float)  widthWindow / (float)  widthGrid;
		
		if (sizeCell <= 1)
		{
			widthGrid = widthWindow;
			sizeCell = (float) widthWindow / (float) widthGrid;
			
		}
		heightGrid = (int) (heightWindow/sizeCell) + 1;
	}

	public void setRangeWindow(int[] rangeWindow) 
	{
		Xoffset = rangeWindow[0];
		Yoffset = rangeWindow[1];
		widthGrid = rangeWindow[2] - rangeWindow[0];
		sizeCell = (float) widthWindow / (float) widthGrid;
		heightGrid = (int) (heightWindow/sizeCell) + 1;

	}
}
