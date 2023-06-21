package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player {

    private String playerName;

    private double CoinBalance;
    private Integer stake;
    private int CardValue;
    private boolean ready;
    private boolean banned;;

    private List<Card> cardList = new ArrayList<>();

    public Player(String playerName, boolean ready, double balance, Boolean isBanned){
        this.playerName = playerName;
        this.CoinBalance = balance;
        this.ready = ready;
        this.banned = isBanned;
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


    public double getBalance() {
        return CoinBalance;
    }
//
    public void setBalance(float newBalance) {
        this.CoinBalance = newBalance;
    }
    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        Player player = (Player) obj;
        return playerName.equals(player.playerName);
    }

    @Override
    public int hashCode() {
        return Objects.hash(playerName);
    }

}
