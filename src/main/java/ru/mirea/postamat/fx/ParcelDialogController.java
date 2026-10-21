package ru.mirea.postamat.fx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.StringConverter;

import ru.mirea.postamat.model.*;
import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.repository.CellRepo;
import ru.mirea.postamat.repository.CellRepoImpl;
import ru.mirea.postamat.repository.UserRepo;
import ru.mirea.postamat.repository.UserRepoImpl;

import java.util.List;

public class ParcelDialogController {

    @FXML private TextField codeField;
    @FXML private ComboBox<User> userBox;
    @FXML private ComboBox<Cell> cellBox;
    @FXML private ComboBox<ParcelStatus> statusBox;

    @FXML private VBox sendForm;
    @FXML private TextField sendPhoneField;
    @FXML private ComboBox<String> sendAddressBox;
    @FXML private ComboBox<SizeType> sendSizeBox;

    @FXML private VBox editForm;
    @FXML private Label errorLabel;
    @FXML private Label cellHint;

    private final UserRepo userRepo = new UserRepoImpl();
    private final CellRepo cellRepo = new CellRepoImpl();

    private Parcel parcel;
    private boolean isNew;
    private boolean saved = false;

    private String chosenPhone;
    private SizeType chosenSize;
    private String chosenAddress;

    @FXML
    public void initialize() {
        statusBox.setItems(FXCollections.observableArrayList(ParcelStatus.values()));
        sendSizeBox.setItems(FXCollections.observableArrayList(SizeType.values()));

        List<User> users = userRepo.get_all();
        userBox.setItems(FXCollections.observableArrayList(users));
        userBox.setConverter(new StringConverter<>() {
            @Override public String toString(User u) {
                if (u == null) return "";
                return u.get_full_name() + " (" + u.get_phone() + ")";
            }
            @Override public User fromString(String s) { return null; }
        });

        List<Cell> cells = cellRepo.get_all();
        cellBox.setItems(FXCollections.observableArrayList(cells));
        cellBox.setConverter(new StringConverter<>() {
            @Override public String toString(Cell c) {
                if (c == null) return "";
                return c.get_locker_address() + ", №" + c.get_cell_number()
                        + " (" + c.get_size_type() + "/" + c.get_status() + ")";
            }
            @Override public Cell fromString(String s) { return null; }
        });

        if (cellHint != null)
            cellHint.setText("Можно оставить пустым — тогда посылка без ячейки");
    }

    public void setupForSend(List<String> addresses) {
        this.isNew = true;
        this.parcel = new Parcel();

        if (editForm != null) { editForm.setVisible(false); editForm.setManaged(false); }
        if (sendForm != null) { sendForm.setVisible(true);  sendForm.setManaged(true); }

        sendAddressBox.setItems(FXCollections.observableArrayList(addresses));
        if (!addresses.isEmpty()) sendAddressBox.setValue(addresses.get(0));

        sendSizeBox.setValue(SizeType.MEDIUM);
        sendPhoneField.setPromptText("+79001234567");
    }

    public void setParcel(Parcel p, boolean isNew) {
        this.parcel = p;
        this.isNew = isNew;

        if (editForm != null) { editForm.setVisible(true);  editForm.setManaged(true); }
        if (sendForm != null) { sendForm.setVisible(false); sendForm.setManaged(false); }

        codeField.setText(p.get_code() == null ? "" : p.get_code());

        if (p.get_user_id() > 0) {
            userBox.getItems().stream()
                    .filter(u -> u.get_id() == p.get_user_id())
                    .findFirst()
                    .ifPresent(userBox::setValue);
        }
        if (p.get_cell_id() != null) {
            cellBox.getItems().stream()
                    .filter(c -> c.get_id() == p.get_cell_id())
                    .findFirst()
                    .ifPresent(cellBox::setValue);
        }
        statusBox.setValue(p.get_status() == null ? ParcelStatus.WAITING : p.get_status());

        if (isNew) codeField.setPromptText("5 цифр, напр. 12345");
    }

    public boolean isSaved() { return saved; }

    public String getChosenPhone()   { return chosenPhone; }
    public SizeType getChosenSize()  { return chosenSize; }
    public String getChosenAddress() { return chosenAddress; }

    @FXML
    private void onSave() {
        errorLabel.setText("");

        if (sendForm != null && sendForm.isVisible()) {
            try {
                String phone = sendPhoneField.getText();
                if (phone == null || phone.isBlank())
                    throw new IllegalArgumentException("Введите телефон получателя");
                phone = phone.trim();
                if (!phone.matches("^\\+?[0-9]{10,15}$"))
                    throw new IllegalArgumentException("Телефон: 10–15 цифр, возможно с '+'");

                String address = sendAddressBox.getValue();
                if (address == null || address.isBlank())
                    throw new IllegalArgumentException("Выберите адрес постамата");

                SizeType size = sendSizeBox.getValue();
                if (size == null)
                    throw new IllegalArgumentException("Выберите размер ячейки");

                this.chosenPhone = phone;
                this.chosenSize = size;
                this.chosenAddress = address;

                saved = true;
                ((Stage) sendPhoneField.getScene().getWindow()).close();
            } catch (IllegalArgumentException e) {
                errorLabel.setText(e.getMessage());
            }
            return;
        }

        try {
            String code = codeField.getText();
            if (code == null || code.isBlank())
                throw new IllegalArgumentException("Код не может быть пустым");
            code = code.trim();
            if (!code.matches("\\d{5}"))
                throw new IllegalArgumentException("Код должен состоять из 5 цифр");

            User user = userBox.getValue();
            if (user == null)
                throw new IllegalArgumentException("Выберите получателя");

            Cell cell = cellBox.getValue();

            ParcelStatus status = statusBox.getValue();
            if (status == null)
                throw new IllegalArgumentException("Выберите статус");

            if (status == ParcelStatus.WAITING && cell == null)
                throw new IllegalArgumentException("Для статуса WAITING нужно выбрать ячейку");

            parcel.set_code(code);
            parcel.set_user_id(user.get_id());
            parcel.set_cell_id(cell == null ? null : cell.get_id());
            parcel.set_status(status);

            saved = true;
            ((Stage) codeField.getScene().getWindow()).close();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        if (sendForm != null && sendForm.isVisible())
            ((Stage) sendPhoneField.getScene().getWindow()).close();
        else
            ((Stage) codeField.getScene().getWindow()).close();
    }
}