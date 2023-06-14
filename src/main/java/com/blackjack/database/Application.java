package com.blackjack.database;

import com.vaadin.flow.component.page.AppShellConfigurator;
import com.vaadin.flow.theme.Theme;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@Theme(value = "mytodo")
public class Application implements AppShellConfigurator {
	private static final long serialVersionUID = -3067701186649745817L;

	public static void main(String[] args) throws ClassNotFoundException {
        SpringApplication.run(Application.class, args);
    }

}
