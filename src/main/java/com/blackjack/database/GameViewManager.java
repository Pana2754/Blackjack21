package com.blackjack.database;

import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class GameViewManager {

    private static GameViewManager instance;
    private Map<String, GameView> gameViews;

    private GameViewManager() {
        gameViews = new ConcurrentHashMap<>();
    }

    public static synchronized GameViewManager getInstance() {
        if (instance == null) {
            instance = new GameViewManager();
        }
        return instance;
    }

    public void startGameViewForPlayer(Player player) {
        // Create a new GameView for this player
        GameView gameView = new GameView();

        // Store it in the map, associated with the player's name or ID
        gameViews.put(player.getPlayerName(), gameView);
    }

    public GameView getGameViewForPlayer(Player player) {
        return gameViews.get(player.getPlayerName());
    }
}
