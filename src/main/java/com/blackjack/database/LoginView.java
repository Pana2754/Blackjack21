package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;

import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.SQLException;

@PageTitle("Login")
@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
    	        
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        Image image = new Image("blackjack.png", "Logo");
        image.addClassNames("login-logo"); 

        TextField usernameField = new TextField("Username");
        usernameField.setWidth("300px");
        usernameField.addClassNames("login-input"); 

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("300px");
        passwordField.addClassNames("login-input"); 
        Button loginButton = new Button("Login");
        loginButton.setWidth("100px");
        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            try {
                if (authenticate(username, password)) {
                    Notification.show("Login successful");
                } else {
                    Notification.show("Invalid credentials");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }
        });
        loginButton.addClassNames("login-button"); 

        Button registerButton = new Button("Register");
        registerButton.setWidth("100px");
        registerButton.addClickListener(event -> {
            showRegistrationForm();
        });
        registerButton.addClassNames("register-button");
        HorizontalLayout buttonLayout = new HorizontalLayout(loginButton, registerButton);


        add(image,usernameField, passwordField, buttonLayout);
        addClassName("login-view"); 
    }

    private boolean authenticate(String username, String password) throws SQLException{
        // PRÜFUNG PASSWORT UND USERNAME
        DatabaseLogic db = new DatabaseLogic();
        db.connectToDb();
        boolean result = db.checkLoginData(username, password);
        db.closeConnection();
        return result;
    }

    private void showRegistrationForm() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");

        Button registerButton = new Button("Register");
        registerButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            String confirmPassword = confirmPasswordField.getValue();

            if (password.equals(confirmPassword)) {
            	// REGISTRIERLOGIK
                DatabaseLogic dbLogic = new DatabaseLogic();
                try {
                    dbLogic.connectToDb();
                    dbLogic.addUser(username, password);
                    dbLogic.closeConnection();
                    Notification.show("Registration successful");
                    dialog.close();
                } catch (SQLException e) {
                    Notification.show("Username already exists. Please choose another one");
                }

            } else {
                Notification.show("Passwords do not match");
            }
        });

        formLayout.add(usernameField, passwordField, confirmPasswordField, registerButton);
        dialog.add(formLayout);

        dialog.open();
    }
}
