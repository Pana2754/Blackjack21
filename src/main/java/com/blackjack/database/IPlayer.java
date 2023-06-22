package com.blackjack.database;

import java.util.List;

public interface IPlayer {
    public void takeCard(Card card);

    public String getPlayerName();
    public void setOut(boolean out);

    public int getCardValues();
    public List<Card> getHand();
}
