package sample;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;


class Position {
    static String[] array = {"NORTH", "EAST", "SOUTH", "WEST"};
    static List<String> orientationList = new ArrayList<String>(Arrays.asList(array));

    int orientation = 0;
    int x, y;
    // int orientation;

    public String toString() {
        return "x:" + x + " y:" + y;
    }

    Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.orientation = 0;
    }

}

public class Interact {

    private Controller controller;
    private Mapa mapa;
    private Thread reloj, streamGobbler;
    private Boolean playing = false;

    Interact(Controller controller, Mapa mapa) {
        this.controller = controller;
        this.mapa = mapa;
    }


    public void stop() throws InterruptedException {
        playing = false;

//        reloj.join();
//        streamGobbler.join();
    }

    public void go() {
        playing = true;
        reloj = new Thread(() -> {
            Process p = null;
            try {
                p = Runtime.getRuntime().exec("/tmp/robot");
            } catch (IOException e) {
                e.printStackTrace();
            }
            // BufferedReader inp = new BufferedReader(new InputStreamReader(p.getInputStream()));
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

            //communication
            setUpStreamGobbler(p.getInputStream(), System.out);

            // FIXME (duplicated code)
            controller.position.x = mapa.getStart().x;
            controller.position.y = mapa.getStart().y;
            controller.position.orientation = Orientation.NORTH;
            controller.imprimir("Starting...");
            // Scanner sc = new Scanner(System.in);
            // while (true) {
            while (playing && (controller.position.x != mapa.getEnd().x || controller.position.y != mapa.getEnd().y)) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String c; // = sc.nextLine();

                // Primer paso, girar
                if (mapa.obstacleAt(controller.position)) {
                    c = "0";
                } else {
                    c = "1";
                }

//                if (!c.contains("0") && !c.contains("1")) {
//                    System.exit(0);
//                }
                try {
                    out.write(c + "\n");
                    out.flush();
                } catch (IOException e) {
                    // e.printStackTrace();
                    System.out.println("Stream closed");
                }
            }
            playing = false;
            System.err.println("Stopped at:" + controller.position);

        });
        reloj.start();
    }


    public static void main(String[] args) throws Exception {
        // new Interact().go();
    }

    public void setUpStreamGobbler(final InputStream is, final PrintStream ps) {
        final InputStreamReader streamReader = new InputStreamReader(is);
        streamGobbler = new Thread(new Runnable() {
            public void run() {
                BufferedReader br = new BufferedReader(streamReader);
                String line = null;
                try {
                    while (playing && (line = br.readLine()) != null) {
                        if (line.contains("girar")) {
                            System.err.println("Girar");
                            controller.imprimir("Girar");
                            controller.position.orientation = (controller.position.orientation + 1) % 4;
                            System.err.println(Position.orientationList.get(controller.position.orientation));
                        } else if (line.contains("Sigue")) {
                            switch (controller.position.orientation) {
                                // FIXME Orientation
                                case 0: // NORTH
                                    controller.position.y--;
                                    break;
                                case 1: // EAST
                                    controller.position.x++;
                                    break;
                                case 2: // SOUTH
                                    controller.position.y++;
                                    break;
                                case 3: // WEST
                                    controller.position.x--;
                                    break;
                            }

                            System.err.println("Sigue");
                            controller.imprimir("Sigue");
                            System.err.println("Position:" + controller.position.x + "," + controller.position.y);
                            System.err.println(Position.orientationList.get(controller.position.orientation));
                        } else if (line.contains("HA LLEGADO")) {
                            controller.imprimir("YOU GOT IT!");
                            break;
                        }
                        ps.println("process stream: " + line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                } finally {
                    try {
                        br.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
        streamGobbler.start();
    }
}
