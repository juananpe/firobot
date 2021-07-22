package sample;

import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;


public class Main3 {

    public static void main(String[] args) throws IOException, InterruptedException {

        final List<String> commands = Arrays.asList("/tmp/prueba3");
        final Process p = new ProcessBuilder(commands).start();

        // imprime erros
        new Thread(() -> {
            BufferedReader ir = new BufferedReader(new InputStreamReader(p.getErrorStream()));
            String line = null;
            try {
                while((line = ir.readLine()) != null){
                    System.out.printf(line);
                }
            } catch(IOException e) {}
        }).start();

        // imprime saida
        new Thread(() -> {
            BufferedReader ir = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = null;
            try {
                while((line = ir.readLine()) != null){
                    System.out.printf("%s\n", line);
                }
            } catch(IOException e) {}
        }).start();

        // imprime saida
        new Thread(() -> {
            int exitCode = 0;
            try {
                exitCode = p.waitFor();
            } catch(InterruptedException e) {
                e.printStackTrace();
            }
            System.out.printf("Exited with code %d\n", exitCode);
        }).start();


        final Scanner sc = new Scanner(System.in);
        final BufferedWriter bf = new BufferedWriter(new OutputStreamWriter(p.getOutputStream()));
        final String newLine = System.getProperty("line.separator");
        while(true){
            String c = sc.nextLine();
            bf.write(c);
            bf.newLine();
            bf.flush();
        }

    }

}
