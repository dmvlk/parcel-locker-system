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
        addressField.setText(c.getLockerAddress() == null ? "" : c.getLockerAddress());
        numberField.setText(String.valueOf(c.getCellNumber()));
        sizeBox.setValue(c.getSizeType());
        statusBox.setValue(c.getStatus());
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

            cell.setLockerAddress(addr.trim());
            cell.setCellNumber(num);
            cell.setSizeType(sizeBox.getValue());
            cell.setStatus(statusBox.getValue());

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