package ru.mirea.postamat.fx;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import ru.mirea.postamat.model.*;
import ru.mirea.postamat.model.Cell;

public class CellDialogController {
    @FXML private TextField addressField;
    @FXML private TextField numberField;
    @FXML private ComboBox<SizeType> sizeBox;
    @FXML private ComboBox<CellStatus> statusBox;
    @FXML private Label errorLabel;

    private Cell cell;
    private boolean saved = false;

    @FXML
    public void initialize() {
        sizeBox.setItems(FXCollections.observableArrayList(SizeType.values()));
        statusBox.setItems(FXCollections.observableArrayList(CellStatus.values()));
    }

    public void setCell(Cell c) {
        this.cell = c;
        addressField.setText(c.get_locker_address() == null ? "" : c.get_locker_address());
        numberField.setText(String.valueOf(c.get_cell_number()));
        sizeBox.setValue(c.get_size_type());
        statusBox.setValue(c.get_status());
    }

    public boolean isSaved() { return saved; }

    @FXML
    private void onSave() {
        errorLabel.setText("");
        try {
            String addr = addressField.getText();
            if (addr == null || addr.isBlank())
                throw new IllegalArgumentException("Адрес не может быть пустым");
            if (addr.length() > 200)
                throw new IllegalArgumentException("Адрес длиннее 200 символов");

            int num;
            try { num = Integer.parseInt(numberField.getText().trim()); }
            catch (NumberFormatException e) { throw new IllegalArgumentException("Номер должен быть целым числом"); }
            if (num <= 0) throw new IllegalArgumentException("Номер должен быть > 0");

            if (sizeBox.getValue() == null) throw new IllegalArgumentException("Выберите размер");
            if (statusBox.getValue() == null) throw new IllegalArgumentException("Выберите статус");

            cell.set_locker_address(addr.trim());
            cell.set_cell_number(num);
            cell.set_size_type(sizeBox.getValue());
            cell.set_status(statusBox.getValue());

            saved = true;
            ((Stage) addressField.getScene().getWindow()).close();
        } catch (IllegalArgumentException e) {
            errorLabel.setText(e.getMessage());
        }
    }

    @FXML
    private void onCancel() {
        ((Stage) addressField.getScene().getWindow()).close();
    }
}