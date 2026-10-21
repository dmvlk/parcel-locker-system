package ru.mirea.postamat.fx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mirea.postamat.model.Role;
import ru.mirea.postamat.model.User;

public class UserDialogController {

    @FXML private TextField fullNameField;
    @FXML private TextField phoneField;
    @FXML private TextField emailField;
    @FXML private PasswordField passwordField;
    @FXML private ComboBox<Role> roleBox;
    @FXML private Label errorLabel;
    @FXML private Label passwordHint;

    private User user;
    private boolean isNew;
    private boolean saved = false;

    @FXML
    public void initialize() {
        roleBox.setItems(FXCollections.observableArrayList(Role.values()));
    }

    public void setUser(User u, boolean isNew) {
        this.user = u;
        this.isNew = isNew;

        fullNameField.setText(u.get_full_name() == null ? "" : u.get_full_name());
        phoneField.setText(u.get_phone() == null ? "" : u.get_phone());
        emailField.setText(u.get_email() == null ? "" : u.get_email());
        roleBox.setValue(u.getRole() == null ? Role.USER : u.getRole());

        if (isNew) {
            passwordHint.setText("Пароль обязателен (мин. 6 символов)");
        } else {
            passwordHint.setText("Оставьте пустым, чтобы не менять пароль");
            passwordField.setPromptText("(не менять)");
        }
    }

    public boolean isSaved() { return saved; }

    @FXML
    private void onSave() {
        errorLabel.setText("");
        try {
            String name = fullNameField.getText();
            if (name == null || name.isBlank())
                throw new IllegalArgumentException("ФИО не может быть пустым");
            if (name.trim().length() > 100)
                throw new IllegalArgumentException("ФИО длиннее 100 символов");

            String phone = phoneField.getText();
            if (phone == null || phone.isBlank())
                throw new IllegalArgumentException("Телефон не может быть пустым");
            phone = phone.trim();
            if (!phone.matches("^\\+?[0-9]{10,15}$"))
                throw new IllegalArgumentException("Телефон: 10–15 цифр, возможно с '+'");

            String email = emailField.getText();
            if (email != null) {
                email = email.trim();
                if (email.isEmpty()) email = null;
                else if (!email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$"))
                    throw new IllegalArgumentException("Некорректный email");
            }

            String pwd = passwordField.getText();
            if (isNew) {
                if (pwd == null || pwd.length() < 6)
                    throw new IllegalArgumentException("Пароль должен быть не короче 6 символов");
            } else if (pwd != null && !pwd.isEmpty() && pwd.length() < 6) {
                throw new IllegalArgumentException("Новый пароль должен быть не короче 6 символов");
            }

            Role role = roleBox.getValue();
            if (role == null) throw new IllegalArgumentException("Выберите роль");

            user.set_full_name(name.trim());
            user.set_phone(phone);
            user.set_email(email);
            user.setRole(role);
            user.setPasswordHash((pwd == null || pwd.isBlank()) ? null : pwd);

            saved = true;
            ((Stage) fullNameField.getScene().getWindow()).close();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) fullNameField.getScene().getWindow()).close();
    }
}