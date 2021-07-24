package sample;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.TextArea;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Rotate;

public class Controller {

    @FXML
    private TextArea console;
    private Interact interact;
    @FXML
    private Canvas robotLayer;
    @FXML
    private Canvas mapLayer;

    GraphicsContext gc, gc2;

    public Position position;

    private Mapa mapa;

    private final int  RobotWidth = 15;

    public void initialize() throws Exception {

        mapa = new Mapa(mapLayer);

        interact = new Interact(this, mapa);
        interact.go();

        gc = robotLayer.getGraphicsContext2D();
        drawShapes(gc);

        robotLayer.toFront();

        /*
        gc2 = mapLayer.getGraphicsContext2D();
        gc2.setFill(Color.BLUE);
        gc2.fillOval(100,100,20,20);
        */
    }



    public void button(ActionEvent event)  {
        /*
        robotLayer.setTranslateX(-70);
        robotLayer.setTranslateY(10);
        robotLayer.setRotate(180);
        */

        System.exit(0);

    }

    private void drawShapes(GraphicsContext gc) {
        gc.setFill(Color.GREEN);
        gc.setStroke(Color.BLUE);
        gc.setLineWidth(5);

        // int incX = this.position.x*RobotWidth;
        // gc.fillRoundRect(mapa.getStart().x *RobotWidth +incX, mapa.getStart().y * RobotWidth, RobotWidth, RobotWidth, 10, 10);
        gc.fillRoundRect(position.x * RobotWidth , position.y * RobotWidth, RobotWidth, RobotWidth, 10, 10);
        gc.setFill(Color.BLACK);

        int [][] incs = {{ 3,8,13,8,3,8} /* NORTH */,
                {5,10,5,0,5,10} /* EAST */,
                {3,8,13,3,7,3} /* SOUTH */,
                {10,5,10,0,5,10} /* WEST */
        } ;

        gc.fillPolygon(new double[]{position.x*RobotWidth+incs[position.orientation][0], position.x*RobotWidth+incs[position.orientation][1], position.x*RobotWidth+incs[position.orientation][2]},
                        new double[]{position.y*RobotWidth+incs[position.orientation][3], position.y*RobotWidth+incs[position.orientation][4], position.y*RobotWidth+incs[position.orientation][5]}, 3);


    }

    public void imprimir(String message) {

        Platform.runLater(() -> {
            console.appendText(message + "\n");

            gc.clearRect(0,0 , robotLayer.getWidth(), robotLayer.getHeight());
            drawShapes(gc);
        });
    }

}
