package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class Player {

    private String playerName;

    private Integer CoinBalance;
    private int CardValue;
    private boolean ready;
    private boolean banned;;
    private Integer stake;
    private List<Card> cardList = new ArrayList<>();

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

    public int getCardValues(){

        int result = 0;
        for(Card card : cardList){
            try {
                result += Integer.parseInt(card.rank);
                continue;
            }
            catch (Exception e){}

            if (card.rank.equals("A")){
                result += 11;
            }
            else {
                result += 10;
            }

        }
        return result;
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

    public boolean isBanned() {
        return banned;
    }

    public void setBanned(boolean banned) {
        this.banned = banned;
    }



}
