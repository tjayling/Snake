package com.tom.Snake;

import java.awt.*;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;
import javax.swing.*;
import java.util.Random;

public class GamePanel extends JPanel implements ActionListener{
	BufferedImage snakeHead;
	BufferedImage snakeBody;
	BufferedImage snakeCorner;
	BufferedImage snakeTail;
	Image apple;
	Image floor;
	
	static final int SCREEN_WIDTH = 800;
	static final int SCREEN_HEIGHT = 600;
	static final int UNIT_SIZE = 20;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT)/UNIT_SIZE;
	static final int DELAY = 100;
	static int[] x;
	static int[] y;
	static int bodyParts = 6;
	int applesEaten = 0;
	static char direction = 'R';
	boolean running = false;
	int appleX;
	int appleY;
	Timer timer;
	Random random;
	
	
	GamePanel() {
		try {
			loadImages();
		} catch(IOException e) {};
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.black);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	};
	
	private void loadImages() throws IOException {
		//importing images for the snake's body parts.
		snakeHead = ImageIO.read(new File("src/images/snake_head.png"));
		snakeBody = ImageIO.read(new File("src/images/snake_body.png"));
		snakeCorner = ImageIO.read(new File("src/images/snake_corner.png"));
		snakeTail = ImageIO.read(new File("src/images/snake_tail.png"));

		//importing icons for the background image and apple image.
		ImageIcon appleICO = new ImageIcon("src/images/apple.png");
		apple = appleICO.getImage();
		
		ImageIcon floorICO = new ImageIcon("src/images/floor.png");
		floor = floorICO.getImage();
	}
	
	public void startGame() {
		//Initialising variables that need to be set and reset at the start of each game.
		bodyParts = 6;
		applesEaten = 0;
		direction = 'R';
		x = new int[GAME_UNITS];
		y = new int[GAME_UNITS];
		newApple();
		
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}
	
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		//drawing the background image on to the screen.
		g.drawImage(floor, 0, 0, null);		
		if (running) {
			g.drawImage(apple, appleX, appleY, null);
			g.setColor(Color.RED);
			for (int i = 0; i<bodyParts; i++) {
				if (i == 0) {
					Image head = rotate(snakeHead, rotation(i));
					g.drawImage(head, x[i], y[i], null);
				}
				else if (i == bodyParts-1) {
					Image tail = rotate(snakeTail, rotation(i));
					g.drawImage(tail,  x[i],  y[i],  null);
				}
				else {
					if (isCorner(i)) {
						Image corner = rotate(snakeCorner, rotation(i));
						g.drawImage(corner, x[i], y[i], null);
					} else {
						Image body = rotate(snakeBody, rotation(i));
						g.drawImage(body, x[i], y[i], null);
					}
				}
			}
			g.setColor(new Color(255, 70, 0));
			g.setFont(new Font("Sans_Serif", Font.BOLD, 25));
			FontMetrics metrics = getFontMetrics(g.getFont());
			String string = "Score: " + applesEaten;
			g.drawString(string, SCREEN_WIDTH - metrics.stringWidth(string) - 20, 30);
		
		}
		else {
			gameOver(g);
		}
	}
	
	public void newApple() {
		appleX = random.nextInt((int)SCREEN_WIDTH/UNIT_SIZE)*UNIT_SIZE;
		appleY = random.nextInt((int)SCREEN_HEIGHT/UNIT_SIZE)*UNIT_SIZE;
	}
	
	public void move() {
		for (int i = bodyParts; i > 0; i--) {
			x[i] = x[i-1];
			y[i] = y[i-1];
		}
		
		switch(direction) {
		case('U'):
			y[0] = (y[0] - UNIT_SIZE);
			if (y[0] < 0) {
				y[0] += SCREEN_HEIGHT;
			};
			break;
		case('D'):
			y[0] = (y[0] + UNIT_SIZE) % SCREEN_HEIGHT;
			break;
		case('L'):
			x[0] = (x[0] - UNIT_SIZE) % SCREEN_WIDTH;
			if (x[0] < 0) {
				x[0] += SCREEN_WIDTH;
			};
			break;
		case('R'):
			x[0] = (x[0] + UNIT_SIZE) % SCREEN_WIDTH;
		}
			
	}
	
	public void checkApple() {
		if ((x[0] == appleX) && (y[0] == appleY)) {
			bodyParts ++;
			applesEaten ++;
			newApple();
		}
	}
	
	public void checkColisions() {
		for (int i = bodyParts; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		
		if (!running) {
			timer.stop();
		}
	}
	
	public void gameOver(Graphics g) {
		//Game over text;
		g.setColor(new Color(255, 20, 0));
		String go = "Game Over!";
		String score = "Score: " + applesEaten;
		g.setFont(new Font("Sans_Serif", Font.BOLD, 75));
		FontMetrics metrics = getFontMetrics(g.getFont());
		g.drawString(go, (SCREEN_WIDTH - metrics.stringWidth(go))/2, SCREEN_HEIGHT/2);
		g.setFont(new Font("Sans_Serif", Font.BOLD, 40));
		g.setColor(new Color(255, 70, 0));
		metrics = getFontMetrics(g.getFont());
		g.drawString(score, (SCREEN_WIDTH - metrics.stringWidth(score))/2, (SCREEN_HEIGHT/2) + 50);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		if (running) {
			move();
			checkApple();
			checkColisions();
		}
		repaint();
	}
	
	public static Image rotate(BufferedImage image, int angle) {
		BufferedImage rotated = new BufferedImage(20, 20, image.getType());
		Graphics2D g = rotated.createGraphics();
		g.rotate(Math.toRadians(angle), 10, 10);
		g.drawImage(image, null, 0, 0);
		g.dispose();
		ImageIcon imgIco = new ImageIcon(rotated);
		return imgIco.getImage();
	}
	
	public static boolean isCorner(int index) {
		//Check is the snake is moving in a straight line.
		if ((x[index] == 0 && (x[index-1] == SCREEN_WIDTH-UNIT_SIZE || x[index+1] == SCREEN_WIDTH-UNIT_SIZE)) || (x[index] == SCREEN_WIDTH-UNIT_SIZE && (x[index-1] == 0 || x[index+1] == 0))
		 || (y[index] == 0 && (y[index-1] == SCREEN_HEIGHT-UNIT_SIZE || y[index+1] == SCREEN_HEIGHT-UNIT_SIZE))|| (y[index] == SCREEN_HEIGHT-UNIT_SIZE && (y[index-1] == 0 || y[index+1] == 0))) {
		 return false;
		}
		else if ((x[index-1] < x[index] && x[index] < x[index + 1]) || (x[index-1] > x[index] && x[index] > x[index + 1]) || (y[index-1] < y[index] && y[index] < y[index + 1]) || (y[index-1] > y[index] && y[index] > y[index + 1])) {
			return false;
		} else {
			return true;
		}
	}
	
	public static int rotation(int index) {
		if (index == 0) {
			switch (direction) {
			case 'U':
				return 0;
			case 'D':
				return 180;
			case 'L':
				return 270;
			case 'R':
				return 90;
			}
		}
		else if (index == bodyParts-1) {
			if (x[index-1] < x[index]) {
				return 270;
			}
			else if (x[index-1] > x[index]) {
				return 90;
			}
			else if (y[index-1] > y[index]) {
				return 180;
			}
			else if (y[index-1] < y[index]) {
				return 0;
			}
		}
		else {
			if (x[index] == 0 && (x[index-1] == SCREEN_WIDTH-UNIT_SIZE || x[index+1] == SCREEN_WIDTH-UNIT_SIZE) || (x[index] == SCREEN_WIDTH-UNIT_SIZE && (x[index-1] == 0 || x[index+1] == 0))) {
				return 90;
			}
			else if ((x[index-1] < x[index] && x[index] < x[index + 1]) || (x[index-1] > x[index] && x[index] > x[index + 1])) {
				return 90;
			}
			else if ((x[index-1] < x[index] && y[index+1] < y[index]) || (x[index+1] < x[index] && y[index-1] < y[index])) {
				return 270;
			}
			else if ((x[index-1] < x[index] && y[index+1] > y[index]) || (x[index+1] < x[index] && y[index-1] > y[index])) {
				return 180;
			}
			else if ((x[index-1] > x[index] && y[index+1] > y[index]) || (x[index+1] > x[index] && y[index-1] > y[index])) {
				return 90;
			}
		}
		return 0;
	}
	
	public class MyKeyAdapter extends KeyAdapter{
		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()) {
			case KeyEvent.VK_W:
			case KeyEvent.VK_UP:
				if (direction != 'D') {
					direction = 'U';
				}
				break;
			case KeyEvent.VK_S:
			case KeyEvent.VK_DOWN:
				if (direction != 'U') {
					direction = 'D';
				}
				break;
			case KeyEvent.VK_A:
			case KeyEvent.VK_LEFT:
				if (direction != 'R') {
					direction = 'L';
				}
				break;
			case KeyEvent.VK_D:
			case KeyEvent.VK_RIGHT:
				if (direction != 'L') {
					direction = 'R';
				}
				break;
			case KeyEvent.VK_ESCAPE:
				break;
			case KeyEvent.VK_ENTER:
				if (!running) {
					startGame();
				}
				break;
			}
		}
	}

}
