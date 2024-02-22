import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.Random;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.Timer;

class MapGenerator{
	public int map[][];
	public int brickwidth;
	public int bricklength;
	MapGenerator(int row, int col){
		map = new int[row][col];
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				map[i][j] = 1;			
			}
		}
		brickwidth = 880/col;
		bricklength = 200/row;
	}
	public void draw(Graphics2D g) {
		for(int i = 0; i < map.length; i++) {
			for(int j = 0; j < map[0].length; j++) {
				if(map[i][j] > 0) {
					g.setColor(Color.WHITE);
					g.fillRect(j * brickwidth + 50, i * bricklength + 50, brickwidth, bricklength);
					g.setStroke(new BasicStroke(4));
					g.setColor(Color.BLACK);
					g.drawRect(j * brickwidth + 50, i * bricklength + 50, brickwidth, bricklength);
				}
			}
		}
	}
	public void setbrickvalue(int value, int row, int col) {
		map[row][col] = value;
	}
}
class Gameplay extends JPanel implements KeyListener, ActionListener{

	private static final long serialVersionUID = 1L;
	private boolean play = false;
	private int score = 0;
	private int totalBricks = 40;
	private Timer time;
	private int delay = 50;
	private int playerX = 310;
	private int ballposX = 200;
	private int ballposY = 700;
	private Random rand = new Random();
	int n = rand.nextInt(2 + 1 - 2) - 2;
	private int ballXdir = n;
	private int ballYdir = -1;
	private MapGenerator map;
	
	Gameplay(){
		map = new MapGenerator(10,10);
		addKeyListener(this);
		setFocusable(true);
		setFocusTraversalKeysEnabled(false);
		time = new Timer(delay, this);
		time.start();
		
		 time = new Timer(10, new ActionListener() {
	            @Override
	            public void actionPerformed(ActionEvent e) {
	                if (play) {
	                	ballposX += ballXdir;
	            		ballposY += ballYdir;
	                    // ... (no changes to actionPerformed method)
	                    repaint();
	                }
	            }
	        });
	        time.start();
	}    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        // Background
        g.setColor(Color.BLACK);
        g.fillRect(0, 0, 999, 999);

        // Drawing map
        map.draw((Graphics2D) g);

        // Create the borders
        g.setColor(Color.YELLOW);
//        g.fillRect(0, 0, 1, 999);
        g.fillRect(0, 0, 999, 3);
//        g.fillRect(980, 0, 1, 990);

        // Paddle
        g.setColor(Color.GREEN);
        g.fillRect(playerX, 900, 100, 8);

        // Ball
        g.setColor(Color.YELLOW);
        g.fillOval(ballposX, ballposY, 20, 20);

        // Score
        g.setColor(Color.RED);
        g.setFont(new Font("serif", Font.BOLD, 25));
        g.drawString("" + score, 850, 40);

        if (ballposY > 920) {
            play = false;
            ballXdir = 0;
            ballYdir = 0;
            g.setColor(Color.RED);
            g.setFont(new Font("serif", Font.BOLD, 25));
            g.drawString("Score Is " + score, 400, 300);
            g.drawString("Press Enter To Restart", 400, 360);
        }
    }

    public void actionPerformed(ActionEvent e) {
        if (play) {
            if (new Rectangle(ballposX, ballposY, 20, 20).intersects(new Rectangle(playerX, 900, 100, 8))) {
                ballYdir = -ballYdir;
            }
            for (int i = 0; i < map.map.length; i++) {
                for (int j = 0; j < map.map[0].length; j++) {
                    if (map.map[i][j] > 0) {
                        int brickX = j * map.brickwidth + 80;
                        int brickY = i * map.bricklength + 50;
                        int brickWidth = map.brickwidth;
                        int brickLength = map.bricklength;

                        Rectangle rect = new Rectangle(brickX, brickY, brickWidth, brickLength);
                        Rectangle ballr = new Rectangle(ballposX, ballposY, 20, 20);
                        Rectangle brr = rect;

                        if (ballr.intersects(brr)) {
                            map.setbrickvalue(0, i, j);
                            totalBricks--;
                            score += 10;
                            if (ballposX + 19 <= brr.x || ballposX + 1 >= brr.x + brr.width) {
                                ballXdir = -ballXdir;
                            } else {
                                ballYdir = -ballYdir;
                            }
                        }
                    }
                }
            }
            ballposX += ballXdir;
            ballposY += ballYdir;
            if (ballposX < 0) {
                ballXdir = -ballXdir;
            }
            if (ballposY < 0) {
                ballYdir = -ballYdir;
            }
            if (ballposX > 948) {
                ballXdir = -ballXdir;
            }
            repaint();
        }
    }

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            if (playerX >= 870) {
                playerX = 850;
            } else {
                moveright();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            if (playerX < 10) {
                playerX = 10;
            } else {
                moveleft();
            }
        }
        if (e.getKeyCode() == KeyEvent.VK_ENTER) {
            if (!play) {
                play = true;
                ballposX = 200;
                ballposY = 700;
                ballXdir = 1; // Start the ball moving to the right
                ballYdir = -2;
                playerX = 310;
                score = 0;
                totalBricks = 40;
                map = new MapGenerator(10, 10);
                repaint();
            }
        }
    }

    public void moveright() {
        play = true;
        playerX += 15;
    }

    public void moveleft() {
        play = true;
        playerX -= 15;
    }

    public void keyReleased(KeyEvent arg0) {
    }

    public void keyTyped(KeyEvent arg0) {
    }
}

public class Main {
    public static void main(String[] args) {
        JFrame obj = new JFrame();
        Gameplay gp = new Gameplay();
        obj.setBounds(10, 10, 1000, 1000);
        obj.setTitle("Brick Breaker- Atish Kumar Sahu");
        obj.setIconImage(Toolkit.getDefaultToolkit().getImage(
                "C:\\Users\\Atish kumar sahu\\eclipse-workspace\\Interview Brick Breaker Game In JAVA\\brick.png"));
        obj.setResizable(false);
        obj.setVisible(true);
        obj.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        obj.add(gp);
	obj.addKeyListener(gp);
	gp.requestFocusInWindow();
    }
}

