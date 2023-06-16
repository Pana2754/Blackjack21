package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {

    private static GameStateManager instance;
    private List<Player> playerList;

    public GameStateManager(){
        playerList = new ArrayList<>();
    }
    public static synchronized GameStateManager getInstance(){
        if(instance == null){
            instance = new GameStateManager();
        }
        return instance;
    }


}
