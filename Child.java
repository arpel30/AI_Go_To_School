package AIProject;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;

public class Child extends Circle {

	double fit;
	private static double x = 350;
	private static double y = 730;
	private static double size = 10;
	private Brain brain;
	private boolean alive;
	private static Image i = new Image("file:child.jpg");
	private static ImagePattern ip = new ImagePattern(i);
	
	public int obsPassed; 
	
	public boolean arrived;
	
	public Child(int brainSize) {
		super(x, y, size, ip);
		alive = true;
		fit = 0;
		brain = new Brain(brainSize);
		arrived = false;
		obsPassed = 0;
	}

	public Child(Brain readyBrain) {
		super(x, y, size, ip);
		alive = true;
		fit = 0;
		brain = readyBrain.copy();
	}

	public void kill() {
		alive = false;
		this.setFill(Color.BLACK);
		this.setRadius(size / 2);
	}

	public boolean isAlive() {
		return alive;
	}

	public void color() {
		this.setFill(Color.DARKGREEN);
	}

	public void neturalColor() {
		this.setFill(ip);
	}

	public Brain getBrain() {
		return brain;
	}

	public void setBrain(Brain brain) {
		this.brain = brain;
	}
}
