package C;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;

public class Bullet extends Rectangle {

	public Color color;
	public int xSpeed;

	public Bullet(int x, int y, int width, int height, Color color, int xSpeed) {
		super(x, y, width, height);
		this.color = color;
		this.xSpeed = xSpeed;
	}

	public boolean move() {
		x += xSpeed;
		return (x + width > Ship.screenBounds.getWidth() || x < 0);
	}

	public void draw(Graphics g) {
		g.setColor(color);
		g.fillRect(x, y, width, height);
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
	}
}
