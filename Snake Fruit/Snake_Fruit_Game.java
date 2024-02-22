import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.util.Random;

class GamePanel extends JPanel implements ActionListener {
	static final int SCREEN_WIDTH = 900;
	static final int SCREEN_HEIGHT = 900;
	static final int UNIT_SIZE = 30;
	static final int GAME_UNITS = (SCREEN_WIDTH * SCREEN_HEIGHT) / UNIT_SIZE;
	static final int DELAY = 75;
	final int[] x = new int[GAME_UNITS];
	final int[] y = new int[GAME_UNITS];
	int bodyPart = 5, fruitEaten = 0, fruitx, fruity;
	char direction = 'R';
	boolean running = false;
	Timer timer;
	Random random;

	GamePanel() {
		random = new Random();
		this.setPreferredSize(new Dimension(SCREEN_WIDTH, SCREEN_HEIGHT));
		this.setBackground(Color.BLACK);
		this.setFocusable(true);
		this.addKeyListener(new MyKeyAdapter());
		startGame();
	}

	public void startGame() {
		newFruit();
		running = true;
		timer = new Timer(DELAY, this);
		timer.start();
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		draw(g);
	}

	public void draw(Graphics g) {
		if (running) {
			for (int i = 0; i < SCREEN_HEIGHT / UNIT_SIZE; i++) {
				g.drawLine(i * UNIT_SIZE, 0, i * UNIT_SIZE, SCREEN_HEIGHT);
				g.drawLine(0, i * UNIT_SIZE, SCREEN_WIDTH, i * UNIT_SIZE);
			}
			g.setColor(Color.ORANGE);
			g.fillOval(fruitx, fruity, UNIT_SIZE, UNIT_SIZE);
			for (int i = 0; i < bodyPart; i++) {
				if (i == 0) {
					g.setColor(Color.green);
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				} else {
					g.setColor(new Color(128, 0, 128));
					g.setColor(new Color(random.nextInt(255), random.nextInt(255), random.nextInt(255)));
					g.fillRect(x[i], y[i], UNIT_SIZE, UNIT_SIZE);
				}
			}
			g.setColor(Color.red);
			g.setFont(new Font("Tahoma", Font.BOLD, 30));
			FontMetrics metrics = getFontMetrics(g.getFont());
			g.drawString("Score : " + fruitEaten, (SCREEN_WIDTH - metrics.stringWidth("Score : " + fruitEaten)) / 2, g.getFont().getSize());
		} else {
			gameOver(g);
		}
	}

	public void newFruit() {
		fruitx = random.nextInt((int) (SCREEN_WIDTH / UNIT_SIZE)) * UNIT_SIZE;
		fruity = random.nextInt((int) (SCREEN_HEIGHT / UNIT_SIZE)) * UNIT_SIZE;
	}

	public void move() {
		for (int i = bodyPart; i > 0; i--) {
			x[i] = x[i - 1];
			y[i] = y[i - 1];
		}
		switch (direction) {
		case 'U':
			y[0] = y[0] - UNIT_SIZE;
			break;
		case 'D':
			y[0] = y[0] + UNIT_SIZE;
			break;
		case 'L':
			x[0] = x[0] - UNIT_SIZE;
			break;
		case 'R':
			x[0] = x[0] + UNIT_SIZE;
			break;
		}
	}

	public void checkFruit() {
		if ((x[0] == fruitx) && (y[0] == fruity)) {
			bodyPart++;
			fruitEaten++;
			newFruit();
		}
	}

	public void checkCollision() {
		// checks colides with body
		for (int i = bodyPart; i > 0; i--) {
			if ((x[0] == x[i]) && (y[0] == y[i])) {
				running = false;
			}
		}
		// left border touch
		if (x[0] < 0)
			running = false;
		// right border touch
		if (x[0] > SCREEN_WIDTH)
			running = false;
		// top border touch
		if (y[0] < 0)
			running = false;
		// down border touch
		if (y[0] > SCREEN_HEIGHT)
			running = false;
		if (!running)
			timer.stop();
	}

	public void resetGame() {
		bodyPart = 5;
		fruitEaten = 0;
		direction = 'R';
		running = false;
		newFruit();
		for (int i = 0; i < bodyPart; i++) {
			x[i] = 0;
			y[i] = 0;
		}
		startGame();
	}

	public void gameOver(Graphics g) {
		// Display Score
		g.setColor(Color.red);
		g.setFont(new Font("Tahoma", Font.BOLD, 30));
		FontMetrics metrics2 = getFontMetrics(g.getFont());
		g.drawString("Score : " + fruitEaten, (SCREEN_WIDTH - metrics2.stringWidth("Score : " + fruitEaten)) / 2,
				g.getFont().getSize());
		// Game Over Screening
		g.setColor(Color.red);
		g.setFont(new Font("Tahoma", Font.BOLD, 50));
		FontMetrics metrics1 = getFontMetrics(g.getFont());
		g.drawString("Game Over!", (SCREEN_WIDTH - metrics1.stringWidth("Game Over!")) / 2, SCREEN_HEIGHT / 2);
		g.setColor(Color.green);
		g.setFont(new Font("Tahoma", Font.BOLD, 20));
		FontMetrics metrics3 = getFontMetrics(g.getFont());
		g.drawString("Press SPACE to Restart!", (SCREEN_WIDTH - metrics3.stringWidth("Press SPACE to Restart!")) / 2,
				SCREEN_HEIGHT / 2 + 50);
	}

	public void actionPerformed(ActionEvent arg0) {
		if (running) {
			move();
			checkFruit();
			checkCollision();
		}
		repaint();
	}

	class MyKeyAdapter extends KeyAdapter {
		public void keyPressed(KeyEvent e) {
			switch (e.getKeyCode()) {
			case KeyEvent.VK_LEFT:
				if (direction != 'R')
					direction = 'L';
				break;
			case KeyEvent.VK_RIGHT:
				if (direction != 'L')
					direction = 'R';
				break;
			case KeyEvent.VK_UP:
				if (direction != 'D')
					direction = 'U';
				break;
			case KeyEvent.VK_DOWN:
				if (direction != 'U')
					direction = 'D';
				break;
			case KeyEvent.VK_SPACE:
				if (!running) {
					resetGame();
				}
				break;
			}
		}
	}
}

class GameFrame extends JFrame {
	GameFrame() {
		this.add(new GamePanel());
		this.setTitle("Snake Fruit Game By Atish Kumar Sahu");
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"C:\\Users\\Atish kumar sahu\\eclipse-workspace\\Resume_Snake_Fruit_Game_In_Java\\src\\Snake_35967.png"));
		this.setResizable(false);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
	}
}

public class Snake_Fruit_Game {
	public static void main(String args[]) {
		new GameFrame();
	}
}