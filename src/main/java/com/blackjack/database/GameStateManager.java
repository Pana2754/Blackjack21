package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.notification.Notification;
import org.atmosphere.config.service.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStateManager {

    private static GameStateManager instance;
    private List<Player> playerList;

    public Dealer dealer;
    private CardDeck cards;

    private boolean isStakeRound = false;
    private boolean isPlayRound = false;
    private boolean isDealerRound = false;
    private boolean isGameEnd = false;

    private GameStateManager() {
        playerList = new ArrayList<>();
        dealer = new Dealer("dealer");
        cards = new CardDeck();
        cards.shuffle();
    }

    public static synchronized GameStateManager getInstance() {
        if (instance == null) {
            instance = new GameStateManager();
        }
        return instance;
    }

    public void startStakeRound(){
        Broadcaster.stakeRound();
        if (isStakeRoundOver()){
            startGame();
        }
    }
    public void raiseStake(int amount, Player player){
        if(player.getBalance() >= amount){
            player.increaseStake(amount);
            player.hasIncreasedStake = true;

            if(isStakeRoundOver()){
                startGame();
            }
        }
        else{
            Notification.show("You dont have enough Cash!");
        }
    }
    //startGame
    public void startGame(){

        for(Player player : playerList) {
            player.takeCard(cards.draw());
            player.takeCard(cards.draw());
        }
        dealer.takeCard(cards.draw());
        Broadcaster.startGame();
    }

    public void resetGame(){
        cards = new CardDeck();
        cards.shuffle();
        for (Player player: playerList){
            player.hasIncreasedStake = false;
            player.resetStake();
            player.resetHand();
            dealer.resetHand();
        }
        Broadcaster.resetGame();
    }


    public boolean isDealersTurn() {
        return playerList.stream().allMatch(player -> isHandOverPoints(player, 21) || player.getStanding());
    }
    public void addPlayer(Player player) {
        playerList.add(player);
    }

    public void giveCardToPlayer(IPlayer player) {
        player.takeCard(cards.draw());
        if (isHandOverPoints(player, 21)) {
            player.setOut(true);
            Broadcaster.playerIsOut(player);

        }
        Broadcaster.broadcast();

    }

    public List<Player> getPlayerList() {
        return playerList;
    }

    public  boolean isHandOverPoints(IPlayer player, int maxPoints) {
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

    private boolean isStakeRoundOver()  {

        return playerList.stream().allMatch(player -> player.hasIncreasedStake);
    }


}
