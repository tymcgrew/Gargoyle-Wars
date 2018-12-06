package C;

import java.awt.Color;

import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import javax.imageio.ImageIO;
import javax.sound.sampled.AudioFormat;
import javax.sound.sampled.AudioInputStream;
import javax.sound.sampled.AudioSystem;
import javax.sound.sampled.Clip;
import javax.sound.sampled.LineEvent;
import javax.sound.sampled.LineEvent.Type;
import javax.sound.sampled.LineListener;
import javax.sound.sampled.LineUnavailableException;
import javax.sound.sampled.UnsupportedAudioFileException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

public class Tester extends JPanel {

	JFrame window = new JFrame("My First Game...");
	Random rnd = new Random();
	Timer tmr = null;
	Ship ship = null;
	ArrayList<Background> bgs = new ArrayList<>();
	ArrayList<Bullet> bullets = new ArrayList<>();
	ArrayList<Enemy> enemies = new ArrayList<>();
	private final int IMAGE_WIDTH = 2000;
	private final int IMAGE_HEIGHT = 900;
	public int count = 0;

	public Tester() {
		window.setBounds(100, 100, 1920, 1080);
		window.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		window.getContentPane().add(this);
		window.setAlwaysOnTop(true);
		bgs.add(new Background(0, 0, IMAGE_WIDTH, IMAGE_HEIGHT, 2, "bg0.png"));
		bgs.add(new Background(0, 200, IMAGE_WIDTH, IMAGE_HEIGHT, 4, "bg1.png"));
		bgs.add(new Background(0, 450, IMAGE_WIDTH, IMAGE_HEIGHT, 8, "bg2.png"));
		ship = new Ship(500, 500, 100, 56);
		window.setLocation(0,0);
		window.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		window.setUndecorated(true);
		window.setVisible(true);
		Ship.screenBounds = this.getBounds();

		for (int i = 0; i < 30; i ++) {
			enemies.add(new Enemy(
					rnd.nextInt(850)+1000, 
					rnd.nextInt(window.getContentPane().getHeight()), 
					60, 
					76)
					);
			enemies.get(i).xSpeed = 0;
			enemies.get(i).ySpeed = 0;
		}

		//================================================================== Events
		tmr = new Timer(10, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				for (Background bg : bgs) {
					bg.move(-1);
					if (bg.x > getWidth()) bg.x = -bg.width;
					if (bg.x + bg.width * 2 < getWidth()) bg.x = getWidth();
				}

				count++;
				if (count == enemies.size()) count = 0;

				for (int i = bullets.size()-1; i >= 0; i--) {
					if (bullets.get(i).move()) {
						bullets.remove(i);
						continue;
					}
					for (Enemy en: enemies) {
						if (bullets.get(i).intersects(en) && bullets.get(i).color == Color.YELLOW) {
							bullets.remove(i);
							enemies.remove(en);
							if(enemies.size() == 1) {
								try {
									ship.image = ImageIO.read(new File("gargoyle.png"));
								}
								catch (Exception f) {}
							}
							else if (rnd.nextInt(enemies.size())==0)	{
								repaint();
								tmr.stop();
								JOptionPane.showMessageDialog(window, "You win!");
								try {
									playClip(new File("sound.wav"));
								}
								catch (Exception f) {}
								System.exit(0);
							}
							break;
						}

						else if (bullets.get(i).intersects(ship)) {
							bullets.remove(i);
							tmr.stop();
							try {
								playClip(new File("Airsoft-01.wav"));
							}
							catch (Exception f) {}
							JOptionPane.showMessageDialog(window, "Game over...");
							System.exit(0);
						}
					}

				}

				for (int i = 0; i < enemies.size(); i++) {
					if (count % 12 == i % 12) {
						enemies.get(i).xSpeed = rnd.nextInt((61-enemies.size()*2)) - ((60-enemies.size()*2))/2;
						enemies.get(i).ySpeed = rnd.nextInt((61-enemies.size()*2)) - ((60-enemies.size()*2))/2;
					}
					if (rnd.nextDouble() < .025 && enemies.size() < 30 || rnd.nextDouble() < .1 && enemies.size() < 15)
						bullets.add(
								new Bullet (
										enemies.get(i).x,
										enemies.get(i).y - 38,
										24, 12, Color.RED, -20
										));
				}

				for (Enemy en : enemies)
					en.move();
				ship.move();

				repaint();

			}
		});

		window.addKeyListener(new KeyListener() {


			@Override
			public void keyReleased(KeyEvent e) {
				System.out.println(e.getKeyCode());
				switch (e.getKeyCode()) {
				case 65: case 68: ship.xSpeed = 0; break;
				case 87: case 83: ship.ySpeed = 0; break;
				}

			}

			@Override
			public void keyPressed(KeyEvent e) {
				switch (e.getKeyCode()) {
				case 65: ship.xSpeed = -12; break;
				case 87: ship.ySpeed = -12; break;
				case 68: ship.xSpeed = 12; break;
				case 83: ship.ySpeed = 12; break;
				case 32: {
					bullets.add(
							new Bullet (
									ship.x + ship.width,
									ship.y + ship.height/2-3,
									24, 12, Color.YELLOW, 20
									));
				}
				}
			}

			@Override
			public void keyTyped(KeyEvent e) {
				// TODO Auto-generated method stub

			}

		});
		//================================================================== End Events

		tmr.start();
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);


		bgs.get(0).draw(g);
		bgs.get(1).draw(g);
		bgs.get(2).draw(g);

		try {
			ship.draw(g);
		}
		catch (Exception e) {e.printStackTrace();}

		for (Bullet b: bullets)
			b.draw(g);

		if (enemies.size() != 0)
			for (Enemy en : enemies)
				en.draw(g);



	}

	public static void main(String[] args) {
		new Tester();
	}

	// From stackoverflow.com
	private static void playClip(File clipFile) throws IOException, 
	UnsupportedAudioFileException, LineUnavailableException, InterruptedException {
		class AudioListener implements LineListener {
			private boolean done = false;
			@Override public synchronized void update(LineEvent event) {
				Type eventType = event.getType();
				if (eventType == Type.STOP || eventType == Type.CLOSE) {
					done = true;
					notifyAll();
				}
			}
			public synchronized void waitUntilDone() throws InterruptedException {
				while (!done) { wait(); }
			}
		}
		AudioListener listener = new AudioListener();
		AudioInputStream audioInputStream = AudioSystem.getAudioInputStream(clipFile);
		try {
			Clip clip = AudioSystem.getClip();
			clip.addLineListener(listener);
			clip.open(audioInputStream);
			try {
				clip.start();
				listener.waitUntilDone();
			} finally {
				clip.close();
			}
		} finally {
			audioInputStream.close();
		}
	}

}