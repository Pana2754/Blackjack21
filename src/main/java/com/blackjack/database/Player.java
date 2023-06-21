package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class Player implements IPlayer {

    private String playerName;

    private double CoinBalance;
    private Integer stake;
    private int CardValue;
    private boolean ready;
    private boolean banned;;

    private boolean isStanding;

    public boolean isOut;

    private List<Card> cardList = new ArrayList<>();

    public Player(String playerName, boolean ready, double balance, Boolean isBanned){
        this.playerName = playerName;
        this.CoinBalance = balance;
        this.ready = ready;
        this.banned = isBanned;
        this.isStanding = false;
    }
    public void takeCard(Card card){
        cardList.add(card);
    }

    private void increaseStake(int value){

    }
    public void setStanding(boolean standing){
        this.isStanding = standing;
    }
    public boolean getStanding(){
        return this.isStanding;
    }
    public int getCardValues(){

        int result = 0;
        if(cardList == null){
            return result;
        }
        int aces = 0;
        for(Card card : cardList){
            if (card.rank.equals("A")) {
                aces += 1;
            }
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
        while (result > 21 && aces > 0){
            result -= 10;
            aces -= 1;
        }
        return result;
    }

    public void resetHand(){
        cardList= new ArrayList<>();
        isOut = false;
        isStanding = false;
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


    public double getBalance() {
        return CoinBalance;
    }
//
    public void setBalance(float newBalance) {
        this.CoinBalance = newBalance;
    }


}
