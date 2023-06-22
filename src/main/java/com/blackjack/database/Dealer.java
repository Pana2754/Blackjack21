package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class Dealer implements IPlayer{

    public String name;
    public List<Card> cards;

    public boolean isOut;
    public Dealer(String name){
        this.name = name;
        cards = new ArrayList<>();
        isOut = false;
    }
    public void takeCard(Card card){
        cards.add(card);
    }

    @Override
    public String getPlayerName() {
        return name;
    }

    @Override
    public void setOut(boolean out) {
       isOut = out;
    }

    public void resetHand(){
        cards= new ArrayList<>();
        isOut = false;
    }

    public int getCardValues(){
        int result = 0;
        if(cards == null){
            return result;
        }
        int aces = 0;
        for(Card card : cards){
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
    public List<Card> getHand() {
        return cards;
    }


}
