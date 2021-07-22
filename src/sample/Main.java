package sample;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;


public class Main {

    class Position {
        int x, y;
        int next = 0;

        Position(int x, int y){
            this.x = x;
            this.y = y;
        }

    }

   Position position = new Position(0,0);

    public static void main(String[] args) throws IOException {

        new Main().go();


    }

    public void go()  throws IOException {

        final List<String> commands = Arrays.asList("/bin/sh");
        final Process p = new ProcessBuilder(commands).start();

        // error stream
        new Thread(() -> {
            BufferedReader ir = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = null;
            try {
                while((line = ir.readLine()) != null){
                    System.out.printf(line);
                }
            } catch(IOException e) {}
        }).start();

        // input stream
        new Thread(() -> {
            BufferedReader ir = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            try {
                while((line = ir.readLine()) != null){
//                    if (line.contains("girar")){
//                        position.next = 1;
//                    }
                    System.out.printf("out: %s\n", line);
                }
            } catch(IOException e) {}
        }).start();

        // output stream
        new Thread(() -> {
            int exitCode = 0;
            try {
                exitCode = p.waitFor();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            // System.out.printf("Exited with code %d\n", exitCode);
            System.out.printf("Pos final: %d, %d\n", position.x, position.y);
        }).start();


        final Scanner sc = new Scanner(System.in);
        final BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        final String newLine = System.getProperty("line.separator");

        // int c = sc.nextInt();
        String c = sc.nextLine();
        try {
            position.y++;
            bf.write(c);
            bf.newLine();
            bf.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }


        Runnable helloRunnable = new Runnable() {
            public void run() {
//                String c = sc.nextLine();
                try {
                    position.y++;
                    bf.write(48);
                    bf.newLine();
                    bf.flush();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        };

    //    ScheduledExecutorService executor = Executors.newScheduledThreadPool(1);
      //  executor.scheduleAtFixedRate(helloRunnable, 3, 3, TimeUnit.SECONDS);

    }

}
