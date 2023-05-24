package backend;

import windowInterface.MyInterface;

public class Simulator extends Thread {

	private MyInterface mjf;
	
	private boolean stopFlag;
	private boolean pauseFlag;
	
	
	private int loopDelay;
	
	public Grid grid = new Grid();
		
	public Simulator(MyInterface mjfParam) {
		mjf = mjfParam;
		
		stopFlag=false;
		pauseFlag=false;
		
		loopDelay = 150;
	}

	/**
	 * Stops simulation by raising the stop flag used in the run method
	 */
	public void stopSimu() {
		stopFlag = true;
	}
	

	/**
	 * Toggles Pause of simulation 
	 * by raising or lowering the pause flag used in the run method
	 */
	public void togglePause() {
		pauseFlag = !pauseFlag;
	}
	
		
	/**
	 * Setter for the delay between steps of the simulation
	 * @param delay in milliseconds
	 */
	public void setLoopDelay(int delay) {
		loopDelay = delay;
	}
	
	/**
	 *  
	 */
	public void run() {
		int stepCount=0;
		while(!stopFlag) {
			stepCount++;
			grid.makeStep();
			
			mjf.update(stepCount);
			try {
				Thread.sleep(loopDelay);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			while(pauseFlag && !stopFlag) {
				try {
					Thread.sleep(loopDelay);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
		}
	}


}
