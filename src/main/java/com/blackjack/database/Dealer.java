package com.blackjack.database;

import java.util.ArrayList;
import java.util.List;

public class Dealer implements IPlayer{

    public String name;
    public List<Card> cards;
    public Dealer(String name){
        this.name = name;
        cards = new ArrayList<>();
    }
    public void takeCard(Card card){
        cards.add(card);
    }

    public int getCardValues(){
        int result = 0;
        if(cards == null){
            return result;
        }
        for(Card card : cards){
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
    public List<Card> getHand() {
        return cards;
    }
}
