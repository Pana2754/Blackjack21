package com.blackjack.database;

import com.vaadin.flow.component.DetachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.checkbox.Checkbox;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;


@PageTitle("Admin Panel")
@Route("admin-panel")
public class AdminPanel extends VerticalLayout {

    public AdminPanel() {
        if (!isLoggedIn()) {
            UI.getCurrent().navigate(LoginView.class);
            return;
        }
        else if(!isAnAdmin()){
            UI.getCurrent().navigate(LoginView.class);
            return;
        }

        Image logo = new Image("blackjack.png", "Logo");
        logo.setWidth("150px");
        logo.setHeight("150px");
        setWidthFull();

        H2 title = new H2("Admin Lobby");

    }

    private boolean isAnAdmin() {
        Player player = getActivePlayer();
        if (player.getPlayerName().equals("admin"))
        {return true;}
        else return false;
    }

    private boolean isLoggedIn() {
            return getActivePlayer() != null;
        }



        private Player getActivePlayer() {
            return (Player) VaadinSession.getCurrent().getAttribute("activePlayer");
        }
}

