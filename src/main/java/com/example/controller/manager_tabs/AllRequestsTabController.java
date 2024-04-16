package com.example.controller.manager_tabs;

import com.example.bdclient.ClientPostgreSQL;
import com.example.controller.ListItemController;
import com.example.controller.dialogs.MyAlert;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.util.StringConverter;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AllRequestsTabController implements Initializable {
    public VBox statesVBox;
    public VBox priorityVBox;
    public TextField dateFilterTF;
    public TextField idFilterTF;

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
    public BorderPane moreInfoPane;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> repairerChoice;
    public ChoiceBox<String> priorityChoice;
//    public DatePicker registerDatePicker;
//    public DatePicker finishDatePicker;
    public TextField registerDateTF;
    public TextField finishDateTF;
    public Button refreshListBtn;
    private ClientPostgreSQL clientPostgreSQL;
    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";
    private Connection connection = null;
    private String initialQuery;
    private String query;
    private int currentRequestNumber = -1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moreInfoPane.setVisible(false);

        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
            initialQuery = "SELECT request_number FROM repair_requests WHERE state != 'Новая' ORDER BY request_number";
            query = initialQuery;
            loadRepairRequests(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при соединении с базой данных.");
        }

        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");
        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании");

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

    @FXML
    public void applyFilters(ActionEvent event) {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных

        StringBuilder queryBuilder = new StringBuilder("SELECT request_number FROM repair_requests WHERE state != 'Новая' AND ");

        List<String> conditions = new ArrayList<>();

        // Фильтры по состояниям (используем оператор OR между выбранными состояниями)
        List<String> stateConditions = new ArrayList<>();
        for (Node node : statesVBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    stateConditions.add("state = '" + checkbox.getText() + "'");
                }
            }
        }
        if (!stateConditions.isEmpty()) {
            conditions.add("(" + String.join(" OR ", stateConditions) + ")");
        }

        // Фильтры по приоритетам (используем оператор OR между выбранными приоритетами)
        List<String> priorityConditions = new ArrayList<>();
        for (Node node : priorityVBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    priorityConditions.add("priority = '" + checkbox.getText() + "'");
                }
            }
        }
        if (!priorityConditions.isEmpty()) {
            conditions.add("(" + String.join(" OR ", priorityConditions) + ")");
        }

        // Фильтр по дате создания заявки
        String selectedDate = dateFilterTF.getText();
        if (selectedDate != null && !selectedDate.trim().isEmpty()) {
            conditions.add("register_date = '" + selectedDate + "'");
        }

        // Фильтр по номеру заявки
        String requestNumber = idFilterTF.getText().trim();
        if (!requestNumber.isEmpty()) {
            try {
                int requestNum = Integer.parseInt(requestNumber);
                conditions.add("request_number = " + requestNum);
            } catch (NumberFormatException e) {
                e.printStackTrace();
                MyAlert.showErrorAlert("Неверный формат номера заявки.");
                return;
            }
        }

        // Строим окончательное условие WHERE
        if (!conditions.isEmpty()) {
            queryBuilder.append(String.join(" AND ", conditions));
        } else {
            queryBuilder.append("1=1"); // Если фильтры не выбраны, выбираем все заявки
        }

        queryBuilder.append(" ORDER BY request_number");

        // Выполняем SQL-запрос и заполняем ListView
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            query = queryBuilder.toString();
            loadRepairRequests(connection, query);

            // Проверяем, видима ли подробная информация после применения фильтров
            if (!isRequestNumberInFilteredResults(currentRequestNumber)) {
                moreInfoPane.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при соединении с базой данных.");
        }

        MyAlert.showInfoAlert("Фильтры успешно применены");
    }


    // Метод для проверки, присутствует ли номер заявки в отфильтрованном списке
    private boolean isRequestNumberInFilteredResults(int requestNumber) {
        ObservableList<String> filteredItems = repairRequestListView.getItems();
        for (String item : filteredItems) {
            if (Integer.parseInt(item) == requestNumber) {
                return true;
            }
        }
        return false;
    }

    @FXML
    public void clearFilters(ActionEvent event) {
        // Очистка выбора с чекбоксов состояний
        for (Node node : statesVBox.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }

        // Очистка выбора с чекбоксов приоритета
        for (Node node : priorityVBox.getChildren()) {
            if (node instanceof CheckBox) {
                ((CheckBox) node).setSelected(false);
            }
        }

        // Очистка полей под дату и номер заявки
        dateFilterTF.clear();
        idFilterTF.clear();
    }



    private void loadRepairRequests(Connection connection, String query) {
        this.query = query;
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

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
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при загрузке данных.");
        }
    }

    @FXML
    public void onActionRefresh(ActionEvent event) {
        repairRequestListView.getItems().clear();
        loadRepairRequests(connection, query);
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
            preparedStatement.setDate(8, Date.valueOf(registerDateTF.getText()));
            preparedStatement.setDate(9, Date.valueOf(finishDateTF.getText()));
            preparedStatement.setInt(10, currentRequestNumber);
            preparedStatement.executeUpdate();


            MyAlert.showInfoAlert("Информация по заявке обновлена успешно.");

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
            moreInfoPane.setVisible(false);
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

                        registerDateTF.setText(resultSet.getDate("register_date").toString());
                        finishDateTF.setText(resultSet.getDate("finish_date").toString());

                        moreInfoPane.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}
