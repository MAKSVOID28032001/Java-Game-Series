import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Random;
import javax.swing.JFrame;
import javax.swing.JPanel;

class GamePanel extends JPanel implements Runnable{
	static final int Game_Width = 1100;
	static final int Game_Height = (int)(Game_Width * (0.7));
	static final Dimension Screen_Size = new Dimension(Game_Width, Game_Height);
	static final int Ball_Diameter = 20;
	static final int paddle_width = 25;
	static final int paddle_height = 100;
	
	Thread gameT;
	Image image;
	Graphics graphics;
	Random random;
	Paddle p1;
	Paddle p2;
	Ball ball;
	Score score;
	
	GamePanel(){
		newPaddles();
		newBall();
		score = new Score(Game_Width, Game_Height);
		this.setFocusable(true);
		this.addKeyListener(new AL());
		this.setPreferredSize(Screen_Size);
		gameT = new Thread(this);
		gameT.start();
	}
	public void newBall() {
		random = new Random();
		ball = new Ball((Game_Width / 2) - (Ball_Diameter / 2), random.nextInt(Game_Height-Ball_Diameter) , Ball_Diameter,Ball_Diameter);
		
	}
	public void newPaddles() {
		p1 = new Paddle(0,(Game_Height / 2) - (paddle_height / 2), paddle_width, paddle_height, 1);
		p2 = new Paddle(Game_Width - paddle_width,(Game_Height / 2) - (paddle_height / 2), paddle_width, paddle_height, 2);
	}
	public void paint(Graphics g) {
		image = createImage(getWidth(), getHeight());
		graphics = image.getGraphics();
		draw(graphics);
		g.drawImage(image, 0, 0, this);
	}
	public void draw(Graphics g) {
		p1.draw(g);
		p2.draw(g);
		ball.draw(g);
		score.draw(g);
	}
	public void move() {
		p1.move();
		p2.move();
		ball.move();
	}
	public void checkCollision() {
		//bounce ball off the top & bottom window edges
		if(ball.y <= 0) {
			ball.setYDirection(-ball.Yvelocity);
		}
		if(ball.y >= Game_Height - Ball_Diameter) {
			ball.setYDirection(-ball.Yvelocity);
		}
		
		//ball bounces off paddle
		if(ball.intersects(p1)) {
			ball.Xvelocity = Math.abs(ball.Xvelocity);
			//ball.Xvelocity++; //optional
			
			if(ball.Yvelocity > 0) {
				//ball.Yvelocity++; //optional
			}else {
				ball.Yvelocity--;
			}
			ball.setXDirection(ball.Xvelocity);
			ball.setYDirection(ball.Yvelocity);
		}
		if(ball.intersects(p2)) {
			ball.Xvelocity = Math.abs(ball.Xvelocity);
			//ball.Xvelocity++; //optional
			
			if(ball.Yvelocity > 0) {
				//ball.Yvelocity++; //optional
			}else {
				ball.Yvelocity--;
			}
			ball.setXDirection(-ball.Xvelocity);
			ball.setYDirection(ball.Yvelocity);
		}
		
		//stop the paddles at window edges
		if(p1.y <= 0) {
			p1.y = 0;
		}
		if(p1.y >= (Game_Height - paddle_height)) {
			p1.y = Game_Height - paddle_height;
		}
		if(p2.y <= 0) {
			p2.y = 0;
		}
		if(p2.y >= (Game_Height - paddle_height)) {
			p2.y = Game_Height - paddle_height;
		}
		
		//give player 1 point and create new paddle and ball
		if(ball.x <= 0) {
			score.player2++;
			newPaddles();
			newBall();
		}
		if(ball.x >= Game_Width - Ball_Diameter) {
			score.player1++;
			newPaddles();
			newBall();
		}
		
	}
	public void run() {
		long lastTime = System.nanoTime();
		double amountOfTicks = 60.0;
		double ns = 1000000000 / amountOfTicks;
		double delta = 0;
		while(true) {
			long now = System.nanoTime();
			delta += (now - lastTime) / ns;
			lastTime = now;
			if(delta >= 1) {
				move();
				checkCollision();
				repaint();
				delta--;
			}
		}
	}
	public class AL extends KeyAdapter{
		public void keyPressed(KeyEvent e) {
			p1.keyPressed(e);
			p2.keyPressed(e);
		}
		public void keyReleased(KeyEvent e) {
			p1.keyReleased(e);
			p2.keyReleased(e);
		}		
	}
}
class Paddle extends Rectangle{
	int id;
	int yvelocity;
	int speed = 10;
	Paddle(int x, int y, int Paddle_Width, int Paddle_Height, int id){
		super(x, y, Paddle_Width, Paddle_Height);
		this.id = id;
	}
	public void keyPressed(KeyEvent e) {
		switch(id) {
		case 1:
			if(e.getKeyCode() == KeyEvent.VK_W) {
				setYDirection(-speed);
				move();
			}if(e.getKeyCode() == KeyEvent.VK_S) {
				setYDirection(speed);
				move();
			}
			break;
		case 2:
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				setYDirection(-speed);
				move();
			}if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				setYDirection(speed);
				move();
			}
			break;
		}
	}
	public void keyReleased(KeyEvent e) {
		switch(id) {
		case 1:
			if(e.getKeyCode() == KeyEvent.VK_W) {
				setYDirection(0);
				move();
			}if(e.getKeyCode() == KeyEvent.VK_S) {
				setYDirection(0);
				move();
			}
			break;
		case 2:
			if(e.getKeyCode() == KeyEvent.VK_UP) {
				setYDirection(0);
				move();
			}if(e.getKeyCode() == KeyEvent.VK_DOWN) {
				setYDirection(0);
				move();
			}
			break;
		}
	}
	public void setYDirection(int YDirection) {
		yvelocity = YDirection;
	}
	public void move() {
		y = y + yvelocity;
	}
	public void draw(Graphics g) {
		if(id == 1) {
			g.setColor(Color.BLUE);
		}else {
			g.setColor(Color.RED);
		}
		g.fillRect(x,  y,  width, height);
	}
}
class Ball extends Rectangle{
	Random random;
	int Xvelocity;
	int Yvelocity;
	int initialSpeed = 4;
	Ball(int x, int y, int width, int height){
		super(x, y, width, height);
		random = new Random();
		int randomXD = random.nextInt(2);
		if(randomXD == 0) {
			randomXD--;
		}
		setXDirection(randomXD * initialSpeed);
		
		int randomYD = random.nextInt(2);
		if(randomYD == 0)
			randomYD--;
		setYDirection(randomYD * initialSpeed);
	}
	public void setXDirection(int randomXDirection) {
		Xvelocity = randomXDirection;
	}
	public void setYDirection(int randomYDirection) {
		Yvelocity = randomYDirection;
	}
	public void move() {
		x += Xvelocity;
		y += Yvelocity;
	}
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.fillOval(x, y, height, width);
	}
}
class Score extends Rectangle{
	static int Game_Width;
	static int Game_Height;
	int player1;
	int player2;
	Score(int GW, int GH){
		Score.Game_Width = GW;
		Score.Game_Height = GH;
	}
	public void draw(Graphics g) {
		g.setColor(Color.WHITE);
		g.setFont(new Font("Consoles", Font.PLAIN, 50));
		g.drawLine(Game_Width/2, 0, Game_Width/2, Game_Height);
		g.drawString(String.valueOf(player1 / 10) + String.valueOf(player1 % 10), Game_Width/2 - 85, 50);
		g.drawString(String.valueOf(player2 / 10) + String.valueOf(player2 % 10), Game_Width/2 + 20, 50);
	}
}
class GameFrame extends JFrame{
	GamePanel panel = new GamePanel();
	GameFrame(){
		panel = new GamePanel();
		this.add(panel);
		this.setTitle("Ping Pong Game - Atish Kumar Sahu");
		this.setResizable(false);
		this.setBackground(Color.BLACK);
		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.pack();
		this.setVisible(true);
		this.setLocationRelativeTo(null);
		this.setIconImage(Toolkit.getDefaultToolkit().getImage(
				"C:\\Users\\Atish kumar sahu\\eclipse-workspace\\Resume_Pong_Game_In_Java\\src\\Ponggame.png"));
	}
}
public class PongGame {
	public static void main(String[] args) {
		GameFrame frame = new GameFrame();
	}
}
