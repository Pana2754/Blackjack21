package com.blackjack.database;

import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.login.LoginForm;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
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

        LoginForm loginForm = new LoginForm();
        loginForm.setAction("login");
        loginForm.setForgotPasswordButtonVisible(false);
        loginForm.addLoginListener(event -> {
            String username = event.getUsername();
            String password = event.getPassword();

            try {
                if (!authenticate(username, password)) {
                    Notification.show("Invalid credentials");
                } else {
                    Notification.show("Login successful");
                }
            } catch (SQLException | ClassNotFoundException e) {
                throw new RuntimeException(e);
            }
        });

        Button registerButton = new Button("Register");
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        registerButton.getStyle()
                .set("--lumo-button-size", "var(--lumo-size-m)")
                .set("--lumo-primary-color", "#007bff")
                .set("--lumo-primary-text-color", "#ffffff");
        registerButton.addClickListener(event -> showRegistrationForm());

        add(new H2("Login"), loginForm, registerButton);
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
