package aBall;

import java.awt.Color;
import javax.swing.JComboBox;
import acm.graphics.GLabel;
import acm.graphics.GOval;

// aBall class generates an instance of a ball in motion

/** DESCRIPTION 
 * This program builds a simulation for N bouncing balls 
 * with randomly chosen parameters. The bouncing balls are then organized
 * by the bTree class based on their radius size and stacked accordingly 
 * in the x and y directions.
 * 
 * @author Zhanna Klimanova 
 */
public class aBall extends Thread {

/** 
 * The constructor specifies the parameters for simulation for the aBall class:
 *  
 * @param Xi double The initial X position of the center of the ball
 * @param Yi double The initial Y position of the center of the ball
 * @param Vo double The initial velocity of the ball at launch
 * @param theta double Launch angle (with the horizontal plane)
 * @param bSize double The bSize of the ball in simulation units 
 * @param bColor Color The initial color of the ball
 * @param bLoss double Fraction [0,1] of the energy lost on each bounce 
 * @param isRunning is the test condition to check whether an individual ball has stopped moving within the simulation 
 */

	private double Xi; // initial X position of center of ball (meters)
	private double Yi; // initial Y position of center of ball (meters)
	private double Vo; // initial velocity of the ball at launch (meter/second)
	private double theta; // launch angle (w/ horizontal plane) (degrees)
	private double bSize; // size of ball (meters)
	private Color bColor; // initial color of ball 
	private double bLoss; // energy loss coefficient of ball at each bounce [0,1] 
	private GOval ball;
	private volatile boolean isRunning=true;
	
	private bSim link; // link is related to trace parameters. Connects link to ourLink in bSim
	private JComboBox<String> link2; // link2 allows aBall to connect to bSim execution when "Stop" is pressed
	private static final double TPD=1;
	private static final double g = 9.8; // gravitational constant (m/s^2)
	private static final double TICK = 0.1; // clock tick duration (seconds) 
	private static final int HEIGHT = 600; 
	private static final int WIDTH = 1200; // screen coordinates 
	private static final double k = 0.0001; // parameter k for vt
	private static final double SCALE = HEIGHT/100; // pixels/meter
	private static final double Pi = 3.1415926535; 
	private static final double ETHR = 0.01; // if vx or vy < ETHR then stop I CHANGED THIS to 0.5 to work
	private static final boolean TEST = true; // print of test is true 
	
	public aBall (double Xi, double Yi, double Vo, double theta, double bSize, Color bColor, double bLoss, bSim link, JComboBox<String> link2) {
		
// Create instance of ball of size 2x the input radius
		
		ball = new GOval(Xi, Yi, (2 * (SCALE * bSize)), (2 * (SCALE * bSize)));
		ball.setFilled(true);
		ball.setFillColor(bColor); //
	
// Get the simulation parameters 
	// ***
		this.Xi=Xi;
		this.Yi=Yi;
		this.Vo=Vo;
		this.theta=theta;
		this.bSize=bSize;
		this.bColor=bColor;
		this.bLoss=bLoss;
		this.link = link;
		this.link2 = link2;
	} //***
	
/** 
 * Once the start method is called on the aBall instance, 
 * the code in the run method is executed concurrently with the main
 * program. 
 * @param void
 * @return
 */

// Bounce simulation 

		public void run() {
		
// Initialize simulation parameters 
			
			
			double v0x = Vo * Math.cos(theta * (Pi/180)); // initial launch velocity in x direction
			double v0y = Vo * Math.sin(theta * (Pi/180)); // initial launch velocity in y direction
			double vt = g / (4 * Pi * (bSize* bSize) * k); // terminal velocity (force air resistance balances gravity)
			double Xlast = 0;
			double Ylast = 0; 
			double X0 = Xi; // offset so trajectories do not overlap
			double time = 0;
			double KEx = 0.5 * v0x * v0x * 1.0; 
			double KEy = 0.5 * v0y * v0y * 1.0; 
			double KEx_last = KEx;
			double KEy_last = KEy;
			
// LOOP STARTS 
			
			double TotalEnergylast=0.5*Vo*Vo;
			double TotalEnergy=TotalEnergylast;

			int conversion = (-1); // Conversion for the condition of left-direction projectiles
			
			while (isRunning) {
				double X = (v0x*vt/g*(1 - Math.exp(-g*time/vt))); // x position of ball at different times starting at Xinit position 
				double Y = bSize+ vt/g*(v0y + vt)*(1 - Math.exp(-g*time/vt)) - vt*time; // y position of ball at different times
				double Xb = X - bSize; // Starting X at center.....Cartesian coordinate for x 
				double Yb = Y + bSize; // Starting Y at center.....Cartesian coordinate for y (y is up)
				double Vx=(X-Xlast)/TICK;
				double Vy=(Y-Ylast)/TICK;
				Xlast = X;
				Ylast = Y;
				
// Conditions to detect collision 
				
				if (Vy < 0 && Y <= bSize) {
					KEx = 0.5 * Vx*Vx*(1-bLoss);
					KEy = 0.5 * Vy*Vy*(1-bLoss);
					if (theta>90) { // Condition to make left-direction bounce
						v0x = conversion*(Math.sqrt(2*KEx));
					} else v0x = (Math.sqrt(2*KEx));
					
// Recompute v0 in x & y because of collision energy loss
					
					v0y = Math.sqrt(2*KEy); 
					Y = bSize; 
					X0 += X; 
					X = 0; // Essentially move coordinate system to start loop all over again 
					time = 0;
					
					TotalEnergylast = KEx_last + KEy_last;
					TotalEnergy = KEx + KEy;
					KEx_last=KEx;
					KEy_last=KEy;
					
// CODE for displaying kinetic energies (also helpful for debugging purposes) 
					/*
					System.out.println("This is KEx: " +KEx);
					System.out.println("This is KEy: " +KEy);
					System.out.println("This is TotalEnergy: " + TotalEnergy);
					System.out.println("This is TotalEnergylast: " +TotalEnergylast);
					*/
				}
				
				double Sx = (X0 +  X - bSize) * (SCALE); // for ball's screen position use real-world coordinates 
				double Sy = HEIGHT - ((Y + bSize)*(SCALE)); 
				ball.setLocation(Sx, Sy); 
				time += TICK;	
				if (link != null) { // trace point! B/c link in bSim is set to null, the trace point is not created
					GOval trace = new GOval(Sx + (SCALE * bSize), Sy + (SCALE * bSize), TPD, TPD);
					trace.setFilled(true);
					trace.setColor(this.bColor);
					link.add(trace);
				}
					
				
// Code to stop the ball, isRunning variable set to false to signify that an individual ball has stopped moving
				
				if (TotalEnergy<ETHR || TotalEnergylast<TotalEnergy || ((String)link2.getSelectedItem()).equals("Stop") || ((String)link2.getSelectedItem()).equals("Stack")) {
					isRunning=false;
					link = null;
				}

				
				//if (TEST) // test conditions 
					//System.out.printf("t: %.2f X: %2f Y: %.2f Vx: %.2f Vy: %.2f\n",  time,X0+X, Y, Vx, Vy); 
				
// Thread class is equivalent of pause 
				
				try { 
					Thread.sleep(10);
				} catch(InterruptedException e) {
					e.printStackTrace();
				} 
			}
		}
		
/**
 * getBall() method needed to return ball so that bSim can output it to the graphics environment 
 * @return ball 
 */
		public GOval getBall() {  
			return ball; 
		}
		
// Methods used in bSim and bTree
		
/** getbSize() method used to get each ball's radius in order to store in bTree and later use to stack the balls chronologically 
 * @return
 */
		public double getbSize() { 
			return bSize; 
		}
/** getbState() Checks if simulation is running; used in bTree in traverse() method 
 * @return
 */
		public boolean getbState() {
			return isRunning; 
		}
/** moveTo() sets the location of each ball in the stack; used in bTree class in the traverse_inorder() 
 * @param x position of ball in x (stacking on side)
 * @param y position of ball in y (stacking on top)
 */
		public void moveTo(double x, double y) {
			double Sx = (x - bSize) * (bSim.SCALE); // for ball's screen position use real-world coordinates 
			double Sy = bSim.HEIGHT - ((y + bSize)*(bSim.SCALE)); 
			ball.setLocation(Sx, Sy); 
		
		}
		}
			
		
	

