package ehu.isad.controllers;

import ehu.isad.Mapa;
import ehu.isad.Orientation;
import ehu.isad.Position;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import ehu.isad.Interact;

import java.io.File;

public class Controller {

    public String solver;

    @FXML
    private TextArea console;
    private Interact interact;
    @FXML
    private Canvas robotLayer;
    @FXML
    private Canvas mapLayer;
    @FXML
    private Label lblMapa;
    @FXML
    private Label lblSolver;
    @FXML
    private Button btnStart;
    @FXML
    private Button btnStop;

    GraphicsContext gc;

    public Position position = new Position(0, 0);

    private Mapa mapa;
    private File ficheroMapa;

    private final int RobotWidth = 15;

    public void initialize() {
        gc = robotLayer.getGraphicsContext2D();
        // drawShapes(gc);
        robotLayer.toFront();

        btnStart.setDisable(true);
        btnStop.setDisable(true);

        gc.setFill(Color.RED);
        gc.fillText("\tHow to Start", 0, 30);
        gc.fillText("\t1. Choose a map", 0, 60);
        gc.fillText("\t2. Choose a solver (your executable solution)", 0, 90);
        gc.fillText("\t3. Press Start", 0, 120);

        lblMapa.setText("Not loaded");
        lblSolver.setText("Not loaded");

    }

    public void loadMapFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose a map");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Map files", "*.txt"));
        ficheroMapa = fileChooser.showOpenDialog(null);
        if (ficheroMapa == null || !ficheroMapa.isFile()) {
            return;
        }

        lblMapa.setText(ficheroMapa.getName());
        if (solver!=null) {
            btnStart.setDisable(false);
            btnStop.setDisable(true);
        }

        // remove previous map
        mapLayer.getGraphicsContext2D().clearRect(0, 0, mapLayer.getWidth(), mapLayer.getHeight());
        // remove previous robot
        robotLayer.getGraphicsContext2D().clearRect(0, 0, robotLayer.getWidth(), robotLayer.getHeight());

        // load new map
        mapa = new Mapa(mapLayer, ficheroMapa);
        if (interact == null) {
            interact = new Interact(this, mapa);
        }
        interact.setMap(mapa);
        // initialize robot's initial position
        position.x = mapa.getStart().x;
        position.y = mapa.getStart().y;
        position.orientation = Orientation.NORTH;
        // draw new map;
        drawShapes(gc);
    }

    public void loadSolver(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choose your executable solution");
        System.out.println(System.getProperty("os.name"));

        if (System.getProperty("os.name").contains("Windows")) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Executable files", "*.exe"));
        }
        File ficheroSolver = fileChooser.showOpenDialog(null);
        if (ficheroSolver == null || !ficheroSolver.isFile()) {
            return;
        }

        if (mapa!=null) {
            btnStart.setDisable(false);
            btnStop.setDisable(true);
        }

        solver = ficheroSolver.getAbsolutePath();
        lblSolver.setText(ficheroSolver.getName());

    }

    public void stop(ActionEvent event) {

        if (interact != null) {
            try {
                interact.stop();
            } catch (InterruptedException e) {
                System.out.println("Clock stopped");
            }
        }

        btnStart.setDisable(false);
        btnStop.setDisable(true);
    }

    public void start(ActionEvent event) {

        if (mapa == null) {
            // this will also instantiate the interact object the first time
            loadMapFile(null);
        }

        interact.go();

        btnStart.setDisable(true);
        btnStop.setDisable(false);

    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);

        gc.fillRoundRect(position.x * RobotWidth, position.y * RobotWidth, RobotWidth, RobotWidth, 10, 10);
        gc.setFill(Color.BLACK);

        int[][] incs = {{3, 8, 13, 8, 3, 8} /* NORTH */,
                {5, 10, 5, 0, 5, 10} /* EAST */,
                {3, 8, 13, 3, 7, 3} /* SOUTH */,
                {10, 5, 10, 0, 5, 10} /* WEST */
        };

        gc.fillPolygon(new double[]{position.x * RobotWidth + incs[position.orientation][0], position.x * RobotWidth + incs[position.orientation][1], position.x * RobotWidth + incs[position.orientation][2]},
                new double[]{position.y * RobotWidth + incs[position.orientation][3], position.y * RobotWidth + incs[position.orientation][4], position.y * RobotWidth + incs[position.orientation][5]}, 3);

    }

    public void imprimir(String message) {

        Platform.runLater(() -> {
            console.appendText(message + "\n");

            gc.clearRect(0, 0, robotLayer.getWidth(), robotLayer.getHeight());
            drawShapes(gc);
        });
    }

}
