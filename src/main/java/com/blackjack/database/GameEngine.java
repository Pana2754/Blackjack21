package com.blackjack.database;

import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public static boolean isHandOverPoints(IPlayer player, int maxPoints) {
        int aces = 0;
        int handValue = player.getCardValues();

        if(player.getHand() == null){
            return false;
        }
        for (Card card : player.getHand()) {
            if (card.rank.equals("A")) {
                aces += 1;
            }
        }

        if (handValue <= maxPoints) {
            return false;
        }
        if (handValue > maxPoints && aces == 0) {
            return true;
        }
        for (int i = 1; i <= aces; i++) {
            if (handValue - i * 10 <= maxPoints) {
                return false;
            }
        }
        return true;
    }
}

