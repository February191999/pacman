package pacman;

import java.awt.*;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

import javax.swing.ImageIcon;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Model extends JPanel implements ActionListener{

	private Dimension d;
	private final Font smallFont = new Font("Arial", Font.BOLD, 14);
	private boolean inGame = false;
	private boolean dying = false;
	
	private final int BLOCK_SIZE = 24; //Size of blocks in game
	private final int N_BLOCKS = 19; //Number of blocks, 19 in width and 19 in height
	private final int SCREEN_SIZE = N_BLOCKS * BLOCK_SIZE; //Number of blocks and block size
	private final int MAX_GHOSTS = 12;
	private final int PACMAN_SPEED = 6;
	
	private int N_GHOSTS = 4; //Number of ghosts is set at 4 in the beginning
	private int lives, score; //simple integer variables for lives and score
	private int[] dx, dy; //dx and dy is needed for the position of the ghosts
	private int[] ghost_x, ghost_y, ghost_dx, ghost_dy, ghostSpeed; //Needed to determine the number and position of the ghosts
	
	private Image heart, ghost; //picture for heart and ghost
	private Image up, down, left, right; //picture for pacman animations
	
	private int pacman_x, pacman_y, pacmand_x, pacmand_y; //First 2 variables are x and y coordinates for pacman, last two are delta changes in horizontal and vertical directions 
	private int req_dx, req_dy; //Determined in the TAdapter in a class. These variables are controlled with the control keys
	
	private final int validSpeeds[] = {1,2,3,4,5,8}; //An array validSpeed for speed
	private final int maxSpeed = 6;
	
	private int currentSpeed = 3;
	private short[] screenData; //Take all the leveldata from array to draw the game
	private Timer timer; //Timer allows animation
	
	private final short levelData[] = {
	        
			19, 26, 26, 26, 18, 26, 26, 26, 22, 0, 19, 26, 26, 26, 18, 26, 26, 26, 22, 
	        21, 0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0, 21,
	        17, 26, 26, 26, 16, 26, 18, 26, 24, 26, 24, 26, 18, 26, 16, 26, 26, 26, 20, 
	        21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21,
	        25, 26, 26, 26, 20, 0, 25, 26, 22, 0, 19, 26, 28, 0, 17, 26, 26, 26, 28, 
	        0, 0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0, 0,
	        0, 0, 0, 0, 21, 0, 19, 26, 24, 18, 24, 26, 22, 0, 21, 0, 0, 0, 0,
	        0, 0, 0, 0, 21, 0, 21, 0, 0, 5, 0, 0, 21, 0, 21, 0, 0, 0, 0,
	        27, 26, 26, 26, 16, 26, 20, 0, 11, 8, 14, 0, 17, 26, 16, 26, 26, 26, 30,  
	        0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0,
	        0, 0, 0, 0, 21, 0, 17, 26, 26, 26, 26, 26, 20, 0, 21, 0, 0, 0, 0,
	        0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0,
	        19, 26, 26, 26, 16, 26, 24, 26, 22, 0, 19, 26, 24, 26, 16, 26, 26, 26, 22, 
	        21, 0, 0, 0, 21, 0, 0, 0, 21, 0, 21, 0, 0, 0, 21, 0, 0, 0, 21,
	        25, 26, 22, 0, 17, 26, 18, 26, 24, 26, 24, 26, 18, 26, 20, 0, 19, 26, 28, 
	        0, 0, 21, 0, 21, 0, 21, 0, 0, 0, 0, 0, 21, 0, 21, 0, 21, 0, 0,
	        19, 26, 24, 26, 28, 0, 25, 26, 22, 0, 19, 26, 28, 0, 25, 26, 24, 26, 22,
	        21, 0, 0, 0, 0, 0, 0, 0, 21, 0, 21, 0, 0, 0, 0, 0, 0, 0, 21,
	        25, 26, 26, 26, 26, 26, 26, 26, 24, 26, 24, 26, 26, 26, 26, 26, 26, 26, 28 
	        
	}; /*Used to create own levels with drawMaze() function. 19 in a row, 19 in a column. Each number represents a number to be displayed.
		0 stands for blue obstacle
		1 is the left border
		2 is the top border
		4 is the right border
		8 is the bottom border
		16 is the white dot that pacman collects
		The trick is to add the numbers together
		Example:
		Top Left is the number 19 = 1(left border)+ 2(top border) + 16 (white dot)
	 */
	
	public Model() {
		loadImages();
		initVariables(); //initializes variables
		addKeyListener(new TAdapter()); //Controller function
		setFocusable(true); //Focusable window
		initGame();//Starts the game
	}
	
	private void loadImages() {
		down = new ImageIcon("src/images/down.gif").getImage();
		up = new ImageIcon("src/images/up.gif").getImage();
		left = new ImageIcon("src/images/left.gif").getImage();
		right = new ImageIcon("src/images/right.gif").getImage();
		ghost = new ImageIcon("src/images/ghost.gif").getImage();
		heart = new ImageIcon("src/images/heart.png").getImage();
	}
	
	public void showIntroScreen(Graphics2D g2d) {
		
		String start = "Press SPACE to start";
		g2d.setColor(Color.yellow);
		g2d.drawString(start, (SCREEN_SIZE)/3, 225); //Define position
	}
	
	public void drawScore(Graphics2D g) {
		g.setFont(smallFont);
		g.setColor(new Color(5, 151, 75));
		String s = "Score: " + score;
		g.drawString(s, SCREEN_SIZE / 2 + 130, SCREEN_SIZE + 20);
		
		for (int i = 0; i < lives; i++) {
			g.drawImage(heart, i * 28 + 10, SCREEN_SIZE + 2, this);
		}
	}
	
	private void initVariables() {
		
		screenData = new short[N_BLOCKS * N_BLOCKS];
		d = new Dimension(500,500);
		ghost_x = new int [MAX_GHOSTS];
		ghost_dx = new int [MAX_GHOSTS];
		ghost_y = new int [MAX_GHOSTS];
		ghost_dy = new int [MAX_GHOSTS];
		ghostSpeed = new int [MAX_GHOSTS];
		dx = new int[4];
		dy = new int[4];
		
		timer = new Timer(60,this); //Takes care of animation. Timer indicates how often the images are redrawn. 40 is the number in milliseconds. Game is redrawn every milliseconds. This controls the speed of movement of the ghosts and pacman. I.E. 1000 moves slowly, 5 moves really fast
		timer.start();
	}
	
	private void initGame() {
		
		lives = 3; //Starting values for lives and score
		score = 0;
		initLevel(); //Then level is initialized
		N_GHOSTS = 4;//Define number of Ghosts and currentSpeed
		currentSpeed = 3;
	}
	
	private void initLevel() {
		
		int i; //To initialize level, create a for loop and copy the whole play field from the array levelData to a new array screenData[]
		for (i = 0; i < N_BLOCKS * N_BLOCKS; i++) {
			screenData[i] = levelData[i];
		}
		
		continueLevel();
	}
	
	private void playGame(Graphics2D g2d) { //Function is just a collection of other functions that are called and displays the graphics
		
		if (dying){
			
			death();
			
		} else {
			
			movePacman();
			drawPacman(g2d);
			moveGhosts(g2d);
			checkMaze();
		}
	}
	
	private void movePacman() {
		int pos;
		short ch;
		
		if (pacman_x % BLOCK_SIZE == 0 && pacman_y % BLOCK_SIZE == 0) { //in the first if query, the position of pacman is determined
			pos = pacman_x / BLOCK_SIZE + N_BLOCKS * (int) (pacman_y / BLOCK_SIZE); 
			ch = screenData[pos];
			
			if ((ch & 16) != 0) {
				screenData[pos] = (short) (ch & 15);
				score++; //If pacman is on the field with 16, the score is increased by 1
			}
			
			if (req_dx != 0 || req_dy != 0) { //With req_dx and req_dy, pacman is controlled
				if (!((req_dx == -1 && req_dy == 0 && (ch & 1) != 0) //The if query checks with ch if pacman is on one of the borders then pacman can't move in the corresponding direction
						|| (req_dx == 1 && req_dy == 0 && (ch & 4) != 0)		
						|| (req_dx == 0 && req_dy == -1 && (ch & 2) != 0)
						|| (req_dx == 0 && req_dy == 1 && (ch & 8) != 0))) {
					pacmand_x = req_dx;
					pacmand_y = req_dy;
				}
			}
			
			if (( pacmand_x == -1 && pacmand_y == 0 && (ch &1) != 0) //Checks for standstill
					|| (pacmand_x == 1 && pacmand_y == 0 && (ch & 4)!= 0)		
					|| (pacmand_x == 0 && pacmand_y == -1 && (ch & 2)!= 0)
					|| (pacmand_x == 0 && pacmand_y == 1 && (ch & 8)!= 0)){
				pacmand_x = 0; //pacmand_x and pacmand_y are set to 0
				pacmand_y = 0;
			}
		}
		
		pacman_x = pacman_x + PACMAN_SPEED * pacmand_x; //Speed is adjusted accordingly
		pacman_y = pacman_y + PACMAN_SPEED * pacmand_y;
		
	}
	
	public void drawPacman(Graphics2D g2d) {
		
		if (req_dx == -1) { //Checks which cursor button was pressed
			g2d.drawImage(left, pacman_x + 1, pacman_y + 1, this); //Corresponding pacman image is loaded for the different directions. If req_dx is -1, then the left image is loaded
		} else if (req_dx == 1) { 
			g2d.drawImage(right, pacman_x + 1, pacman_y + 1, this); 
		} else if (req_dy == -1) { 
			g2d.drawImage(up, pacman_x + 1, pacman_y + 1, this); 
		} else { 
			g2d.drawImage(down, pacman_x + 1, pacman_y + 1, this); 
		} 
	}
	
	public void moveGhosts(Graphics2D g2d) { //Allows ghosts to move automatically
		
		int pos;
		int count;
		
		for (int i = 0; i < N_GHOSTS; i++) { //Fist, set position of all six ghosts again using BLOCK_SIZE and the number of ghosts
			if (ghost_x[i] % BLOCK_SIZE == 0 && ghost_y[i] % BLOCK_SIZE == 0) { //The ghosts move on one square and then decide if they should change directions. We continue only if we have to finish moving on the square
				pos = ghost_x[i] / BLOCK_SIZE + N_BLOCKS * (int)(ghost_y[i] / BLOCK_SIZE);
				
				count = 0;
				
				if ((screenData[pos] & 1 ) == 0 && ghost_dx[i] != 1 ) {// Then we use the border information i.e. 1, 2, 4, and 8 to determine how the ghosts can move
					dx[count] = -1;
					dy[count] = 0;
					count++;
				}
				
				if ((screenData[pos] & 2 ) == 0 && ghost_dy[i] != 1 ) {
					dx[count] = 0;
					dy[count] = -1;
					count++;
				}
				
				if ((screenData[pos] & 4 ) == 0 && ghost_dx[i] != -1 ) {
					dx[count] = 1;
					dy[count] = 0;
					count++;
				}
				if ((screenData[pos] & 8 ) == 0 && ghost_dy[i] != -1 ) {
					dx[count] = 0;
					dy[count] = 1;
					count++;
				}
				
				if (count == 0) {
					
					if ((screenData[pos] & 15) == 15) {
						ghost_dy[i] = 0;
						ghost_dx[i] = 0;
					} else {
						ghost_dy[i] = -ghost_dy[i]; //The line determines where the ghost is located. In which position or square. There are 225 locations. Ghosts cannot move over walls. If there is no obstacle on the left and the ghost is not already moving to the right, then the ghost will move to the left
						ghost_dx[i] = -ghost_dx[i];
					}
					
				} else {
					
					count = (int)(Math.random() * count);
					
					if (count > 3) {
						count = 3;
					}
					
					ghost_dx[i] = dx[count];
					ghost_dy[i] = dy[count];
				}
			}
			
			ghost_x[i] = ghost_x[i] + (ghost_dx[i] * ghostSpeed[i]);
			ghost_y[i] = ghost_y[i] + (ghost_dy[i] * ghostSpeed[i]);
			drawGhost(g2d, ghost_x[i] + 1, ghost_y[i] + 1);//Loads image of the ghost we want to draw

			if (pacman_x > (ghost_x[i] - 12) && pacman_x < (ghost_x[i] + 12) //If pacman touches the ghosts, pacman loses a life
					&& pacman_y > (ghost_y[i] - 12) && pacman_y < (ghost_y[i] + 12)
					&& inGame) {
					dying = true;
			}
		}
	}
	
	private void drawGhost(Graphics2D g2d, int x, int y) {
		
		g2d.drawImage(ghost, x, y, this);
			
	}
	
	private void checkMaze(){//With the code in checkMaze() we check if there are any points(16) left for pacman to eat 
		
		int i = 0;
		boolean finished = true;
		
		while (i < N_BLOCKS * N_BLOCKS && finished) {
			
			if ((screenData[i] & 48) != 0) {
				finished = false;
			}
			
			i++;
		} 
		
		if (finished){ //If all points are consumed, we move to the next level, or in our case, just restart the game
			
			score += 50; //In addition, the score is increased by 50 and the ghost and speed are increased by 1
		
			if (N_GHOSTS < MAX_GHOSTS){
				N_GHOSTS++;
			}
		
			if (currentSpeed < maxSpeed){
				currentSpeed++;
			}
		
			initLevel();
		}	       
	}
	
	private void death(){//If pacman dies, one life is deducted and the game continues until pacman has no more lives
		
		lives--;
		
		if (lives == 0) {
			inGame = false;
		}
		
		continueLevel(); //Ghosts and pacman are put back at starting position
	}
	
	private void continueLevel() { //Function defines position of the ghosts
		
		int dx = 1;
		int random; // Also creates random speed for the ghosts
		
		for (int i = 0; i < N_GHOSTS; i++) {
			
			ghost_y[i] = 8 * BLOCK_SIZE; //start position
			ghost_x[i] = 9 * BLOCK_SIZE;
			ghost_dy[i] = 0;
			ghost_dx[i] = dx;
			dx = -dx;
			random = (int) (Math.random() * (currentSpeed +1));
			
			if (random > currentSpeed) {
				random = currentSpeed;
			} //Speed may only have the value which is also available in the array validSpeeds
			
			ghostSpeed[i] = validSpeeds[random];
			
		}
		//Start position of pacman
		
		pacman_x = 9 * BLOCK_SIZE;
		pacman_y = 10 * BLOCK_SIZE;
		pacmand_x = 0; //reset direction move
		pacmand_y = 0;
		req_dx = 0; //values of req_dx and req_dy are controlled by the cursor keys
		req_dy = 0;
		dying = false;
	}
	
	private void drawMaze(Graphics2D g2d) { //Game is now drawn just as defined above with 225 numbers with this function
		
		short i = 0;
		int x,y;
		
		for (y = 0; y < SCREEN_SIZE; y += BLOCK_SIZE){ //It is iterated in two for loops with SCREEN_SIZE and BLOCK_SIZE through the whole array. This will draw the x and y axis of the array
			for (x = 0; x < SCREEN_SIZE; x += BLOCK_SIZE){
				
				g2d.setColor(new Color(0, 72, 251)); //Color
				g2d.setStroke(new BasicStroke(5)); //Thickness of the border
				
				if ((levelData[i] == 0)) { //If on the field of the array is zero, it is colored blue
					g2d.fillRect(x, y, BLOCK_SIZE, BLOCK_SIZE);
				}
				
				if ((screenData[i] & 1) != 0) { //If on the field of the array is one, left border is drawn
					g2d.drawLine(x, y, x, y + BLOCK_SIZE - 1);
				}
				
				if ((screenData[i] & 2) != 0) { //Top Border
					g2d.drawLine(x, y, x + BLOCK_SIZE - 1, y);
				}
				
				if ((screenData[i] & 4) != 0) { //Right Border
					g2d.drawLine(x + BLOCK_SIZE -1, y, x + BLOCK_SIZE -1, y + BLOCK_SIZE - 1);
				}
				
				if ((screenData[i] & 8) != 0) { // Bottom Border
					g2d.drawLine(x, y + BLOCK_SIZE - 1, x + BLOCK_SIZE -1, y + BLOCK_SIZE - 1);
				}
				
				if ((screenData[i] & 16) != 0) { // White Dot
					g2d.setColor(new Color(255, 255, 255));
					g2d.fillOval(x + 10, y + 10, 6, 6);
				}
				
				i++;
			}
		}
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g); //With super, the constructor of the parent class is called
		
		Graphics2D g2d = (Graphics2D) g;
		
		g2d.setColor(Color.black); //sets bg color
		g2d.fillRect(0, 0, d.width, d.height); //Draw position with fillRect
		
		drawMaze(g2d);
		drawScore(g2d);
		
		if (inGame) {
			playGame(g2d);
		} else {
			showIntroScreen(g2d);
		}
		
		Toolkit.getDefaultToolkit().sync();
		g2d.dispose();
	}
	
	//controls
	class TAdapter extends KeyAdapter{
		
		@Override
		public void keyPressed(KeyEvent e) {
			
			int key = e.getKeyCode();
			
			if (inGame) {
				if (key == KeyEvent.VK_LEFT){
					req_dx = -1;//Variables req_dy and req_dx are used to control the x and y positions
					req_dy = 0; 
				}
				else if (key == KeyEvent.VK_RIGHT){
					req_dx = 1;
					req_dy = 0; 
				}
				else if (key == KeyEvent.VK_UP){
					req_dx = 0;
					req_dy = -1; 
				}
				else if (key == KeyEvent.VK_DOWN){
					req_dx = 0;
					req_dy = 1; 
				}
				else if (key == KeyEvent.VK_ESCAPE && timer.isRunning()){
					inGame = false;
				}//If timer is running and escape is pressed, then the game ends
			} else {
				if (key == KeyEvent.VK_SPACE) {
					inGame = true; //If space is pressed, game starts
					initGame(); //Function initGame is called
				}
			}
		}
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		repaint();
		
	}

}
