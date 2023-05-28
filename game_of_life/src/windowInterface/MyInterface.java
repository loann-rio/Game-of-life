package windowInterface;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import backend.Simulator;
import backend.FileLoader;

import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JCheckBox;

import java.awt.event.ActionListener;
import java.awt.event.ActionEvent;

import java.net.URI;
import java.awt.Desktop;


public class MyInterface extends JFrame {

	// Serial version UID to ensure consistent serialization of the object
	private static final long serialVersionUID = -6840815447618468846L;
	
	// The main panel for the interface
	private JPanel contentPane;
	
	// Labels for the current step and speed of the simulation
	private JLabel stepLabel;
	private JLabel speedLabel;
	
	// The panel where the simulation is drawn
	private JPanelDraw panelDraw;
	
	// The simulator object that runs the simulation
	private Simulator mySimu = null;
	
	// Sliders for controlling the randomness and speed of the simulation
	private JSlider randSlider;
	private JSlider speedSlider;
	
	// The file loader used to load and save simulation data
	private FileLoader fileLoader = new FileLoader();
	
	// Checkbox
	boolean paint = false;
	boolean DAD = false; // drag and drop
	JCheckBox btndraw = new JCheckBox("paint");
	JCheckBox DragNdropbtn = new JCheckBox("drag and drop");

	/**
	 * Create the frame.
	 */
	public MyInterface() 
	{

		// Set the properties of the main JFrame window
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		setBounds(500, 200, 500, 400);
		setTitle("Game of life");
		
		// Create the main content panel and set its layout
		contentPane = new JPanel();
		contentPane.setBorder(new EmptyBorder(5, 5, 5, 5));
		contentPane.setLayout(new BorderLayout(0, 0));
		setContentPane(contentPane);
		
		setJMenuBar(new JMenuBar());
	    getJMenuBar().add(createfileMenu());
	    getJMenuBar().add(createMenuRun());
	    getJMenuBar().add(createMenuAbout());

		// Create a panel for the buttons on the right side of the interface
		JPanel panelRight = new JPanel();
		panelRight.setLayout(new GridLayout(10, 1));
		contentPane.add(panelRight, BorderLayout.EAST);

		// Create a button to toggle the pause state of the simulation
		JButton btnPause = new JButton("Toggle Pause");
		btnPause.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicButtonPause();
			}
		});
		panelRight.add(btnPause);

		// Create a label for the current step of the simulation
		stepLabel = new JLabel("Step : X");
		panelRight.add(stepLabel);

		// Create a label for the speed slider
		speedLabel = new JLabel("speed slider : ");
		panelRight.add(speedLabel);

		// Create a slider for controlling the speed of the simulation
		speedSlider = new JSlider();
		speedSlider.setValue(3);
		speedSlider.setMinimum(0);
		speedSlider.setMaximum(10);
		speedSlider.setOrientation(SwingConstants.HORIZONTAL);
		speedSlider.setPreferredSize(new Dimension(100, 30));
		speedSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent arg0) {
				changeSpeed();
			}
		});
		panelRight.add(speedSlider);
		
		// density slider
		JLabel randLabel = new JLabel("random density slider :");
		panelRight.add(randLabel);

		randSlider = new JSlider();
		randSlider.setValue(50);
		randSlider.setMinimum(0);
		randSlider.setMaximum(100);
		randSlider.setPreferredSize(new Dimension(30, 200));
		panelRight.add(randSlider);
		
		
		// draw checkBox
		btndraw.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicButtonPaint();
				paint = !paint;
				if (paint) {
					DragNdropbtn.setSelected(false);
					DAD = false;
				}
			}
		});
		panelRight.add(btndraw);
		
		// drag and drop checkBox
		DragNdropbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicButtonDragNdrop();
				DAD = !DAD;
				if (DAD) {
					btndraw.setSelected(false);
					paint = false;
				}
			}
		});
		panelRight.add(DragNdropbtn);

		
		panelDraw = new JPanelDraw(this);
		contentPane.add(panelDraw, BorderLayout.CENTER);
		
		
	}
	
	private JMenu createfileMenu() {
	    JMenu menu = new JMenu("file");
	    
	    JMenu subMenuNew = new JMenu("new");
	    
	    // Create a button to stop the simulation
	    JMenuItem btnnew = new JMenuItem("empty");
	    btnnew.addActionListener(new ActionListener() {
 			public void actionPerformed(ActionEvent e) {
 				setSimuToZero();
 			}
 		});	 		
	    
	    subMenuNew.add(btnnew);
	    
	    JMenuItem btnRandGen = new JMenuItem("Random");
		btnRandGen.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				generateRandomBoard();
			}
		});
		
	    subMenuNew.add(btnRandGen);
	    menu.add(subMenuNew);
	    
	    menu.addSeparator();
	    
	    JMenuItem importbtn = new JMenuItem("import");
	    importbtn.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicLoadFileButton();
			}
		});
	    menu.add(importbtn);
	    
	    JMenuItem CSV = new JMenuItem("CSV");
	    CSV.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicSaveToFileButtonCSV();
			}
		});
	    
	    JMenuItem TXT = new JMenuItem("text");
	    TXT.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				clicSaveToFileButtonTXT();
			}
		});
	    
	    	    
	    JMenu subMenu = new JMenu("save");
	    
	    subMenu.add(CSV);
	    subMenu.add(TXT);

	    menu.add(subMenu);
	    
	    menu.addSeparator();
	    menu.add(new JMenuItem("close")).addActionListener(e -> this.dispose());;
	    return menu;
	}
	
	private JMenu createMenuRun() {
		JMenu menu = new JMenu("run");
		JMenuItem btnRun = new JMenuItem("run");
		btnRun.addActionListener(new ActionListener() {
 			public void actionPerformed(ActionEvent e) {
 				clicButtonGo();
 			}
 		});	 		
	    
		menu.add(btnRun);
		return menu;
	}

	private JMenu createMenuAbout() {
		JMenu menu = new JMenu("about");
		JMenuItem btnReadMe = new JMenuItem("ReadMe");
		
		
		btnReadMe.addActionListener(new ActionListener() {
 			public void actionPerformed(ActionEvent e) {
 				try {
 					// open readMe on Github
 	 				URI uri= new URI("https://github.com/loann-rio/Game-of-life");
 	 			    Desktop.getDesktop().browse(uri);
 				 
 				  } catch (Exception a) {
 				   
 				   a.printStackTrace();
 				  }
 				
 				
 			}
 		});	 		
	    
		menu.add(btnReadMe);
		return menu;
	}
	
	// set the step count to be show on the window
	public void setStepBanner(String s) {
		stepLabel.setText(s);
	}

	public JPanelDraw getPanelDessin() {
		return panelDraw;
	}
	
	/**
	 * instantiate the simu if it doesn't exist
	 */
	public void instantiateSimu() {
		if (mySimu == null) {
			mySimu = new Simulator(this);
			panelDraw.setSimu(mySimu);
		}
		fileLoader.setGrid(mySimu.grid);
	}
		
	public void setSimuToZero()
	{
		panelDraw.setOptionsOff();
		
		DragNdropbtn.setSelected(false);
		DAD = false;
		btndraw.setSelected(false);
		paint = false;
		
		mySimu = new Simulator(this);
		panelDraw.setSimu(mySimu);
		
		fileLoader.setGrid(mySimu.grid);
		
		this.eraseLabels();
		panelDraw.repaint();
	}

	public void clicButtonGo() 
	{
		this.instantiateSimu();
		if (!mySimu.isAlive()) {
			mySimu.start();
		} else {
			mySimu.togglePause();
		}
	}
	
	public void clicButtonPaint()
	{
		panelDraw.togglePaint();
	}

	public void clicButtonPause() 
	{
		if(mySimu != null) {
			mySimu.togglePause();
		}
	}
	
	public void clicButtonDragNdrop()
	{
		panelDraw.toggleDragNDrop();
	}
	
	public void generateRandomBoard() 
	{
		setSimuToZero();
		
		float chanceOfLife = ((float) randSlider.getValue()) / ((float) randSlider.getMaximum());
		mySimu.grid.generateRandom(chanceOfLife, panelDraw.zoomAndOffset.getXoffset(), panelDraw.zoomAndOffset.getYoffset(), (int) panelDraw.zoomAndOffset.getWidth(), (int) panelDraw.zoomAndOffset.getHeight());
		panelDraw.repaint();
	}

	public void changeSpeed() 
	{
		if (mySimu != null) {
			int delay = (int) Math.pow(2, 10 - speedSlider.getValue());
			mySimu.setLoopDelay(delay);
		} else {
			speedSlider.setValue(3);
		}
	}

	public void clicLoadFileButton() 
	{	
		setSimuToZero();
		this.eraseLabels();
		
		String fileName = SelectFile();
		if (fileName.length() > 0) {
			int[] rangeWindow = fileLoader.loadFile(fileName);
			panelDraw.zoomAndOffset.setRangeWindow(rangeWindow);
			this.repaint();
		}
	}

	public void clicSaveToFileButtonCSV()
	{
		if (mySimu != null)
		{
			String fileName = SelectFile();
			if (fileName.length() > 0) {
				fileLoader.writeFile(fileName);
			}
		}
	}
	
	public void clicSaveToFileButtonTXT()
	{
		if (mySimu != null)
		{
			String fileName = SelectFile();
			if (fileName.length() > 0) {
				fileLoader.writeFile(fileName);
			}
		}
	}

	public void update(int stepCount) {
		this.setStepBanner("Step : " + stepCount);
		this.repaint();
	}

	public void eraseLabels() {
		this.setStepBanner("Step : X");
		speedSlider.setValue(3);
	}
	
	public String SelectFile() {
		String s;
		JFileChooser chooser = new JFileChooser();
		chooser.setCurrentDirectory(new java.io.File("."));
		chooser.setDialogTitle("Choose a file");
		chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
		chooser.setAcceptAllFileFilterUsed(true);
		if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
			s = chooser.getSelectedFile().toString();
		} else {
			System.out.println("No Selection ");
			s = "";
		}
		return s;
	}

}
