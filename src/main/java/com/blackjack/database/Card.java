package com.blackjack.database;

public class Card {

    public String suit;
    public String rank;
    public String id;

    public String imagePath;
    public Card(String suit, String rank, String imagePath){
        this.suit = suit;
        this.rank = rank;
        this.imagePath = imagePath;
    }

    public String getId(){
        return id;
    }
}
