package C;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Random;

import javax.imageio.ImageIO;

public class Enemy extends Rectangle {

public static Rectangle screenBounds;
	
	public int xSpeed, ySpeed;
	private BufferedImage image = null;
	private Random rnd = new Random();
	
	public Enemy(int x, int y, int width, int height) {
		super(x, y, width, height);
		try { image = ImageIO.read(new File("gargoyle.png")); }
		catch (Exception e) {}
	}
	
	public void move() {
		x += xSpeed;
		y += ySpeed;
		if (x < 1000)
			x = 1000;
		else if (x + width > Ship.screenBounds.width)
			x = Ship.screenBounds.width - width;
		if (y < 0)
			y = 0;
		else if (y + height > Ship.screenBounds.height)
			y = Ship.screenBounds.height - height;
		
		//now check for bounds and adjust so ship is never outside the viewing window
	}
	
	public void draw(Graphics g) {
		g.drawImage(image, x, y, null); 
		
	}

}
