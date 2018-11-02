package me.boom;

import me.maps.MapLoader;

public class Main {

    /**
     * Primer bitmape
     */
    public static String bitMap[][] = {
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","1","2","2","2"},
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","0","0","0","0"},
            {"0","0","0","0","0","0","0","0"},
    };

    public static void main(String[] args) {
        bitMap = MapLoader.loadFromFile("maps/1.txt");

        Game game = new Game(800, 640, bitMap);
        game.initGameWindow();
    }
}
