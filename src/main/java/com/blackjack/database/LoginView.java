package com.blackjack.database;

import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Image;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

import java.io.Serial;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.Period;

@Route("login")
public class LoginView extends VerticalLayout {
    @Serial
    private static final long serialVersionUID = -4286830884968200051L;
    private static final int MINIMUM_AGE = 18;

    public LoginView() {
        setJustifyContentMode(JustifyContentMode.CENTER);
        setAlignItems(Alignment.CENTER);

        Image image = createImage();
        TextField usernameField = createTextField();
        PasswordField passwordField = createPasswordField("Password");

        HorizontalLayout buttonLayout = new HorizontalLayout(
                createLoginButton(usernameField, passwordField),
                createRegisterButton()
        );

        add(image, usernameField, passwordField, buttonLayout);
        addClassName("login-view");
    }

    private Image createImage() {
        Image image = new Image("head.png", "Logo");
        image.addClassNames("login-logo");
        return image;
    }

        //
        TextField usernameField = new TextField("Username");
        usernameField.setWidth("300px");
        usernameField.addClassNames("login-input");

        PasswordField passwordField = new PasswordField("Password");
  
        passwordField.setWidth("300px");
        passwordField.addClassNames("login-input");
        return passwordField;
    }

    private Button createLoginButton(TextField usernameField, PasswordField passwordField) {
        Button loginButton = new Button("Login");
        loginButton.setWidth("150px");
        loginButton.addClickListener(event -> handleLogin(usernameField, passwordField));
        loginButton.addClassNames("red-button");
        return loginButton;
    }

    private Button createRegisterButton() {
        Button registerButton = new Button("Register");
        registerButton.setWidth("150px");
        registerButton.addClickListener(event -> showRegistrationForm());
        registerButton.addClassNames("green-button");
        return registerButton;
    }

    private void handleLogin(TextField usernameField, PasswordField passwordField) {
        String username = usernameField.getValue();
        String password = hashPassword(passwordField.getValue());

        try {
            if (isAdmin(username, password)) {
                Notification.show("Logged in as administrator!");
                UI.getCurrent().navigate("admin-panel");
            } else if (authenticate(username, password) && !passwordField.getValue().isEmpty()) {
                loginUser(username);
            } else {
                Notification.show("Invalid credentials!");
            }
        } catch (SQLException e) {
            handleException(e);
        }
    }

    private void loginUser(String username) throws SQLException {
        Notification.show("Login successful!");
        DatabaseLogic db = new DatabaseLogic();
        Player activePlayer = db.getUser(username);
        VaadinSession.getCurrent().setAttribute("activePlayer", activePlayer);
        Lobby.playerLoggedIn(activePlayer);

        UI.getCurrent().navigate("waiting-lobby");
    }

    private void handleException(Exception e) {
        e.printStackTrace();
        Notification.show("An error occurred. Please try again.");
    }

    private boolean isAdmin(String username, String password) throws SQLException {
        DatabaseLogic db = null;
        try {
            db = new DatabaseLogic();
            db.connectToDb();
            return !password.isEmpty() && authenticate(username, password) && db.checkAdmin(username);
        } finally {
            if (db != null) {
                db.closeConnection();
            }
        }
    }

    private boolean authenticate(String username, String password) throws SQLException {
        DatabaseLogic db = new DatabaseLogic();
        try {
            db.connectToDb();
            return db.checkLoginData(username, password);
        } finally {
            db.closeConnection();
        }
    }

    private boolean isAgeValid(LocalDate birthdate) {
        return Period.between(birthdate, LocalDate.now()).getYears() >= MINIMUM_AGE;
    }

    private void showRegistrationForm() {
        Dialog dialog = new Dialog();
        dialog.setWidth("400px");

        FormLayout formLayout = new FormLayout();
        TextField usernameField = createTextField();
        PasswordField passwordField = createPasswordField("Password");
        PasswordField confirmPasswordField = createPasswordField("Confirm Password");
        DatePicker datePicker = new DatePicker("Birthdate");

        Button registerButton = new Button("Register");
        registerButton.addClickListener(event -> handleRegistration(usernameField, passwordField, confirmPasswordField, datePicker, dialog));

        formLayout.add(usernameField, passwordField, confirmPasswordField, datePicker, registerButton);
        dialog.add(formLayout);
        dialog.open();
    }

    private void handleRegistration(TextField usernameField, PasswordField passwordField, PasswordField confirmPasswordField, DatePicker datePicker, Dialog dialog) {
        DatabaseLogic dbLogic = null;
        try {
            dbLogic = new DatabaseLogic();
            dbLogic.connectToDb();

            String username = usernameField.getValue();
            String password = hashPassword(passwordField.getValue());
            String confirmPassword = hashPassword(confirmPasswordField.getValue());
            LocalDate birthdate = datePicker.getValue();

            if (dbLogic.doesUserExist(username)) {
                Notification.show("Username already exists!");
            } else if (!password.equals(confirmPassword)) {
                Notification.show("Passwords do not match!");
            } else if (isAgeValid(birthdate)) {
                dbLogic.addUser(username, password, false, false, 1000);
                Notification.show("Successfully registered!");
                dialog.close();
            } else {
                Notification.show("You must be at least 18 years old to play!");
            }
        } catch (SQLException e) {
            handleException(e);
        } finally {
            if (dbLogic != null) {
                try {
                    dbLogic.closeConnection();
                } catch (SQLException e) {
                    handleException(e);
                }
            }
        }
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
