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

    public String toString(){
        return "x:" + x + " y:" + y;
    }

    Position(int x, int y){
        this.x = x;
        this.y = y;
        this.orientation = 0;
    }

}

public class Interact {

    Position position; // = new Position(0,0);

    private Controller controller;
    private Mapa mapa;

    Interact(Controller controller, Mapa mapa){
        this.controller = controller;
        // this.position = controller.position;
        this.controller.position = mapa.getStart();
        this.mapa = mapa;
    }



    public void go() {
        new Thread(() -> {
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
            position = mapa.getStart();
            // Scanner sc = new Scanner(System.in);
            // while (true) {
            while (position.x != mapa.getEnd().x || position.y != mapa.getEnd().y) {
                try {
                    TimeUnit.SECONDS.sleep(1);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                String c; // = sc.nextLine();

                // Primer paso, girar
                if (mapa.obstacleAt(position)){
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

            System.err.println("Tararí" + mapa.getEnd());

        }).start();
    }


    public static void main(String[] args) throws Exception{
      // new Interact().go();
    }

    public void setUpStreamGobbler(final InputStream is, final PrintStream ps) {
        final InputStreamReader streamReader = new InputStreamReader(is);
        new Thread(new Runnable() {
            public void run() {
                BufferedReader br = new BufferedReader(streamReader);
                String line = null;
                try {
                    while ((line = br.readLine()) != null) {
                        if (line.contains("girar")){
                            System.err.println("Girar");
                            controller.imprimir("Girar");
                            position.orientation = (position.orientation + 1) % 4;
                            System.err.println( Position.orientationList.get(position.orientation));
                        }else if (line.contains("Sigue")) {
                            switch (position.orientation) {
                                // FIXME Orientation
                                case 0: // NORTH
                                    position.y--;
                                    break;
                                case 1: // EAST
                                    position.x++;
                                    break;
                                case 2: // SOUTH
                                    position.y++;
                                    break;
                                case 3: // WEST
                                    position.x--;
                                    break;
                            }

                            System.err.println("Sigue");
                            controller.imprimir("Sigue");
                            System.err.println("Position:" +  position.x  + "," + position.y);
                            System.err.println( Position.orientationList.get(position.orientation));
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
        }).start();
    }
}
