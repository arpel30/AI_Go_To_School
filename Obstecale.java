package AIProject;

import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Rectangle;

public class Obstecale extends Rectangle {
	
	public static int width = 400;
	private static int height = 10;
	
	public int code; // 0 - left side open, 1 - right open, 2 - both 
	
	private static Image i = new Image("file:covid19_obstecale.png");
	private static ImagePattern ip = new ImagePattern(i);

	public Obstecale(double topX, double topY, int code) {
		super(width, height, ip);
		this.setLayoutX(topX);
		this.setLayoutY(topY);
	}

}
