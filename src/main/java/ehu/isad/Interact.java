package ehu.isad;

import ehu.isad.controllers.Controller;

import java.io.*;

import java.util.concurrent.TimeUnit;


public class Interact {

    private Controller controller;
    private Mapa mapa;
    private Thread reloj, streamGobbler;
    private Boolean playing = false;


    public Interact(Controller controller, Mapa mapa) {
        this.controller = controller;
        this.mapa = mapa;
    }


    public void setMap(Mapa mapa){
        this.mapa = mapa;
    }

    public void stop() throws InterruptedException {
        playing = false;

    }

    public void go() {
        playing = true;
        reloj = new Thread(() -> {
            Process p = null;
            try {
                if (controller.solver == null){
                    controller.imprimir("Starting...");
                    return;
                }

                p = Runtime.getRuntime().exec(controller.solver);
            } catch (IOException e) {
                e.printStackTrace();
            }
            BufferedWriter out = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));

            //communication
            setUpStreamGobbler(p.getInputStream(), System.out);

            // FIXME (duplicated code)
            controller.position.x = mapa.getStart().x;
            controller.position.y = mapa.getStart().y;
            controller.position.orientation = Orientation.NORTH;
            controller.imprimir("Starting...");

            // set end point
            try {
                out.write(mapa.getEnd().x + " " + mapa.getEnd().y + "\n");
                out.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }

            while (playing && (controller.position.x != mapa.getEnd().x || controller.position.y != mapa.getEnd().y)) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String c;

                // Primer paso, girar
                if (mapa.obstacleAt(controller.position)) {
                    c = "1";
                } else {
                    c = "0";
                }

                try {
                    out.write(c + "\n");
                    out.flush();
                } catch (IOException e) {
                    System.out.println("Stream closed");
                }
            }
            playing = false;
            controller.imprimir("YOU GOT IT!");
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
                    br.readLine(); // Read the end point, but it is useless for us, bc it is calculated autom.

                    while (playing && (line = br.readLine()) != null) {
                        if (line.contains("Turn")) {
                            System.err.println("Turn 90 degrees");
                            controller.imprimir("Turn");
                            controller.position.orientation = (controller.position.orientation + 1) % 4;
                            System.err.println(Position.orientationList.get(controller.position.orientation));
                        } else if (line.contains("Go")) {
                            switch (controller.position.orientation) {
                                case Orientation.NORTH:
                                    controller.position.y--;
                                    break;
                                case Orientation.EAST:
                                    controller.position.x++;
                                    break;
                                case Orientation.SOUTH:
                                    controller.position.y++;
                                    break;
                                case Orientation.WEST:
                                    controller.position.x--;
                                    break;
                            }

                            System.err.println("Go ahead");
                            controller.imprimir("Go ahead");
                            System.err.println("Position:" + controller.position.x + "," + controller.position.y);
                            System.err.println(Position.orientationList.get(controller.position.orientation));
                        } else if (line.contains("You got it")) {
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
