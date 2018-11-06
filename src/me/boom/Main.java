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

        Game game = Game.getInstance();
        game.initGameWindow();
    }
}
