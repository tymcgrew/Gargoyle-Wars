package C;

import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;

import javax.imageio.ImageIO;

public class Ship extends Rectangle {

	public static Rectangle screenBounds;
	
	public int xSpeed, ySpeed;
	public BufferedImage image = null;
	
	public Ship(int x, int y, int width, int height) {
		super(x, y, width, height);
		try { image = ImageIO.read(new File("ship.png")); }
		catch (Exception e) {}
	}
	
	public void move() {
		x += xSpeed;
		y += ySpeed;
		
		if(!screenBounds.contains(this)) {
			if (x < 0)
				x = 0;
			else if (x + width > screenBounds.width)
				x = screenBounds.width - width;
			if (y < 0)
				y = 0;
			else if (y + height > screenBounds.height)
				y = screenBounds.height - height;
		}
		//now check for bounds and adjust so ship is never outside the viewing window
		
		
	}
	
	public void draw(Graphics g) {
		g.drawImage(image, x, y, null); 
		
	}
		
}
