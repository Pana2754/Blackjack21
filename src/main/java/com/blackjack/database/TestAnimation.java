package com.blackjack.database;

import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Random;

@Route("test-animation")
public class TestAnimation extends VerticalLayout {
    public TestAnimation() {
        List<String> cardImages = Arrays.asList("Clubs2.png", "Hearts3.png", "Spades4.png",
                "Diamonds5.png", "Clubs6.png", "Hearts7.png");

        Collections.shuffle(cardImages); // Shuffle the card images

        Random random = new Random();

        int startX = 300; // Starting X position
        int startY = 300; // Starting Y position

        for (int i = 0; i < cardImages.size(); i++) {
            Image card = new Image(cardImages.get(i), "");
            card.addClassName("card");

            int randomX = startX + random.nextInt(200) - 100; // Random X position within range [-100, 100] of startX
            int randomY = startY + random.nextInt(200) - 100; // Random Y position within range [-100, 100] of startY

            card.getStyle().set("left", randomX + "px");
            card.getStyle().set("top", randomY + "px");

            add(card);
        }
    }
}
