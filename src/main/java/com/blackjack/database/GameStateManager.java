package com.blackjack.database;

import com.vaadin.flow.component.UI;
import org.atmosphere.config.service.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

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
        UI  g =  UI.getCurrent();
        startStakeRound();
        for(Player player : playerList){
            player.takeCard(cards.draw());
            player.takeCard(cards.draw());
        }
        Broadcaster.startGame();

    }

    private void startStakeRound()  {
        while (playerList.stream().allMatch(player -> !player.hasIncreasedStake)){
            try {
                Thread.sleep(100); // Sleep for 100 milliseconds
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
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
