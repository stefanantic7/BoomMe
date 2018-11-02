package me.maps;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Scanner;

public class MapLoader {

    public static String[][] loadFromFile(String filePath) {
        File file = new File(filePath);
        String[][] bitMap = new String[0][0];

        try (Scanner scanner = new Scanner(file)) {
            ArrayList<String[]> rows = new ArrayList<>();

            while (scanner.hasNextLine()) {
                String line = scanner.nextLine();
                rows.add(line.split(" "));
            }

            bitMap = rows.toArray(new String[0][]);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }


        return bitMap;
    }

}
