package aBall;

import java.awt.Color;

import acm.graphics.GLabel;
import acm.graphics.GOval;
import acm.graphics.GRect;
import acm.gui.TableLayout;
import acm.program.GraphicsProgram;
import acm.util.RandomGenerator;
import acm.program.*;
import java.awt.event.*;

import javax.swing.JComboBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeListener;

// bSim class sets up the Graphics display for each object instance 

/**
 * DESCRIPTION
 * This is the main program that runs/displays to graphics environment all the instructions provided to 
 * it by aBall and bTree. It runs everything based on the aBall and bSim template. 
 * @author Zhanna Klimanova
 *
 */
public class bSim extends GraphicsProgram implements ActionListener { 

	public static final double TPD = 1; 
	public static final int WIDTH = 1200; // screen coordinates 
	public static final int HEIGHT = 600; 
	public static final int OFFSET = 200;
	public static final double SCALE = HEIGHT/100.0; // pixels/meter
	
	public static final int NUMBALLS = 60; // balls to simulate
	private int NumBalls=NUMBALLS;
	
	private static final double MINSIZE = 1.0; // minimum ball radius (meters)
	private double MinSize=MINSIZE;
	
	private static final double MAXSIZE = 7.0;  // maximum ball radius (meters)
	private double MaxSize=MAXSIZE;
	
	private static final double EMIN = 0.2; // minimum loss coefficient 
	private double Emin=EMIN;

	private static final double EMAX = 0.6; // maximum loss coefficient 
	private double Emax=EMAX;
	
	private static final double VoMIN = 40.0; // minimum velocity (meters/sec)
	private double Vomin=VoMIN;
	
	private static final double VoMAX = 50.0; // maximum velocity (meters/sec)
	private double Vomax=VoMAX;
	
	private static final double ThetaMIN = 80.0; // minimum launch angle (degrees)
	private double Thetamin=ThetaMIN;
	
	private static final double ThetaMAX = 100.0; // maximum launch angle (degrees)
	private double Thetamax=ThetaMAX;
	
	/**
	 * ComboBoxes that are needed for user interaction 
	 */
	private JComboBox<String> bSimC;
	private JComboBox<String> FileC;
	private JComboBox<String> EditC;
	private JComboBox<String> HelpC;
	
	private JToggleButton trace; // Toggle button for trace enabling (enables trace ball by being clicked on/off)
	
	private bSim ourLink = null;
	
	RandomGenerator rgen = new RandomGenerator();
	bTree myTree = new bTree(); 
	
	/**
	 * Houses all the text fields that take input for changing parameters of the balls 
	 */
	JPanel panel;
	JTextField text1;
	JTextField text2;
	JTextField text3;
	JTextField text4;
	JTextField text5;
	JTextField text6;
	JTextField text7;
	JTextField text8;
	JTextField text9;
	
	boolean simEnable=false;
	
// setting the instance variables to constants 
	
// create ground plane rectangle/line with width=600 & height 3 pixels
	/**
	 * This run method implements the while loop needed to run the simulation and update the screen
	 */
	public void run() {
		
		while(true) {
			pause(200);
			if(simEnable) {
				doSim();
				bSimC.setSelectedIndex(0);
				simEnable=false;
			}
		}
	}
	/**
	 * init() method needed to run set up JLabels and execute other graphics instructions through ActionListeners
	 */
	public void init() {
	
//Screen layout
		
		this.resize(WIDTH, HEIGHT+OFFSET);
		
		GRect groundPlane = new GRect(0, HEIGHT, WIDTH, 3); 
		groundPlane.setFilled(true);
		groundPlane.setColor(Color.BLACK); 
		add(groundPlane);
	
		rgen.setSeed((long)424242); // define beforehand so seed is done b/fore random generation happens
	
		setChoosers(); // pull-down menu
		
		
		
/** Executing doSim()*/

		// Executing listeners MouseListeners() and JComboListeners

		panel = new JPanel();
		panel.setLayout(new TableLayout(11,2));
		add(panel, EAST);
		panel.add(new JLabel("General Simulation Parameters"), "gridwidth=2"); 
		panel.add(new JLabel(" "), "gridwidth=2"); 
		panel.add(new JLabel ("NUMBALLS   [1,255]:     "));
		text1 = new JTextField(4);
		text1.addActionListener(this);
		panel.add(text1);
		
		panel.add(new JLabel("MIN SIZE   [1.0-25.0]:     "));
		text2 = new JTextField(4);
		text2.addActionListener(this);
		panel.add(text2);
		
		panel.add(new JLabel("MAX SIZE   [1.0-25.0]:     "));
		text3 = new JTextField(4);
		text3.addActionListener(this);
		panel.add(text3);
		
		panel.add(new JLabel("LOSS MIN   [0.0-1.0]:     "));
		text4 = new JTextField(4);
		text4.addActionListener(this);
		panel.add(text4);
		
		panel.add(new JLabel("LOSS MAX   [0.0-1.0]:     "));
		text5 = new JTextField(4);
		text5.addActionListener(this);
		panel.add(text5);
		
		panel.add(new JLabel("MIN VEL   [1.0-200.0]:     "));
		text6 = new JTextField(4);
		text6.addActionListener(this);
		panel.add(text6);
		
		panel.add(new JLabel("MAX VEL   [1.0-200.0]:     "));
		text7 = new JTextField(4);
		text7.addActionListener(this);
		panel.add(text7);
		
		panel.add(new JLabel("THETA MIN   [1.0-180.0]:     "));
		text8 = new JTextField(4);
		text8.addActionListener(this);
		panel.add(text8);
		
		panel.add(new JLabel("THETA MAX [1.0-180.0]:     ")); 
		text9 = new JTextField(4);
		text9.addActionListener(this);
		panel.add(text9);
		
		// Trace button at the bottom 
		trace = new JToggleButton("Trace");
		trace.addActionListener(this);
		add(trace, SOUTH); 
		
	}
	

/** Placed the simulation into its own method. 
 *This executes the random generator simulation, updates constructor, updates bTree. 
 **/
	
public void doSim() {

	aBall someBall = null;
	for (int i=0; i<NumBalls; i+=1) {
		double bSize = rgen.nextDouble(MinSize, MaxSize);
		Color bColor = rgen.nextColor();
		double bLoss = rgen.nextDouble(Emin, Emax);
		double Vo = rgen.nextDouble(Vomin, Vomax);
		double theta = rgen.nextDouble(Thetamin, Thetamax);
		
//Adding someBall to screen 100 times w/ 100 different random parameters 
		
		someBall = new aBall((WIDTH/2)/SCALE, bSize, Vo, theta, bSize, bColor, bLoss, ourLink, bSimC); // change null to this to print trace points 
		//System.out.println((WIDTH/2)/SCALE + " " + bSize); debugging print statement 
		add(someBall.getBall());
		someBall.start();
		myTree.addNode(someBall); // adding the information of each ball to each tree node 
	
	}
}
/**
 * doStack() stacks the balls contained within bTree
 */

public void doStack() { 
	while (myTree.isRunning()); // this while loop will continue to be executed until myTree.isRunning() will be false 
	GLabel label = new GLabel ("Click mouse to continue", 1000, 500); // the label will appear after every ball stops moving 
	add(label);
//	waitForClick();
	myTree.stackBalls(); // executes the stackBalls() method 
	GLabel label2 = new GLabel ("All Stacked", 1000, 500);
	add(label2);
	label.setVisible(false); // this hides the initial label 
	add(label);
	
}
/**
 * addJComboListeners() adds the bSimC, FileC, EditC, HelpC to the NORTH
 */
public void addJComboListeners() {
	bSimC.addItemListener((ItemListener)this); 
	FileC.addItemListener((ItemListener)this);
	EditC.addItemListener((ItemListener)this);
	HelpC.addItemListener((ItemListener)this);
	
}
/**
 * setChoosers() adds the Run, Stack, Clear, Stop, Quit to the NORTH of the screen within bSimC as well as 
 * FileC, EditC, EditC, HelpC
 */
public void setChoosers() {
	bSimC = new JComboBox<String>();
	bSimC.addItem("Run");
	bSimC.addItem("Stack");
	bSimC.addItem("Clear");
	bSimC.addItem("Stop");
	bSimC.addItem("Quit"); 
	add(bSimC,NORTH);
	bSimC.addActionListener(this);
	
	FileC = new JComboBox<String>();
	FileC.addItem("File");
	add(FileC,NORTH);
	
	EditC = new JComboBox<String>();
	EditC.addItem("Edit");
	add(EditC,NORTH);
	
	HelpC = new JComboBox<String>();
	HelpC.addItem("Help");
	add(HelpC,NORTH);
}
/** 
 * actionPerformed() listens to the particular event, sets it to e, and executes program accordingly 
 */
	public void actionPerformed(ActionEvent e) {
		String stored = e.getActionCommand();
		
		
		if(e.getSource() == bSimC) {
			String value = (String) bSimC.getSelectedItem();
			
			if (value.equals("Run")) {
				
				simEnable=true; //when true it will re-run the program
			} else if (value.equals("Clear")) {
				removeAll(); // deleting the assignment of the balls to the display (not actually clearing bTree)
				myTree = new bTree(); // now I'm creating a new tree and putting balls into it 
				rgen.setSeed((long)424242);
				GRect groundPlane = new GRect(0, HEIGHT, WIDTH, 3); 
				groundPlane.setFilled(true);
				groundPlane.setColor(Color.BLACK); 
				add(groundPlane);
				
				
			} else if (value.equals("Stack")) {
				doStack(); 
			} else if (value.equals("Quit")) {
				System.exit(0); 
			} else if(value.equals("Stop")); 
		
			// Stores input of text fields and uses in program 
			
		} else if (e.getSource() == text1) { 
			NumBalls = Integer.parseInt(text1.getText());
			
		} else if (e.getSource() == text2) {
			MinSize = Double.parseDouble(text2.getText());	
		
		} else if (e.getSource() == text3) {
			MaxSize= Double.parseDouble(text3.getText());
		
		} else if (e.getSource() == text4) {
			Emin = Double.parseDouble(text4.getText());
		
		} else if (e.getSource() == text5) { 
			Emax = Double.parseDouble(text5.getText());
		
		} else if (e.getSource() == text6) {
			Vomin = Double.parseDouble(text6.getText());
		} else if (e.getSource() == text7) {
			Vomax = Double.parseDouble(text7.getText());
		} else if (e.getSource() == text8) {
			Thetamin = Double.parseDouble(text8.getText());
		} else if (e.getSource() == text9) {
			Thetamax = Double.parseDouble(text9.getText()); 
		} else if (e.getSource() == trace) { // if trace enabled, then trace happens 
			if (ourLink == null) ourLink = this;
			else if (ourLink == this) ourLink = null;
		}
	
	}
}

	

	

	
	
	

