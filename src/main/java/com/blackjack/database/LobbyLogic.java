package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class LobbyLogic {

    private List<Player> playerList;

    public LobbyLogic(){
        playerList = new ArrayList<>();
    }
    public void addPlayer(Player player){

        if(playerList.size() >= 7){

            return;
        }
        playerList.add(player);
    }

    public void startGame(){

    }
}
