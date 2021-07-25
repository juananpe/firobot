package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;

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

    GraphicsContext gc;

    public Position position = new Position(0, 0);

    private Mapa mapa;
    private File ficheroMapa;

    private final int RobotWidth = 15;

    public void initialize() throws Exception {
        gc = robotLayer.getGraphicsContext2D();
        // drawShapes(gc);
        robotLayer.toFront();
    }

    public void loadMapFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Selecciona un mapa");
        fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Ficheros mapa",  "*.txt"));
        ficheroMapa = fileChooser.showOpenDialog(null);
        if (ficheroMapa == null || !ficheroMapa.isFile()) {
            return;
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
        fileChooser.setTitle("Selecciona el ejecutable a probar");
        System.out.println(System.getProperty("os.name"));

        if (System.getProperty("os.name").contains("Windows")) {
            fileChooser.getExtensionFilters().addAll(new FileChooser.ExtensionFilter("Ficheros ejecutables",  "*.exe"));
        }
        File ficheroSolver = fileChooser.showOpenDialog(null);
        if (ficheroSolver == null || !ficheroSolver.isFile()) {
            return;
        }
        solver = ficheroSolver.getAbsolutePath();

    }

    public void stop(ActionEvent event) {
        if (interact != null) {
            try {
                interact.stop();
            } catch (InterruptedException e) {
                System.out.println("Reloj parado");
            }
        }
    }

    public void start(ActionEvent event) {

        if (mapa == null) {
            // this will also instantiate the interact object the first time
            loadMapFile(null);
        }

        interact.go();

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
