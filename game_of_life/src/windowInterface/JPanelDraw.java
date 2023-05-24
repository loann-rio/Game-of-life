package windowInterface;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;


import javax.swing.JPanel;

import backend.Simulator;

public class JPanelDraw extends JPanel {

	private static final long serialVersionUID = 1L;
	private Simulator mySimu;
	private MyInterface interfaceGlobal;
	public ZoomAndOffset zoomAndOffset;
	
	private boolean DragAndDropForOffset = false; // is able to drag?
	private boolean drag = false; // is drag?
	
	private boolean paint = false; // paint tool: while the click is hold the cell under the cursor goes to state 1
	
	public JPanelDraw(MyInterface itf) {
		super();
		mySimu = null;
		interfaceGlobal = itf;
		
		// Initialize zoomAndOffset class with the w and h of the window
		zoomAndOffset = new ZoomAndOffset(getWidth(), getHeight());
		
		addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent me) {
				
				if (mySimu == null) {
					interfaceGlobal.instantiateSimu();
				}
				
				int[] posMouse = zoomAndOffset.globalPosOfMouseToGridPos(me.getX(), me.getY());
				
				if (! DragAndDropForOffset)
				{
					mySimu.grid.toggleCell(posMouse[0], posMouse[1]);
				}
				else
				{
					setCursor(new Cursor(Cursor.MOVE_CURSOR));
					drag = true;
					zoomAndOffset.setStartDragNDrop( me.getX(),  me.getY() );
				}
				repaint();
			}
			
			
			
			public void mouseReleased(MouseEvent me) {
				if (drag) {
					setCursor(new Cursor(Cursor.HAND_CURSOR));
					drag = false;
					zoomAndOffset.endOfDrag();
				}
		    }
		});
		
		addMouseMotionListener(new MouseMotionAdapter() {
			public void mouseDragged(MouseEvent me) {
				if (mySimu == null) {
					interfaceGlobal.instantiateSimu();
				}
				
		        if (DragAndDropForOffset && drag) {
		            // update the offset of the zoomAndOffset object based on mouse movement
		            zoomAndOffset.updateDragOffset(me.getX(), me.getY() );
		            repaint();
		        }
		        else if (paint) 
		        {
					int[] posMouse = zoomAndOffset.globalPosOfMouseToGridPos(me.getX(), me.getY());
					
					mySimu.grid.setCell(posMouse[0], posMouse[1], 1);
					repaint();
		        }
		    }
		});
		
		addMouseWheelListener(new MouseWheelListener() {
	        public void mouseWheelMoved(MouseWheelEvent e) {
	            zoomAndOffset.zoom(e.getWheelRotation(), e.getX(), e.getY());
	            repaint();
	        }
	    });
		
		addComponentListener(new ComponentAdapter() {
	        public void componentResized(ComponentEvent e) {
	            // update zoomAndOffset with the new width and height
	        	zoomAndOffset.setSizeWindow(getWidth(), getHeight());	
	            repaint();
	        }
	    });
	}

	public void setSimu(Simulator simu) {
		mySimu = simu;
	}
	
	public void toggleDragNDrop()
	{
		DragAndDropForOffset = !DragAndDropForOffset;
		
		if (DragAndDropForOffset)
		{
			paint = false;
			setCursor(new Cursor(Cursor.HAND_CURSOR));
		}
		else
		{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}
	
	public void setOptionsOff()
	{
		setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		DragAndDropForOffset = false;
		paint = false;
	}
	
	public void togglePaint()
	{
		paint = !paint;
		
		if (paint)
		{
			DragAndDropForOffset = false;
			setCursor(new Cursor(Cursor.CROSSHAIR_CURSOR));
		}
		else
		{
			setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		this.setBackground(Color.white);
		if (mySimu != null) {			
			// Draw Interface from state of simulator
			
			// as the height of the grid is fixed by the width in a way to have a cell as squared as possible width is close to height
			// but to be sure to no have a gap at the bottom, a small variation in height is allowed
			float cellsize = zoomAndOffset.getCellSize();
			
			
			// draw lines
			g.setColor(Color.gray);
			
			// if the grid is too small, no need to draw it
			if (cellsize > 5)
			{
				for (int x = 0; x < zoomAndOffset.getWidth(); x++) {
					int graphX = Math.round(x * cellsize);
					g.drawLine(graphX, 0, graphX, this.getHeight());
				}
				for (int y = 0; y < zoomAndOffset.getHeight()+1; y++) {
					int graphY = Math.round(y * cellsize);
					g.drawLine(0, graphY, this.getWidth(), graphY);
				}
								
			}
			
			
			// draw cells
			for (int x = 0; x <zoomAndOffset.getWidth(); x++) {
				for (int y = 0; y < zoomAndOffset.getHeight()+1; y++) {
					
					int cellContent = mySimu.grid.getCell(x+zoomAndOffset.getXoffset()+zoomAndOffset.getTempOffsetX(), y+zoomAndOffset.getYoffset()+zoomAndOffset.getTempOffsetY());
					
					switch (cellContent) {
				    case -1:
				        continue;
				    case 0:
				        //g.setColor(Color.gray);
				        continue;
				    case 1:
				        g.setColor(Color.black);
				        break;
				    case 2:
				        g.setColor(Color.red);
				        break;
				    case 3:
				        g.setColor(Color.blue);
				        break;
				    case 4:
				        g.setColor(Color.green);
				        break;
				    default:
				        g.setColor(Color.gray);
				        break;
					}
	
					g.fillRect((int) Math.round(x * cellsize), (int) Math.round(y * cellsize),
					        (int) Math.round(cellsize), (int) Math.round(cellsize));
				}
			}
		}
	}

}
