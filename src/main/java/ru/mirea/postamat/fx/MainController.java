package ru.mirea.postamat.fx;

import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;

import ru.mirea.postamat.model.*;
import ru.mirea.postamat.model.Cell;
import ru.mirea.postamat.repository.*;
import ru.mirea.postamat.service.*;
import ru.mirea.postamat.util.*;

import java.io.File;
import java.time.format.DateTimeFormatter;
import java.util.List;

public class MainController {

    private final UserRepo userRepo = new UserRepoImpl();
    private final CellRepo cellRepo = new CellRepoImpl();
    private final ParcelRepo parcelRepo = new ParcelRepoImpl();

    private final UserService userService = new UserService(userRepo);
    private final CellService cellService = new CellService(cellRepo);
    private final ParcelService parcelService = new ParcelService(parcelRepo, cellRepo, userRepo);
    private final StatsService statsService = new StatsService(userRepo, cellRepo, parcelRepo);

    private final ObservableList<Cell> cellData = FXCollections.observableArrayList();
    private final ObservableList<User> userData = FXCollections.observableArrayList();
    private final ObservableList<Parcel> parcelData = FXCollections.observableArrayList();

    @FXML private TabPane mainTabPane;
    @FXML private Tab cellsTab;
    @FXML private Tab usersTab;
    @FXML private Tab statsTab;

    @FXML private Label userLabel;

    @FXML private TableView<Cell> cellTable;
    @FXML private TableColumn<Cell, Integer>    colCellId;
    @FXML private TableColumn<Cell, String>     colCellAddress;
    @FXML private TableColumn<Cell, Integer>    colCellNumber;
    @FXML private TableColumn<Cell, SizeType>   colCellSize;
    @FXML private TableColumn<Cell, CellStatus> colCellStatus;
    @FXML private TextField cellSearchField;
    @FXML private ComboBox<CellStatus> cellStatusFilter;
    @FXML private ComboBox<SizeType>   cellSizeFilter;
    @FXML private ComboBox<String>     cellSortBox;

    @FXML private Button cellAddButton;
    @FXML private Button cellEditButton;
    @FXML private Button cellDeleteButton;

    @FXML private TableView<User> userTable;
    @FXML private TableColumn<User, Integer> colUserId;
    @FXML private TableColumn<User, String>  colUserFullName;
    @FXML private TableColumn<User, String>  colUserPhone;
    @FXML private TableColumn<User, String>  colUserEmail;
    @FXML private TableColumn<User, Role>    colUserRole;
    @FXML private TextField userSearchField;

    @FXML private Button userAddButton;
    @FXML private Button userEditButton;
    @FXML private Button userDeleteButton;

    @FXML private TableView<Parcel> parcelTable;
    @FXML private TableColumn<Parcel, Integer>       colParcelId;
    @FXML private TableColumn<Parcel, String>        colParcelCode;
    @FXML private TableColumn<Parcel, Integer>       colParcelUserId;
    @FXML private TableColumn<Parcel, Integer>       colParcelCellId;
    @FXML private TableColumn<Parcel, ParcelStatus>  colParcelStatus;
    @FXML private TableColumn<Parcel, String>        colParcelDate;

    @FXML private TextArea statsLabel;

    @FXML
    public void initialize() {
        if (Session.get() == null) { onLogout(); return; }

        userLabel.setText("Пользователь: " + Session.get().getFullName());

        // Ячейки
        colCellId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colCellAddress.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getLockerAddress()));
        colCellNumber.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCellNumber()));
        colCellSize.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getSizeType()));
        colCellStatus.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStatus()));

        // Пользователи
        colUserId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colUserFullName.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getFullName()));
        colUserPhone.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getPhone()));
        colUserEmail.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getEmail() == null ? "" : c.getValue().getEmail()));
        colUserRole.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getRole()));

        // Посылки
        colParcelId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getId()));
        colParcelCode.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCode()));
        colParcelUserId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getUserId()));
        colParcelCellId.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getCellId()));
        colParcelStatus.setCellValueFactory(c -> new SimpleObjectProperty<>(c.getValue().getStatus()));
        colParcelDate.setCellValueFactory(c -> new SimpleStringProperty(c.getValue().getCreatedAt() == null ? "" : c.getValue().getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"))));

        cellTable.setItems(cellData);
        userTable.setItems(userData);
        parcelTable.setItems(parcelData);

        cellStatusFilter.setItems(FXCollections.observableArrayList(CellStatus.values()));
        cellSizeFilter.setItems(FXCollections.observableArrayList(SizeType.values()));
        cellSortBox.setItems(FXCollections.observableArrayList("По адресу", "По номеру"));

        boolean isAdmin = Session.isAdmin();

        if (!isAdmin) {
            if (cellsTab != null && mainTabPane != null) mainTabPane.getTabs().remove(cellsTab);
            if (usersTab != null && mainTabPane != null) mainTabPane.getTabs().remove(usersTab);
            if (statsTab != null && mainTabPane != null) mainTabPane.getTabs().remove(statsTab);
        }

        if (cellAddButton != null)    cellAddButton.setDisable(!isAdmin);
        if (cellEditButton != null)   cellEditButton.setDisable(!isAdmin);
        if (cellDeleteButton != null) cellDeleteButton.setDisable(!isAdmin);

        if (userAddButton != null)    userAddButton.setDisable(!isAdmin);
        if (userEditButton != null)   userEditButton.setDisable(!isAdmin);
        if (userDeleteButton != null) userDeleteButton.setDisable(!isAdmin);

        refreshAll();
    }

    private void refreshAll() {
        loadCells();
        loadUsers();
        loadParcels();
    }
    private void loadCells() { cellData.setAll(cellService.getAll()); }
    private void loadUsers() { userData.setAll(userService.getAll()); }

    private void loadParcels() {
        List<Parcel> all = parcelRepo.getAll();
        if (Session.isAdmin()) {
            parcelData.setAll(all);
        } else {
            int myId = Session.get().getId();
            parcelData.setAll(all.stream()
                    .filter(p -> p.getUserId() == myId)
                    .toList());
        }
    }

    @FXML
    private void onCellSearch() {
        String search = cellSearchField.getText();
        CellStatus st = cellStatusFilter.getValue();
        SizeType sz = cellSizeFilter.getValue();
        String sort = cellSortBox.getValue();

        List<Cell> result = cellService.getAll();

        if (search != null && !search.isBlank())
            result = result.stream()
                    .filter(c -> c.getLockerAddress().toLowerCase()
                            .contains(search.toLowerCase().trim()))
                    .toList();

        if (st != null) result = result.stream().filter(c -> c.getStatus() == st).toList();
        if (sz != null) result = result.stream().filter(c -> c.getSizeType() == sz).toList();

        if ("По адресу".equals(sort))
            result = result.stream()
                    .sorted((a, b) -> a.getLockerAddress().compareTo(b.getLockerAddress()))
                    .toList();
        else if ("По номеру".equals(sort))
            result = result.stream()
                    .sorted((a, b) -> Integer.compare(a.getCellNumber(), b.getCellNumber()))
                    .toList();

        cellData.setAll(result);
    }

    @FXML
    private void onCellReset() {
        cellSearchField.clear();
        cellStatusFilter.setValue(null);
        cellSizeFilter.setValue(null);
        cellSortBox.setValue(null);
        loadCells();
    }

    @FXML
    private void onAddCell() {
        if (!Session.isAdmin()) { showError("Только администратор может добавлять ячейки"); return; }
        Cell editing = new Cell();
        if (openCellDialog(editing)) {
            try {
                cellService.create(editing.getLockerAddress(), editing.getCellNumber(),
                        editing.getSizeType(), editing.getStatus());
                loadCells();
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    @FXML
    private void onEditCell() {
        if (!Session.isAdmin()) { showError("Только администратор может изменять ячейки"); return; }
        Cell selected = cellTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите ячейку"); return; }

        Cell copy = new Cell(selected.getId(), selected.getLockerAddress(),
                selected.getCellNumber(), selected.getSizeType(), selected.getStatus());
        if (openCellDialog(copy)) {
            try {
                cellService.update(copy.getId(), copy.getLockerAddress(),
                        copy.getCellNumber(), copy.getSizeType(), copy.getStatus());
                loadCells();
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    @FXML
    private void onDeleteCell() {
        if (!Session.isAdmin()) { showError("Только администратор может удалять ячейки"); return; }
        Cell selected = cellTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите ячейку"); return; }
        if (!confirm("Удалить ячейку ID=" + selected.getId() + "?")) return;
        try {
            cellService.delete(selected.getId());
            loadCells();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private boolean openCellDialog(Cell cell) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/ru/mirea/postamat/cell-dialog.fxml"));
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setScene(new javafx.scene.Scene(loader.load()));
            dlg.getScene().getStylesheets().add(MainController.class.getResource("/ru/mirea/postamat/css/app.css").toExternalForm());
            CellDialogController c = loader.getController();
            c.setCell(cell);
            dlg.showAndWait();
            return c.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось открыть диалог: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void onUserSearch() {
        String q = userSearchField.getText();
        if (q == null || q.isBlank()) { loadUsers(); return; }
        List<User> byPhone = userService.search_by_phone(q);
        if (!byPhone.isEmpty()) { userData.setAll(byPhone); return; }
        userData.setAll(userService.search_by_full_name(q));
    }

    @FXML
    private void onUserReset() { userSearchField.clear(); loadUsers(); }

    @FXML
    private void onAddUser() {
        if (!Session.isAdmin()) { showError("Только администратор может добавлять пользователей"); return; }
        User editing = new User();
        if (openUserDialog(editing, true)) {
            try {
                userService.create(editing.getFullName(), editing.getPhone(), editing.getEmail(), editing.getPasswordHash(), editing.getRole());
                loadUsers();
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    @FXML
    private void onEditUser() {
        if (!Session.isAdmin()) { showError("Только администратор может редактировать пользователей"); return; }
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите пользователя"); return; }

        User copy = new User(selected.getId(), selected.getFullName(), selected.getPhone(), selected.getEmail(), null, selected.getRole());

        if (openUserDialog(copy, false)) {
            try {
                String raw = copy.getPasswordHash();
                userService.update(copy.getId(), copy.getFullName(), copy.getPhone(), copy.getEmail(), (raw == null || raw.isBlank()) ? null : raw, copy.getRole());
                loadUsers();
            } catch (Exception e) { showError(e.getMessage()); }
        }
    }

    @FXML
    private void onDeleteUser() {
        if (!Session.isAdmin()) { showError("Только администратор может удалять пользователей"); return; }
        User selected = userTable.getSelectionModel().getSelectedItem();
        if (selected == null) { showError("Выберите пользователя"); return; }
        if (!confirm("Удалить пользователя ID=" + selected.getId() + "?")) return;
        try {
            userService.delete(selected.getId());
            loadUsers();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private boolean openUserDialog(User user, boolean isNew) {
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/ru/mirea/postamat/user-dialog.fxml"));
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setScene(new javafx.scene.Scene(loader.load()));
            dlg.getScene().getStylesheets().add(MainController.class.getResource("/ru/mirea/postamat/css/app.css").toExternalForm());
            UserDialogController c = loader.getController();
            c.setUser(user, isNew);
            dlg.showAndWait();
            return c.isSaved();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Не удалось открыть диалог: " + e.getMessage());
            return false;
        }
    }

    @FXML
    private void onPutParcel() {
        try {
            List<String> addresses = cellService.getAllAddresses();
            if (addresses.isEmpty()) { showError("Нет ни одного постамата"); return; }

            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/ru/mirea/postamat/parcel-dialog.fxml"));
            Stage dlg = new Stage();
            dlg.initModality(Modality.APPLICATION_MODAL);
            dlg.setScene(new javafx.scene.Scene(loader.load()));
            dlg.getScene().getStylesheets().add(MainController.class.getResource("/ru/mirea/postamat/css/app.css").toExternalForm());

            ParcelDialogController c = loader.getController();
            c.setupForSend(addresses);

            dlg.showAndWait();
            if (!c.isSaved()) return;

            String code = parcelService.put_parcel(c.getChosenPhone(), c.getChosenSize(), c.getChosenAddress());
            showInfo("Посылка создана.\nКод получения: " + code);
            refreshAll();
        } catch (Exception e) {
            e.printStackTrace();
            showError("Ошибка: " + e.getMessage());
        }
    }

    @FXML
    private void onTakeParcel() {
        TextInputDialog dlg = new TextInputDialog();
        dlg.setTitle("Получение посылки");
        dlg.setHeaderText("Введите код получения");
        dlg.setContentText("Код:");
        String code = dlg.showAndWait().orElse(null);
        if (code == null || code.isBlank()) return;
        try {
            Parcel p = parcelService.takeParcel(code.trim());
            showInfo("Посылка выдана. ID: " + p.getId());
            refreshAll();
        } catch (Exception e) { showError(e.getMessage()); }
    }

    @FXML
    private void onRefreshParcels() { loadParcels(); }

    @FXML
    private void onExportCellsXlsx() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("Excel", "*.xlsx"));
        File f = fc.showSaveDialog(cellTable.getScene().getWindow());
        if (f == null) return;
        try {
            ExcelExport.exportCells(cellData, f.getAbsolutePath());
            showInfo("Экспортировано в " + f.getAbsolutePath());
        } catch (Exception e) { showError(e.getMessage()); }
    }

    @FXML
    private void onExportCellsCsv() {
        FileChooser fc = new FileChooser();
        fc.getExtensionFilters().add(new FileChooser.ExtensionFilter("CSV", "*.csv"));
        File f = fc.showSaveDialog(cellTable.getScene().getWindow());
        if (f == null) return;
        try {
            CsvExport.exportCells(cellData, f.getAbsolutePath());
            showInfo("Экспортировано в " + f.getAbsolutePath());
        } catch (Exception e) { showError(e.getMessage()); }
    }

    @FXML
    private void onStats() {
        try { statsLabel.setText(statsService.buildReport()); }
        catch (Exception e) { showError(e.getMessage()); }
    }

    @FXML
    private void onLogout() {
        Session.set(null);
        try {
            FXMLLoader loader = new FXMLLoader(MainController.class.getResource("/ru/mirea/postamat/login.fxml"));
            Parent root = loader.load();
            MainApp.setRoot(root, "Точно не озон");
        } catch (Exception e) { showError(e.getMessage()); }
    }

    private void showError(String msg) {
        Alert a = new Alert(Alert.AlertType.ERROR);
        a.setTitle("Ошибка"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
    private void showInfo(String msg) {
        Alert a = new Alert(Alert.AlertType.INFORMATION);
        a.setTitle("Готово"); a.setHeaderText(null); a.setContentText(msg);
        a.showAndWait();
    }
    private boolean confirm(String msg) {
        Alert a = new Alert(Alert.AlertType.CONFIRMATION, msg, ButtonType.OK, ButtonType.CANCEL);
        return a.showAndWait().orElse(ButtonType.CANCEL) == ButtonType.OK;
    }
}