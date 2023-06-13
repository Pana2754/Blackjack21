package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.textfield.TextFieldVariant;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.sql.SQLException;

@PageTitle("Login")
@Route("login")
public class LoginView extends VerticalLayout {

    public LoginView() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        H2 heading = new H2("Login");
        heading.getStyle().set("font-size", "24px");
        heading.setClassName("h2Login");


        TextField usernameField = new TextField("Username");
        //usernameField.getStyle().set("background-color", "#ffffff");
        usernameField.setWidth("300px");
        usernameField.addThemeVariants(
                TextFieldVariant.LUMO_SMALL,
                TextFieldVariant.LUMO_ALIGN_RIGHT,
                TextFieldVariant.LUMO_HELPER_ABOVE_FIELD
        );
        usernameField.getStyle().set("--vaadin-input-field-border-width", "1px");


        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("300px");
        passwordField.addThemeVariants(
                TextFieldVariant.LUMO_SMALL,
                TextFieldVariant.LUMO_ALIGN_RIGHT,
                TextFieldVariant.LUMO_HELPER_ABOVE_FIELD
        );
        passwordField.getStyle().set("--vaadin-input-field-border-width", "1px");

        Button loginButton = new Button("Login");
        loginButton.setWidth("100px");
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);

        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = passwordField.getValue();

            try {
                if (!authenticate(username, password)) {
                    Notification.show("Invalid credentials");
                } else {
                    Notification.show("Login successful");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            } catch (ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Button registerButton = new Button("Register");
        registerButton.setWidth("100px");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.addClickListener(event -> {
            showRegistrationForm();
        });

        add(heading, usernameField, passwordField, loginButton, registerButton);
    }

    private boolean authenticate(String username, String password) throws SQLException, ClassNotFoundException {
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
