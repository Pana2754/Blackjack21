package com.blackjack.database;

import org.atmosphere.config.service.Message;

import java.util.ArrayList;
import java.util.List;

public class GameStateManager {

    private static GameStateManager instance;
    private List<Player> playerList;

    private CardDeck cards;

    private GameStateManager() {
        playerList = new ArrayList<>();
        cards = new CardDeck();
        cards.shuffle();
    }

    public static synchronized GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public void giveCardToPlayer(IPlayer player) {
        player.takeCard(cards.draw());
    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public boolean isDealersTurn() {
        return playerList.stream().allMatch(player -> isHandOverPoints(player, 21) || player.getStanding());
    }


    public static boolean isHandOverPoints(IPlayer player, int maxPoints) {
        int handValue = player.getCardValues();

        if (player.getHand() == null) {
            return false;
        }
        if (handValue <= maxPoints) {
            return false;
        } else {
            return true;
        }
    }
    public void startGame(){
        for(Player player : playerList){
            player.takeCard(cards.draw());
            player.takeCard(cards.draw());
        }
    }
    public void resetGame(){
        cards = new CardDeck();
        cards.shuffle();
        for (Player player: playerList){
            player.resetHand();
        }
        startGame();
    }
}
