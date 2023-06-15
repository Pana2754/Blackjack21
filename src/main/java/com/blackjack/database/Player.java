package com.blackjack.database;

public class Player {

    private String playerName;
    private Integer CoinBalance;
    private Integer stake;
    private boolean ready;

    private Integer cardValues;

    public Player(String playerName, boolean ready){
        this.playerName = playerName;
        this.CoinBalance = 1000;
        this.ready = ready;
    }
    private void getAnotherCard(){

    }

    private void increaseStake(int value){

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
