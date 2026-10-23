package ru.mirea.postamat.fx;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import ru.mirea.postamat.model.User;
import ru.mirea.postamat.repository.UserRepoImpl;
import ru.mirea.postamat.service.AuthService;

public class LoginController {

    @FXML private TextField phoneField;
    @FXML private PasswordField passwordField;
    @FXML private Label errorLabel;

    private final AuthService authService = new AuthService(new UserRepoImpl());

    @FXML
    private void onLogin() {
        errorLabel.setText("");
        try {
            User u = authService.login(phoneField.getText(), passwordField.getText());
            FXMLLoader loader = new FXMLLoader(LoginController.class.getResource("/ru/mirea/postamat/main.fxml"));
            Parent root = loader.load();
            MainApp.setRoot(root, "Точно не озон");

        } catch (Exception e) {
            e.printStackTrace();
            errorLabel.setText(e.getMessage());
        }
    }
}