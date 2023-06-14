package com.blackjack.database;

import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("waiting-lobby")
public class Lobby extends VerticalLayout {
	private static final long serialVersionUID = 503398040364625051L;

	public Lobby() {
        Image logo = new Image("blackjack.png", "Logo");
        logo.setWidth("150px");
        logo.setHeight("150px");

        H2 title = new H2("Waiting Lobby");

        Grid<Player> playersGrid = new Grid<>(Player.class);
        playersGrid.setItems(
                new Player("Player 1", false),
                new Player("Player 2", true),
                new Player("Player 3", false)
        );
        playersGrid.setColumns("name", "ready");

        Checkbox readyCheckbox = new Checkbox("Ready");
        readyCheckbox.addValueChangeListener(event -> {
            boolean ready = event.getValue();
            Player selectedPlayer = playersGrid.asSingleSelect().getValue();
            if (selectedPlayer != null) {
                selectedPlayer.setReady(ready);
                playersGrid.getDataProvider().refreshAll();
            }
        });

        add(logo, title, playersGrid, readyCheckbox);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
    }

    public static class Player {
        private String name;
        private boolean ready;

        public Player(String name, boolean ready) {
            this.name = name;
            this.ready = ready;
        }

        public String getName() {
            return name;
        }

        public boolean isReady() {
            return ready;
        }

        public void setReady(boolean ready) {
            this.ready = ready;
        }
    }
}
