package AIProject;

import java.io.Serializable;
import java.util.Random;

public class Brain implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 799668788680692480L;
	
	private double[] x;
	private double[] y;
	private int size;

	private Random rand = new Random();
	private double high = 20;
	private int low = 10;

	private double changeProb = 0.15;

	public Brain(int size) {
		this.size = size + 1;
		x = new double[this.size];
		y = new double[this.size];
	}

	public void randomBrain() {
		for (int i = 0; i < size; i++) {
			x[i] = rand.nextDouble() * high - low;
			y[i] = rand.nextDouble() * high - low;
		}
	}
	
	public void mutate() {
		for (int i = 0; i < size; i++) {
			if (rand.nextDouble() < changeProb) {
				x[i] = rand.nextDouble() * high - low;
				y[i] = rand.nextDouble() * high - low;
			}
		}
	}

	public Brain copy() {
		Brain tmp = new Brain(this.size);
		tmp.setX(this.x);
		tmp.setY(this.y);
		return tmp;
	}

	public double[] getX() {
		return x;
	}

	public double[] getY() {
		return y;
	}

	public void setX(double[] x) {
		for (int i = 0; i < size-1; i++) {
			this.x[i] = x[i];
		}
	}

	public void setY(double[] y) {
		for (int i = 0; i < size-1; i++) {
			this.y[i] = y[i];
		}
	}

	public int getSize() {
		return size;
	}

	

}
