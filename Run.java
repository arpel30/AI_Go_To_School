package AIProject;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Application;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.image.Image;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.util.Duration;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class Run extends Application {

	private Timeline timeline;
	private double speed = 0.0001 * 100;

	private int popSize = 100;
	private Population pop = new Population(popSize);

	private Pane pane = new Pane();

	private int width = 750;
	private int height = 750;

	private Scene scene = new Scene(pane, width, height);
	private Stage win = new Stage();
	private Stage graphStage = new Stage();

	private Image i = new Image("file:school_imoji.png");
	private ImagePattern ip = new ImagePattern(i);
	private Circle school = new Circle(325, 50, 50);

	private int gen;
	private int alive;
	private int maxSteps = 300;
	private int step;
	private int maxGen = 130;
	
	private Label aliveLabel = new Label();
	private String aliveStr = "Alive";
	private Label genLabel = new Label();
	private String genStr = "Gen";
	private Label stepLabel = new Label();
	private String stepStr = "step";

	private Child bestC;

	private double lestBestFit;

	private Obstecale[] obs;

	// Buttons :

	Boolean isPlay = true;
	Label plLabel = new Label("Click anywhere to Play/Pause");

	Button save = new Button("SAVE BRAIN");
	Button load = new Button("LOAD BRAIN");

	String filename = "brain.txt";

	Brain savedBrain;

	Button restart = new Button("Restart");
	Button graph = new Button("Show/Hide Graph");
	Button fast = new Button("Faster");
	Button slow = new Button("Slower");

	// flag for restart
	boolean res = false;

	// graph:
	LineChart linechart;

	XYChart.Series series;

	/*
	 * Runnable r = () -> { System.out.print("|"); play.setOnAction(e -> {
	 * playBut(); });
	 * 
	 * };
	 * 
	 * Thread butT = new Thread(r);
	 */
	VBox buttons = new VBox();

	public static void main(String[] args) {
		launch(args);

	}

	@Override
	public void start(Stage primaryStage) throws Exception {
		// butT.start();
		step = 0;
		lestBestFit = 0;
		gen = 1;
		alive = popSize;
		obs = new Obstecale[2];
		obs[0] = new Obstecale(0, 450, 1);
		obs[1] = new Obstecale(width - Obstecale.width, 250, 0);
//		obs[2] = new Obstecale(0, 150, 1);

		plLabel.setStyle("-fx-font-weight: bold; -fx-font-size: 20px;");
		save.setStyle("-fx-font-weight: bold");
		load.setStyle("-fx-font-weight: bold");

		HBox hbox = new HBox(slow, fast);
		buttons.getChildren().addAll(save, load, hbox, restart, graph);
		pane.getChildren().addAll(school, aliveLabel, genLabel, stepLabel, buttons, plLabel);

		buttons.setLayoutY(height - 200);

		for (int i = 0; i < obs.length; i++) {
			pane.getChildren().add(obs[i]);
		}

		genLabel.setLayoutY(aliveLabel.getLayoutY() + 20);
		stepLabel.setLayoutY(aliveLabel.getLayoutY() + 40);
		plLabel.setLayoutY(height - 40);
//		plLabel.setLayoutX((width+aliveLabel.getWidth())/2);

		NumberAxis xAxis = new NumberAxis(1, maxGen, 20);
		xAxis.setLabel("Generation");

		// Defining the y axis
		NumberAxis yAxis = new NumberAxis(-1.2, 2.1, 0.05);
		yAxis.setLabel("Score");

		// Creating the line chart
		linechart = new LineChart(xAxis, yAxis);

		series = new XYChart.Series();
		series.setName("Learning Process");
		series.getData().add(new XYChart.Data(0, -1));
		linechart.getData().add(series);

		school.setFill(ip);
		win.setScene(scene);
		win.show();

		for (int i = 0; i < popSize; i++) {
			pop.childs[i] = new Child(maxSteps);
			pop.childs[i].getBrain().randomBrain();
			pane.getChildren().add(pop.childs[i]);

		}

		KeyFrame frame = new KeyFrame(Duration.seconds(speed), e -> {
			speed *= 10;
			fit();
			bestC = best();
			bestC.color();
			updateLabels();
			arrived();

			if (step >= maxSteps) {
				if (res)
					restartBut();
				else
					newGen();
				pane.getChildren().clear();
				pane.getChildren().addAll(school, aliveLabel, genLabel, stepLabel, buttons, plLabel);

				for (int i = 0; i < obs.length; i++) {
					pane.getChildren().add(obs[i]);
				}

				genLabel.setLayoutY(aliveLabel.getLayoutY() + 20);
				for (int i = 0; i < popSize; i++) {
					pane.getChildren().add(pop.childs[i]);
					pop.childs[i].setCenterX(350);
					pop.childs[i].setCenterY(730);
				}
			}

			for (int i = 0; i < popSize; i++) {
				if (pop.childs[i].isAlive() && !pop.childs[i].arrived) {
					pop.childs[i].setCenterX(pop.childs[i].getCenterX() + pop.childs[i].getBrain().getX()[step]);
					pop.childs[i].setCenterY(pop.childs[i].getCenterY() + pop.childs[i].getBrain().getY()[step]);
					obsPassed(pop.childs[i]);
				}
				if (pop.childs[i].arrived) {
					timeline.stop();
					series.getData().add(new XYChart.Data(gen, bestC.fit));
				}
			}

			foul();
			step++;
			for (int i = 0; i < popSize; i++) {
				double x = pop.childs[i].getCenterX();
				double y = pop.childs[i].getCenterY();
				if (pop.childs[i].isAlive() && (x <= 1 || y <= 1 || x >= width - 5 || y >= height - 5)) {
					pop.childs[i].kill();
					alive--;
				}
			}

		});

		timeline = new Timeline(frame);
		timeline.setCycleCount(Timeline.INDEFINITE);

		load.setOnAction(e -> loadBrain());
		save.setOnAction(e -> saveBrain());
		pane.setOnMouseClicked(e -> {
			if (e.getX() >= buttons.getWidth() || e.getY() >= buttons.getHeight())
				playBut();
		});
		restart.setOnAction(e -> restartHelper());
		graph.setOnAction(e -> graphBut());

		Group root = new Group(linechart);

		Scene scene = new Scene(root, height / 2, width / 2);
		linechart.setPrefSize(width / 2, height / 2);

		graphStage.setTitle("Learning Chart");
		win.setTitle("AI Learns To Go To School");
		graphStage.setScene(scene);

//		primaryStage.show();

	}

	private void saveBrain() {
		try {
			FileOutputStream f = new FileOutputStream(new File(filename));
			ObjectOutputStream o = new ObjectOutputStream(f);

			// Write objects to file
			o.writeObject(bestC.getBrain());

			o.close();
			f.close();

		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		}
	}

	private void loadBrain() {
		try {
			FileInputStream fi = new FileInputStream(new File(filename));
			ObjectInputStream oi = new ObjectInputStream(fi);

			// Read objects
			savedBrain = (Brain) oi.readObject();

			oi.close();
			fi.close();
			for (int i = 0; i < pop.childs.length; i++) {
				pop.childs[i].setBrain(savedBrain);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found");
		} catch (IOException e) {
			System.out.println("Error initializing stream");
		} catch (Exception e) {
			System.out.println("Error initializing stream");
		}
	}

	public void updateLabels() {
		aliveLabel.setText(aliveStr + "\t: " + alive + "/" + popSize);
		genLabel.setText(genStr + "\t: " + gen);
		stepLabel.setText(stepStr + "\t: " + step + "/" + maxSteps);
	}

	public void fit() {
		for (int i = 0; i < popSize; i++) {
			if (pop.childs[i].isAlive())
				pop.childs[i].fit = 1 / dist(pop.childs[i], school.getCenterX(), school.getCenterY())
						+ ((pop.childs[i].obsPassed) * 2) - closestObs(pop.childs[i]);

			if (pop.childs[i].arrived)
				pop.childs[i].fit = 10;
		}
	}

	public Child best() {
		int maxIndex = 0;
		for (int i = 0; i < popSize; i++) {
			pop.childs[i].neturalColor();
			if (pop.childs[i].fit > pop.childs[maxIndex].fit)
				maxIndex = i;
		}
		return pop.childs[maxIndex];
	}

	private double dist(Child c, double x, double y) {
		return Math.sqrt(Math.pow(x - c.getCenterX(), 2) + (Math.pow(y - c.getCenterY(), 2)));
	}

	private void obsPassed(Child c) {
		for (int j = 0; j < obs.length; j++) {
			if (c.getCenterY() <= obs[j].getLayoutY()) {
				c.obsPassed = j + 1;
			}
		}
	}

	private double closestObs(Child c) {
		if (c.obsPassed < obs.length) {
			Obstecale ob = obs[c.obsPassed];
			if (ob.code == 0) {
				if (ob.getLayoutX() * 0.75 < c.getCenterX())
					return 1;
				return 0;

			} else if (ob.code == 1) {
				if ((ob.getLayoutX() + ob.getWidth()) * 0.75 > c.getCenterX())
					return 1;
				return 0;
			} else {
				return 0;
			}
		}
		return 2;
	}

	public void newGen() {
		if (lestBestFit != bestC.fit)
			System.out.println("\nGen : " + gen + " Best score : " + bestC.fit + " Step : " + step + " Improvement : "
					+ (bestC.fit - lestBestFit));

		step = 0;
		gen++;
		alive = popSize;
		pop.reproduce(bestC.getBrain(), maxSteps);

		if (Math.abs(bestC.fit - lestBestFit) >= 0.001 || gen % 20 == 0) {
			series.getData().add(new XYChart.Data(gen, bestC.fit));
		}
		lestBestFit = bestC.fit;

	}

	public void arrived() {
		for (int i = 0; i < popSize; i++) {
			if (school.getBoundsInParent().intersects(pop.childs[i].getBoundsInParent())) {
				pop.childs[i].arrived = true;
				System.out.println(i + " arrived");
			}
		}
	}

	public void foul() {
		for (int i = 0; i < popSize; i++) {
			for (int j = 0; j < obs.length; j++) {
				if (pop.childs[i].isAlive()
						&& pop.childs[i].getBoundsInParent().intersects(obs[j].getBoundsInParent())) {
					pop.childs[i].fit -= 2;
					pop.childs[i].kill();
					alive--;
				}
			}
		}
	}

	// button handler functions :

	private void playBut() {
		if (isPlay) {
			timeline.play();
			isPlay = false;
			System.out.println("play");
			speed = 0.0001 * 1000;
		} else {
			timeline.stop();
			isPlay = true;
			System.out.println("stop");
			speed = 10;
		}
	}

	private void graphBut() {
		if (!graphStage.isShowing())
			graphStage.show();
		graph.setVisible(false);
	}

	private void restartHelper() {
		res = true;
		step = maxSteps;
	}
	
	private void restartBut() {
		step = 0;
		gen++;
		alive = popSize;
		pop.reproduce(null, maxSteps);
		res = false;
	}

}
