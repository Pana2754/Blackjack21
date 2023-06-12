package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Login")
@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);
        Image image = new Image("download.jpg", "Logo");
        
        
        H2 heading = new H2("Login");
        heading.getStyle().set("font-size", "24px");
        
        TextField usernameField = new TextField("Username");
        usernameField.setWidth("300px");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("300px");

        Button loginButton = new Button("Login");
        loginButton.setWidth("100px");
        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();
            
            if (authenticate(username, password)) {
                Notification.show("Login successful");
            } else {
                Notification.show("Invalid credentials");
            }
        });

        Button registerButton = new Button("Register");
        registerButton.setWidth("100px");
        registerButton.addClickListener(event -> {
            showRegistrationForm();
        });

        add(image, heading, usernameField, passwordField, loginButton, registerButton);
    }

    private boolean authenticate(String username, String password) {
        // PRÜFUNG PASSWORT UND USERNAME
        return username.equals("admin") && password.equals("admin");
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
            	Notification.show("Registration successful");
                dialog.close();
            } else {
                Notification.show("Passwords do not match");
            }
        });

        formLayout.add(usernameField, passwordField, confirmPasswordField, registerButton);
        dialog.add(formLayout);

        dialog.open();
    }
}
