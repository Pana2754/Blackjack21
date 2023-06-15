package com.blackjack.database;

import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("BlackJack")
@Route(value = "")
public class MainView extends HorizontalLayout {

    public MainView() {
    	add(new LoginView());
    }

}
