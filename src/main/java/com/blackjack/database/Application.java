package com.blackjack.database;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.theme.Theme;
import com.vaadin.flow.theme.lumo.Lumo;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.component.page.Push;
@Push
@SpringBootApplication
@Theme(variant = Lumo.DARK, value = "mytodo")
public class Application implements AppShellConfigurator {
	private static final long serialVersionUID = -3067701186649745817L;

	public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(Application.class, args);
    }

}
