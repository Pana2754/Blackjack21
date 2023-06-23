package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import org.atmosphere.config.service.Message;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class GameStateManager {

    private static GameStateManager instance;
    private List<Player> playerList;

    public Dealer dealer;
    private CardDeck cards;

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
        if(isStakeRoundOver()){
            for(Player player : playerList){
                player.takeCard(cards.draw());
                player.takeCard(cards.draw());
            }
            dealer.takeCard(cards.draw());
            Broadcaster.startGame();
        }
    }

    public void dealerPlay(){
        while (isHandOverPoints(dealer, 16)){

            giveCardToPlayer(dealer);
        }
        if(isHandOverPoints(dealer, 21)){
            dealer.isOut= true;
        }
        Broadcaster.onDealerEnd();
    }

    private boolean isStakeRoundOver()  {

        return playerList.stream().allMatch(player -> player.hasIncreasedStake);
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
}
