package sample;

// Java Program to create a canvas with specified
// width and height(as arguments of constructor),
// add it to the stage and also add a circle and
// rectangle on it

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.canvas.*;
import javafx.scene.paint.Color;
import javafx.scene.Group;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class Mapa extends Application {

    private enum Block {
        SPACE, START, END, GROUND
    }

    private Position start, end;
    private int mapWidth, mapHeight;

    private ArrayList<Block> map = new ArrayList<>();

    private GraphicsContext graphics_context;
    private final int BLOCK_HEIGHT = 15, BLOCK_WIDTH = 15;

    private void drawMap() {
        int i = 0;
        for (Block block : map) {
            draw(block, (i % mapWidth) * BLOCK_WIDTH, (i / mapHeight) * BLOCK_HEIGHT);
            i++;
        }

        draw(Block.START, start.x * BLOCK_WIDTH, start.y * BLOCK_HEIGHT);
        draw(Block.END, end.x * BLOCK_WIDTH, end.y * BLOCK_HEIGHT);

    }

    private void draw(Block block, int x, int y) {
        Color color = Color.LIGHTGRAY;

        switch (block) {
            case GROUND:
                color = Color.BLUE;
                break;
            case START:
                color = Color.GREEN;
                break;
            case END:
                color = Color.RED;
                break;
        }
        graphics_context.setFill(color);
        graphics_context.fillRect(x, y, BLOCK_WIDTH, BLOCK_HEIGHT);

    }

    public void loadMap() {
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File("mapa.txt"));
            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                parse(line);
                System.out.println(line);

            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } finally {
            if (scanner != null) {
                scanner.close();
            }
        }
    }

    private void parse(String line) {
        for (int i = 0; i < line.length(); i++) {
            if (line.charAt(i) == '-') {
                map.add(Block.SPACE);
            } else if (line.charAt(i) == 'X') {
                map.add(Block.GROUND);
            } else if (line.charAt(i) == 'S') {
                map.add(Block.SPACE);
                start = new Position(i, map.size() / line.length());
            } else if (line.charAt(i) == 'E') {
                map.add(Block.SPACE);
                end = new Position(i, map.size() / line.length());
                mapWidth = line.length();
            }
        }

    }

    // launch the application
    public void start(Stage stage) {

        // set title for the stage
        stage.setTitle("creating canvas");

        loadMap();
        mapHeight = map.size() / mapWidth;

        System.out.println("Start:" + start);
        System.out.println("End:" + end);

        // create a canvas
        Canvas canvas = new Canvas(300.0f, 300.0f);

        // graphics context
        graphics_context =
                canvas.getGraphicsContext2D();

        drawMap();
        // draw(Block.GROUND, 0, 0);

        // create a Group
        Group group = new Group(canvas);

        // create a scene
        Scene scene = new Scene(group, 400, 400);

        // set the scene
        stage.setScene(scene);

        stage.show();
    }

    // Main Method
    public static void main(String args[]) {

        // launch the application
        launch(args);
    }
}

