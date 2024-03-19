package com.example.controller.manager_tabs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.ListItemController;
import com.example.controller.dialogs.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class AllRequestsTabController implements Initializable {

    @FXML
    private ListView<String> repairRequestListView;
    public Label requestNumberLabel;
    public TextField equipTypeField;
    public TextArea descriptionTextArea;
    public Label clientNameLabel;
    public Label clientPhoneLabel;
    public TextField equipSerialField;
    public TextArea notesTextArea;
    public ScrollPane moreInfoScrollPane;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> repairerChoice;
    public ChoiceBox<String> priorityChoice;
    public DatePicker registerDatePicker;
    public DatePicker finishDatePicker;
    public Button refreshListBtn;
    private ClientPostgreSQL clientPostgreSQL;
    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";
    private Connection connection = null;
    private int currentRequestNumber = -1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moreInfoScrollPane.setVisible(false);
        loadRepairRequests();
        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");
        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании");
        registerDatePicker = new DatePicker();
        finishDatePicker = new DatePicker();

        // TODO: Сделать подгрузку списка исполнителей
        repairerChoice.getItems().addAll("Исполнитель1", "Исполнитель2", "Исполнитель3", "Исполнитель4");

        repairRequestListView.setOnMouseClicked(event -> {
            int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = repairRequestListView.getItems().get(selectedIndex);
                currentRequestNumber = Integer.parseInt(selectedItem);
                showMoreInfo(currentRequestNumber);
            }
        });
    }


    private void loadRepairRequests() {
        // Здесь вы должны использовать JDBC для подключения к вашей БД
        // и получения данных о заявках на ремонт

        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT request_number FROM repair_requests WHERE state != 'Новая' ORDER BY request_number";


            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Добавляем элементы в ListView
                        String requestNumber = resultSet.getString("request_number");

                        repairRequestListView.getItems().add(requestNumber);
                        repairRequestListView.setCellFactory(param -> {
                            return new ListCell<String>() {
                                @Override
                                protected void updateItem(String item, boolean empty) {
                                    super.updateItem(item, empty);

                                    if (item == null || empty) {
                                        setGraphic(null);
                                    } else {
                                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListItem.fxml"));
                                        try {
                                            Parent root = loader.load();
                                            ListItemController controller = loader.getController();
                                            controller.setRequestNumber(Integer.parseInt(item));
                                            setGraphic(root);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            setGraphic(null);
                                        }
                                    }
                                }
                            };
                        });
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void onActionRefresh(ActionEvent event) {
        repairRequestListView.getItems().clear();
        loadRepairRequests();
    }


    public void onActionSave(ActionEvent event) {
        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE repair_requests SET equipment_serial_number = ?, equipment_type = ?, description = ?, notes = ?, " +
                            "state = ?, repairer = ?, priority = ?, register_date = ?, finish_date = ? WHERE request_number = ?");
            preparedStatement.setString(1, equipSerialField.getText());
            preparedStatement.setString(2, equipTypeField.getText());
            preparedStatement.setString(3, descriptionTextArea.getText());
            preparedStatement.setString(4, notesTextArea.getText());
            preparedStatement.setString(5, stateChoice.getValue());
            preparedStatement.setString(6, repairerChoice.getValue());
            preparedStatement.setString(7, priorityChoice.getValue());
            preparedStatement.setDate(8, Date.valueOf(registerDatePicker.getValue()));
            preparedStatement.setDate(9, Date.valueOf(finishDatePicker.getValue()));
            preparedStatement.setInt(10, currentRequestNumber);
            preparedStatement.executeUpdate();


            MyAlert.showInfoAlert("Заявка успешно зарегистрирована.");
            moreInfoScrollPane.setVisible(false);
            repairRequestListView.getItems().remove(repairRequestListView.getSelectionModel().getSelectedIndex());

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при регистрации заявки.");
        } finally {
            try {
                connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void onActionDelete(ActionEvent actionEvent) {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String columnSearch = repairRequestListView.getItems().get(selectedIndex);
//            String columnSearchName = ((TableColumn) tableView.getColumns().get(0)).getText();
            String columnSearchName = "request_number";
            String selectedTable = "repair_requests";
            repairRequestListView.getItems().remove(selectedIndex);
            clientPostgreSQL = ClientPostgreSQL.getInstance();
            clientPostgreSQL.deleteRowTable(selectedTable, columnSearchName, columnSearch);
            moreInfoScrollPane.setVisible(false);
            MyAlert.showInfoAlert("Заявка успешно удалена");
        }
    }

    private void showMoreInfo(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT * FROM repair_requests WHERE request_number = ?";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, requestNumber);
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        requestNumberLabel.setText("Заявка №" + resultSet.getString(1));
                        equipTypeField.setText(resultSet.getString("equipment_type"));
                        descriptionTextArea.setText(resultSet.getString("description"));
                        clientNameLabel.setText("ФИО: " + resultSet.getString("client_name"));
                        clientPhoneLabel.setText("Телефон: " + resultSet.getString("client_phone"));
                        equipSerialField.setText(resultSet.getString("equipment_serial_number"));
                        notesTextArea.setText(resultSet.getString("notes"));
                        stateChoice.setValue(resultSet.getString("state"));
                        priorityChoice.setValue(resultSet.getString("priority"));
                        repairerChoice.setValue(resultSet.getString("repairer"));

//                        Date registerDate = resultSet.getDate("register_date");
//                        System.out.println(registerDate.toLocalDate());
//                        if (registerDate != null) {
//                            registerDatePicker.setValue(registerDate.toLocalDate());
//                        } else {
//                            registerDatePicker.setValue(null); // Если дата null, установите значение DatePicker в null
//                        }
//
//                        Date finishDate = resultSet.getDate("finish_date");
//                        System.out.println(finishDate);
//                        if (finishDate != null) {
//                            finishDatePicker.setValue(finishDate.toLocalDate());
//                        } else {
//                            finishDatePicker.setValue(null); // Если дата null, установите значение DatePicker в null
//                        }

                        StringConverter<LocalDate> converter = new StringConverter<LocalDate>() {
                            String pattern = "yyyy-MM-dd";
                            DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern(pattern);
                            @Override
                            public String toString(LocalDate date) {
                                if (date != null) {
                                    return dateFormatter.format(date);
                                } else {
                                    return "";
                                }
                            }

                            @Override
                            public LocalDate fromString(String string) {
                                if (string != null && !string.isEmpty()) {
                                    return LocalDate.parse(string, dateFormatter);
                                } else {
                                    return null;
                                }
                            }
                        };

                        registerDatePicker.setConverter(converter);
                        finishDatePicker.setConverter(converter);

                        registerDatePicker.setValue(resultSet.getDate("register_date").toLocalDate());
                        finishDatePicker.setValue(resultSet.getDate("finish_date").toLocalDate());

                        moreInfoScrollPane.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
