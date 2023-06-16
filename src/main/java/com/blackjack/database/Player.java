package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String playerName;
    private Integer CoinBalance;
    private Integer stake;
    private boolean ready;
    private List<Card> cardList = new ArrayList<>();
    private Integer cardValues;

    public Player(String playerName, boolean ready){
        this.playerName = playerName;
        this.CoinBalance = 1000;
        this.ready = ready;
    }
    public void takeCard(Card card){
        cardList.add(card);
    }

    private void increaseStake(int value){

    }

    public List<Card> getHand(){
        return cardList;
    }
    public String getPlayerName() {
        return playerName;
    }

    public boolean isReady() {
        return ready;
    }

    public void setReady(boolean ready) {
        this.ready = ready;
    }



}
