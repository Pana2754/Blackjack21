package com.blackjack.database;

import java.util.List;

public class Dealer {

    public String name;
    public List<Card> cards;
    public int cardValues;
    public Dealer(String name){
        this.name = name;
    }
}
