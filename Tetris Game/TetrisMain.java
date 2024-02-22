import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.ArrayList;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

class GamePanel extends JPanel implements Runnable {
	public static final int WIDTH = 900;
	public static final int HEIGHT = 900;
	final int FPS = 60;
	Thread gameThread;
	playManager pm;

	public GamePanel() {
		this.setPreferredSize(new Dimension(WIDTH, HEIGHT));
		this.setBackground(Color.BLACK);
		this.setLayout(null);
		this.addKeyListener(new keyHandler());
		this.setFocusable(true);
		pm = new playManager();
	}

	public void launchGame() {
		gameThread = new Thread(this);
		gameThread.start();
	}

	@Override
	public void run() {
		double drawInterval = 1000000000 / FPS;
		double delta = 0;
		long lastTime = System.nanoTime();
		long currentTime;
		while (gameThread != null) {
			currentTime = System.nanoTime();
			delta += (currentTime - lastTime) / drawInterval;
			lastTime = currentTime;
			if (delta >= 1) {
				update();
				repaint();
				delta--;
			}
		}
	}

	private void update() {
		if (keyHandler.pausePressed == false && pm.gameOver == false) {
			pm.update();
		}
	}

	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2 = (Graphics2D) g;
		pm.draw(g2);
	}

}

class playManager {
	final int WIDTH = 560;// 28
	final int HEIGHT = 840;// 42
	public static int left_X;
	public static int right_X;
	public static int top_Y;
	public static int bottom_Y;

	Mino currentMino;
	final int MINO_START_X;
	final int MINO_START_Y;

	Mino nextMino;
	final int NEXTMINO_X;
	final int NEXTMINO_Y;
	public static ArrayList<Block> staticBlocks = new ArrayList<>();

	public static int dropInterval = 60;
	boolean gameOver;

	boolean EffectCounterOn;
	int effectCounter;
	ArrayList<Integer> effect = new ArrayList<>();

	int level = 1;
	int lines, score;

	playManager() {
		left_X = (GamePanel.WIDTH / 3) - (WIDTH / 2);
		right_X = left_X + WIDTH;
		top_Y = 20;
		bottom_Y = top_Y + HEIGHT;
		MINO_START_X = left_X + (WIDTH / 2) - Block.size;
		MINO_START_Y = top_Y + Block.size;

		NEXTMINO_X = right_X + 165;
		NEXTMINO_Y = top_Y + 700;

		currentMino = pickMino();
		currentMino.setXY(MINO_START_X, MINO_START_Y);

		nextMino = pickMino();
		nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);
	}

	private Mino pickMino() {
		Mino mino = null;
		int i = new Random().nextInt(7);
		switch (i) {
		case 0:
			mino = new MinoL1();
			break;
		case 1:
			mino = new MinoL2();
			break;
		case 2:
			mino = new MinoSquare();
			break;
		case 3:
			mino = new MInoBar();
			break;
		case 4:
			mino = new MinoT();
			break;
		case 5:
			mino = new MinoZ1();
			break;
		case 6:
			mino = new MinoZ2();
			break;
		}
		return mino;
	}

	public void update() {
		if (currentMino.active == false) {
			staticBlocks.add(currentMino.b[0]);
			staticBlocks.add(currentMino.b[1]);
			staticBlocks.add(currentMino.b[2]);
			staticBlocks.add(currentMino.b[3]);

			if (currentMino.b[0].x == MINO_START_X && currentMino.b[0].y == MINO_START_Y) {
				gameOver = true;
			}

			currentMino.deactivating = false;

			currentMino = nextMino;
			currentMino.setXY(MINO_START_X, MINO_START_Y);
			nextMino = pickMino();
			nextMino.setXY(NEXTMINO_X, NEXTMINO_Y);

			checkDelete();

		} else {
			currentMino.update();
		}
	}

	private void checkDelete() {
		int x = left_X;
		int y = top_Y;
		int blockCount = 0;
		int lineCount = 0;

		while (x < right_X && y < bottom_Y) {
			for (int i = 0; i < staticBlocks.size(); i++) {
				if (staticBlocks.get(i).x == x && staticBlocks.get(i).y == y) {
					blockCount++;
				}
			}
			x += Block.size;
			if (x == right_X) {
				if (blockCount == 28) {
					EffectCounterOn = true;
					effect.add(y);
					for (int i = staticBlocks.size() - 1; i > -1; i--) {
						if (staticBlocks.get(i).y == y) {
							staticBlocks.remove(i);
						}
					}
					lineCount++;
					lines++;
					if (lines % 10 == 0 && dropInterval > 1) {
						level++;
						if (dropInterval > 10) {
							dropInterval -= 10;
						} else {
							dropInterval -= 1;
						}
					}
					for (int i = 0; i < staticBlocks.size(); i++) {
						if (staticBlocks.get(i).y < y) {
							staticBlocks.get(i).y += Block.size;
						}
					}
				}
				blockCount = 0;
				x = left_X;
				y += Block.size;
			}
		}
		if (lineCount > 0) {
			int singlelIneScore = 10 + level;
			score += singlelIneScore + lineCount;
		}
	}

	public void draw(Graphics2D g) {
		g.setColor(Color.WHITE);
		g.setStroke(new BasicStroke(4f));
		g.drawRect(left_X - 4, top_Y - 4, WIDTH + 8, HEIGHT + 8);
		int x = right_X + 20;
		int y = bottom_Y - 250;
		g.drawRect(x, y, 280, 250);
		g.setFont(new Font("Arial", Font.PLAIN, 20));
		g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
		g.drawString("Next Shape:", x + 10, y + 30);

		g.drawRect(right_X + 20, bottom_Y - 650, 280, 300);
		x += 40;
		y = top_Y + 250;
		g.drawString("Level : " + level, x, y);
		y += 70;
		g.drawString("Lines : " + lines, x, y);
		y += 70;
		g.drawString("Score : " + score, x, y);

		if (currentMino != null) {
			currentMino.draw(g);
		}

		nextMino.draw(g);

		for (int i = 0; i < staticBlocks.size(); i++) {
			staticBlocks.get(i).draw(g);
		}

		if (EffectCounterOn) {
			effectCounter++;
			g.setColor(Color.red);
			for (int i = 0; i < effect.size(); i++) {
				g.fillRect(left_X, effect.get(i), WIDTH, Block.size);
			}
			if (effectCounter == 10) {
				EffectCounterOn = false;
				effectCounter = 0;
				effect.clear();
			}
		}

		g.setColor(Color.YELLOW);
		g.setFont(g.getFont().deriveFont(50f));
		if (gameOver) {
			x = left_X + 125;
			y = top_Y + 320;
			g.drawString("Game Over!", x, y);
		} else if (keyHandler.pausePressed) {
			x = left_X + 120;
			y = top_Y + 320;
			g.drawString("Pause Game!", x, y);
		}
		x = 630;
		y = top_Y + 50;
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 40));
		g.drawString("Welcome To", x, y);

		x = 630;
		y = top_Y + 100;
		g.setColor(Color.white);
		g.setFont(new Font("Times New Roman", Font.BOLD, 40));
		g.drawString("Titris Game!", x, y);

	}
}

class Block extends Rectangle {
	public int x, y;
	public static final int size = 20;
	public Color c;

	Block(Color c) {
		this.c = c;
	}

	public void draw(Graphics2D g) {
		int margin = 2;
		g.setColor(c);
		g.fillRect(x + margin, y + margin, size - (margin * 2), size - (margin * 2));
	}
}

class Mino {
	public Block b[] = new Block[4];
	public Block tempB[] = new Block[4];
	int autoDropCount = 0;
	public int directon = 1;
	boolean leftCollision, rightCollision, bottomCollision;
	public boolean active = true;
	public boolean deactivating;
	int deactivatcounter = 0;

	public void create(Color c) {
		b[0] = new Block(c);
		b[1] = new Block(c);
		b[2] = new Block(c);
		b[3] = new Block(c);

		tempB[0] = new Block(c);
		tempB[1] = new Block(c);
		tempB[2] = new Block(c);
		tempB[3] = new Block(c);
	}

	public void setXY(int x, int y) {
	}

	public void updateXY(int direction) {
		checkRotationCollision();
		if (leftCollision == false && rightCollision == false && bottomCollision == false) {
			this.directon = direction;
			b[0].x = tempB[0].x;
			b[0].y = tempB[0].y;
			b[1].x = tempB[1].x;
			b[1].y = tempB[1].y;
			b[2].x = tempB[2].x;
			b[2].y = tempB[2].y;
			b[3].x = tempB[3].x;
			b[3].y = tempB[3].y;
		}
	}

	public void getDirection1() {
	}

	public void getDirection2() {
	}

	public void getDirection3() {
	}

	public void getDirection4() {
	}

	public void checkMovementCollision() {
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;

		checkStaticBlockCollision();

		for (int i = 0; i < b.length; i++) {
			if (b[i].x == playManager.left_X) {
				leftCollision = true;
			}
		}
		for (int i = 0; i < b.length; i++) {
			if (b[i].x + Block.size == playManager.right_X) {
				rightCollision = true;
			}
		}
		for (int i = 0; i < b.length; i++) {
			if (b[i].y + Block.size == playManager.bottom_Y) {
				bottomCollision = true;
			}
		}
	}

	public void checkRotationCollision() {
		leftCollision = false;
		rightCollision = false;
		bottomCollision = false;

		checkStaticBlockCollision();

		for (int i = 0; i < b.length; i++) {
			if (tempB[i].x < playManager.left_X) {
				leftCollision = true;
			}
		}
		for (int i = 0; i < b.length; i++) {
			if (tempB[i].x + Block.size > playManager.right_X) {
				rightCollision = true;
			}
		}
		for (int i = 0; i < b.length; i++) {
			if (tempB[i].y + Block.size > playManager.bottom_Y) {
				bottomCollision = true;
			}
		}
	}

	private void checkStaticBlockCollision() {
		for (int i = 0; i < playManager.staticBlocks.size(); i++) {
			int targetX = playManager.staticBlocks.get(i).x;
			int targetY = playManager.staticBlocks.get(i).y;

			for (int ii = 0; ii < b.length; ii++) {
				if (b[ii].y + Block.size == targetY && b[ii].x == targetX) {
					bottomCollision = true;
				}
			}
			for (int ii = 0; ii < b.length; ii++) {
				if (b[ii].x - Block.size == targetX && b[ii].y == targetY) {
					leftCollision = true;
				}
			}
			for (int ii = 0; ii < b.length; ii++) {
				if (b[ii].x + Block.size == targetX && b[ii].y == targetY) {
					rightCollision = true;
				}
			}
		}
	}

	public void update() {
		if (deactivating) {
			deactivating();
		}
		if (keyHandler.upPressed) {
			switch (directon) {
			case 1:
				getDirection2();
				break;
			case 2:
				getDirection3();
				break;
			case 3:
				getDirection4();
				break;
			case 4:
				getDirection1();
				break;
			}
			keyHandler.upPressed = false;
		}
		checkMovementCollision();
		if (keyHandler.downPressed) {
			if (bottomCollision == false) {
				b[0].y += Block.size;
				b[1].y += Block.size;
				b[2].y += Block.size;
				b[3].y += Block.size;
				autoDropCount = 0;
			}
			keyHandler.downPressed = false;
		}
		if (keyHandler.leftPressed) {
			if (leftCollision == false) {
				b[0].x -= Block.size;
				b[1].x -= Block.size;
				b[2].x -= Block.size;
				b[3].x -= Block.size;
			}
			keyHandler.leftPressed = false;
		}
		if (keyHandler.rightPressed) {
			if (rightCollision == false) {
				b[0].x += Block.size;
				b[1].x += Block.size;
				b[2].x += Block.size;
				b[3].x += Block.size;
			}
			keyHandler.rightPressed = false;
		}
		if (bottomCollision) {
			deactivating = true;
		} else {
			autoDropCount++;
			if (autoDropCount == playManager.dropInterval) {
				b[0].y += Block.size;
				b[1].y += Block.size;
				b[2].y += Block.size;
				b[3].y += Block.size;
				autoDropCount = 0;
			}
		}
	}

	private void deactivating() {
		deactivatcounter++;
		if (deactivatcounter == 45) {
			deactivatcounter = 0;
			checkMovementCollision();
			if (bottomCollision) {
				active = false;
			}
		}
	}

	public void draw(Graphics2D g) {
		int margin = 2;
		g.setColor(b[0].c);
		g.fillRect(b[0].x + margin, b[0].y + margin, Block.size - (margin * 2), Block.size - (margin * 2));
		g.fillRect(b[1].x + margin, b[1].y + margin, Block.size - (margin * 2), Block.size - (margin * 2));
		g.fillRect(b[2].x + margin, b[2].y + margin, Block.size - (margin * 2), Block.size - (margin * 2));
		g.fillRect(b[3].x + margin, b[3].y + margin, Block.size - (margin * 2), Block.size - (margin * 2));
	}
}

class MinoL1 extends Mino {
	MinoL1() {
		create(Color.ORANGE);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y - Block.size;
		b[2].x = b[0].x;
		b[2].y = b[0].y + Block.size;
		b[3].x = b[0].x + Block.size;
		b[3].y = b[0].y + Block.size;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y + Block.size;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(1);
	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x + Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x - Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(2);
	}

	public void getDirection3() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y + Block.size;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y - Block.size;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(3);
	}

	public void getDirection4() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x - Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x + Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(4);
	}
}

class MinoL2 extends Mino {
	MinoL2() {
		create(Color.BLUE);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y - Block.size;
		b[2].x = b[0].x;
		b[2].y = b[0].y + Block.size;
		b[3].x = b[0].x - Block.size;
		b[3].y = b[0].y + Block.size;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y + Block.size;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(1);

	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x + Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x - Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(2);
	}

	public void getDirection3() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y + Block.size;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y - Block.size;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(3);
	}

	public void getDirection4() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x - Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x + Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(4);
	}
}

class MinoT extends Mino {
	public MinoT() {
		create(Color.MAGENTA);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y - Block.size;
		b[2].x = b[0].x - Block.size;
		b[2].y = b[0].y;
		b[3].x = b[0].x + Block.size;
		b[3].y = b[0].y;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x - Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y;
		updateXY(1);

	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x + Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y - Block.size;
		tempB[3].x = b[0].x;
		tempB[3].y = b[0].y + Block.size;
		updateXY(2);
	}

	public void getDirection3() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y + Block.size;
		tempB[2].x = b[0].x + Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y;
		updateXY(3);
	}

	public void getDirection4() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x - Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y + Block.size;
		tempB[3].x = b[0].x;
		tempB[3].y = b[0].y - Block.size;
		updateXY(4);
	}
}

class MInoBar extends Mino {
	MInoBar() {
		create(Color.CYAN);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x - Block.size;
		b[1].y = b[0].y;
		b[2].x = b[0].x + Block.size;
		b[2].y = b[0].y;
		b[3].x = b[0].x + Block.size * 2;
		b[3].y = b[0].y;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x - Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x + Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size * 2;
		tempB[3].y = b[0].y;
		updateXY(1);
	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y + Block.size;
		tempB[3].x = b[0].x;
		tempB[3].y = b[0].y + Block.size * 2;
		updateXY(2);
	}

	public void getDirection3() {
		getDirection1();
	}

	public void getDirection4() {
		getDirection2();
	}
}

class MinoSquare extends Mino {
	MinoSquare() {
		create(Color.YELLOW);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y + Block.size;
		b[2].x = b[0].x + Block.size;
		b[2].y = b[0].y;
		b[3].x = b[0].x + Block.size;
		b[3].y = b[0].y + Block.size;
	}

	public void getDirection1() {
	}

	public void getDirection2() {
	}

	public void getDirection3() {
	}

	public void getDirection4() {
	}
}

class MinoZ1 extends Mino {
	MinoZ1() {
		create(Color.RED);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y - Block.size;
		b[2].x = b[0].x - Block.size;
		b[2].y = b[0].y;
		b[3].x = b[0].x - Block.size;
		b[3].y = b[0].y + Block.size;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x - Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(1);
	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x + Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y - Block.size;
		tempB[3].x = b[0].x - Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(2);
	}

	public void getDirection3() {
		getDirection1();
	}

	public void getDirection4() {
		getDirection2();
	}
}

class MinoZ2 extends Mino {
	public MinoZ2() {
		create(Color.GREEN);
	}

	public void setXY(int x, int y) {
		b[0].x = x;
		b[0].y = y;
		b[1].x = b[0].x;
		b[1].y = b[0].y - Block.size;
		b[2].x = b[0].x + Block.size;
		b[2].y = b[0].y;
		b[3].x = b[0].x + Block.size;
		b[3].y = b[0].y + Block.size;
	}

	public void getDirection1() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x;
		tempB[1].y = b[0].y - Block.size;
		tempB[2].x = b[0].x + Block.size;
		tempB[2].y = b[0].y;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y + Block.size;
		updateXY(1);
	}

	public void getDirection2() {
		tempB[0].x = b[0].x;
		tempB[0].y = b[0].y;
		tempB[1].x = b[0].x - Block.size;
		tempB[1].y = b[0].y;
		tempB[2].x = b[0].x;
		tempB[2].y = b[0].y - Block.size;
		tempB[3].x = b[0].x + Block.size;
		tempB[3].y = b[0].y - Block.size;
		updateXY(2);
	}

	public void getDirection3() {
		getDirection1();
	}

	public void getDirection4() {
		getDirection2();
	}
}

class keyHandler implements KeyListener {

	public static boolean upPressed, downPressed, leftPressed, rightPressed, pausePressed;

	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		if (code == KeyEvent.VK_W) {
			upPressed = true;
		}
		if (code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if (code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if (code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if (code == KeyEvent.VK_SPACE) {
			if (pausePressed) {
				pausePressed = false;
			} else {
				pausePressed = true;
			}
		}
	}

	@Override
	public void keyReleased(KeyEvent arg0) {
	}

	@Override
	public void keyTyped(KeyEvent arg0) {
	}
}

public class TetrisMain {
	public static void main(String args[]) {
		JFrame window = new JFrame("Tetris Game: Atish Kumar Sahu");
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.setResizable(false);
		GamePanel gp = new GamePanel();
		window.add(gp);
		window.pack();
		window.setLocationRelativeTo(null);
		window.setIconImage(Toolkit.getDefaultToolkit().getImage("C:\\Users\\Atish kumar sahu\\eclipse-workspace\\Tetris Gama In Java\\src\\tetrisgame_tetri_6955.png"));
		window.setVisible(true);
		gp.launchGame();
	}
}