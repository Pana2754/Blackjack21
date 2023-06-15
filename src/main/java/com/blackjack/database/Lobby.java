package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.ArrayList;
import java.util.List;

@PageTitle("Waiting Lobby")
@Route("waiting-lobby")
public class Lobby extends VerticalLayout {
    private static final long serialVersionUID = 503398040364625051L;
    List<Player> players= new ArrayList<>();

    public Lobby() {



        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        Image logo = new Image("blackjack.png", "Logo");
        logo.setWidth("150px");
        logo.setHeight("150px");
        setWidthFull();

        H2 title = new H2("Waiting Lobby");

        Grid<Player> playersGrid = new Grid<>();
        playersGrid.addColumn(Player::getName).setHeader("Name");
        playersGrid.addComponentColumn(this::createReadyCheckbox).setHeader("Ready");
        playersGrid.setHeight("300px");
        playersGrid.setWidth("900px");

        Player activePlayer = getActivePlayer();
        if (activePlayer != null) {

            players.add(activePlayer);
            playersGrid.setItems(players);
        }

        add(logo, title, playersGrid);
        setAlignItems(Alignment.CENTER);
        setJustifyContentMode(JustifyContentMode.CENTER);
        setMargin(true);
        setSpacing(true);
    }

    private boolean isLoggedIn() {
        return getActivePlayer() != null;
    }

    private Player getActivePlayer() {
        return (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
    }

    private Checkbox createReadyCheckbox(Player player) {
        Checkbox checkbox = new Checkbox();
        checkbox.setValue(player.isReady());
        checkbox.addValueChangeListener(event -> {
            player.setReady(event.getValue());
        });
        return checkbox;
    }


}
