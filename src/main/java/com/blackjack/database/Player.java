package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class Player implements IPlayer {

    private String playerName;

    private int CoinBalance;
    private int stake;

    public boolean hasIncreasedStake;
    private int CardValue;
    private boolean ready;
    private boolean banned;

    private boolean isStanding;

    public boolean isOut;

    private List<Card> cardList = new ArrayList<>();

    public Player(String playerName, boolean ready, int balance, Boolean isBanned){
        this.playerName = playerName;
        this.CoinBalance = balance;
        this.ready = ready;
        this.banned = isBanned;
        this.isStanding = false;
        hasIncreasedStake = false;
    }
    public void takeCard(Card card){
        cardList.add(card);
    }

    public void increaseStake(int value){
        stake+= value;
        CoinBalance -= value;
    }
    public void resetStake(){
        stake = 0;
    }
    public int getStake(){
        return stake;
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


    public int getBalance() {
        return CoinBalance;
    }
//
    public void setBalance(int newBalance) {
        this.CoinBalance = newBalance;
    }

    public void increaseBalance(int amount){
        this.CoinBalance+= amount;
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
