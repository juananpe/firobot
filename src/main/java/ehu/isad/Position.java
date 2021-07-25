package ehu.isad;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Position {
    public static String[] array = {"NORTH", "EAST", "SOUTH", "WEST"};
    public static List<String> orientationList = new ArrayList<String>(Arrays.asList(array));

    public int orientation = 0;
    public int x, y;
    // int orientation;

    public String toString() {
        return "x:" + x + " y:" + y;
    }

    public Position(int x, int y) {
        this.x = x;
        this.y = y;
        this.orientation = 0;
    }

}
