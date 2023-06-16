package com.blackjack.database;

import com.vaadin.flow.component.charts.model.Label;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

public class GameEngine {

    public static boolean isHandOver21Points(Player player){
        int aces = 0;
        int handValue = player.getCardValues();

        for (Card card: player.getHand()){
            if(card.rank.equals("A")){
                aces += 1;
            }
        }

        if(handValue <= 21){
            return false;
        }
        if(handValue > 21 && aces == 0){
            return true;
        }
        for(int i = 1; i<=aces; i++){
            if(handValue - i*10 <= 21){
                return false;
            }
        }
        return true;





    }


}
