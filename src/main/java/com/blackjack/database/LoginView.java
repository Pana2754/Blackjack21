package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

@Route("login")
public class LoginView extends VerticalLayout {
    private static final long serialVersionUID = -4286830884968200051L;
    public LoginView() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        Image image = new Image("head.png", "Logo");
        image.addClassNames("login-logo");


        TextField usernameField = new TextField("Username");
        usernameField.setWidth("300px");
        usernameField.addClassNames("login-input");

        PasswordField passwordField = new PasswordField("Password");
        passwordField.setWidth("300px");
        passwordField.addClassNames("login-input");

        Button loginButton = new Button("Login");
        loginButton.setWidth("150px");
        loginButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = hashPassword(passwordField.getValue());

            try {
                if (isAdmin(username, password)) {
                    Notification.show("Logged in as administrator!");
                    UI.getCurrent().navigate("admin-panel");
                } else if (authenticate(username, password) && !passwordField.getValue().isEmpty()) {
                    Notification.show("Login successful!");
                    DatabaseLogic db = new DatabaseLogic();
                    db.connectToDb();
                    double balance = db.getUserStats(username);
                    boolean banned = db.getBannedStatus(username);
                    Player activePlayer = new Player(username,banned,balance);
                    VaadinSession.getCurrent().setAttribute("activePlayer", activePlayer);
                    Lobby.playerLoggedIn(activePlayer);
                    db.closeConnection();
                    UI.getCurrent().navigate("waiting-lobby");
                } else {
                    Notification.show("Invalid credentials!");
                }
            } catch (SQLException e) {
                throw new RuntimeException(e);
            }

        });
        loginButton.addClassNames("red-button");

        Button registerButton = new Button("Register");
        registerButton.setWidth("150px");
        registerButton.addClickListener(event -> showRegistrationForm());
        registerButton.addClassNames("green-button");

        HorizontalLayout buttonLayout = new HorizontalLayout(loginButton, registerButton);

        add(image, usernameField, passwordField, buttonLayout);
        addClassName("login-view");
    }

    private boolean isAdmin(String username, String password) throws SQLException {
        if (!password.isEmpty() && authenticate(username, password)) {
            DatabaseLogic db = new DatabaseLogic();
            try {
                db.connectToDb();
                boolean result = db.checkAdmin(username);
                return result;
            } finally {
                db.closeConnection();
            }
        }
        return false;
    }

    private boolean authenticate(String username, String password) throws SQLException {
        DatabaseLogic db = new DatabaseLogic();
        try {

            db.connectToDb();
            boolean result = db.checkLoginData(username, password);

            return result;
        } finally {
            db.closeConnection();
        }
    }

    private boolean verifyAge(LocalDate selectedDate) {
        LocalDate currentTime = LocalDate.now();
        Period difference = Period.between(selectedDate, currentTime);
        int age = difference.getYears();

        if (age < 18) {
            Notification.show("You must be 18 to play!");
            return false;
        }
        return true;
    }

    private void showRegistrationForm() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();

        TextField usernameField = new TextField("Username");
        PasswordField passwordField = new PasswordField("Password");
        PasswordField confirmPasswordField = new PasswordField("Confirm Password");
        DatePicker datePicker = new DatePicker("Birthdate");

        Button registerButton = new Button("Register");
        registerButton.addClickListener(event -> {
            String username = usernameField.getValue();
            String password = hashPassword(passwordField.getValue());
            String confirmPassword = hashPassword(confirmPasswordField.getValue());

            LocalDate userAge = datePicker.getValue();

            DatabaseLogic dbLogic = new DatabaseLogic();
            try {
                dbLogic.connectToDb();

                // Assuming there's a method called doesUserExist, if not you need to implement it or remove this block
                if (dbLogic.doesUserExist(username)) {
                    Notification.show("Username already exists!");
                } else if (password.equals(confirmPassword) && verifyAge(userAge)) {
                    dbLogic.addUser(username, password, false, false, 1000);
                    Notification.show("Successfully registered!");
                    dialog.close();
                } else if (!password.equals(confirmPassword)) {
                    Notification.show("Passwords do not match!");
                }
            } catch (SQLException e) {
                e.printStackTrace();
                Notification.show("Registration failed!");
            } finally {
                try {
                    dbLogic.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        });

        formLayout.add(usernameField, passwordField, confirmPasswordField, datePicker, registerButton);
        dialog.add(formLayout);

        dialog.open();
    }


    private String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-512");
            byte[] hashedPassword = md.digest(password.getBytes());
            StringBuilder sb = new StringBuilder();
            for (byte b : hashedPassword) {
                sb.append(String.format("%02x", b));
            }
            return sb.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }
}
