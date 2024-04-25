package com.example.controller.repairer_tabs;

import com.example.bdclient.Database;
import com.example.controller.ListItemController;
import com.example.controller.MainViewController;
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

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class CommonRequestsTabController implements Initializable {
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
    public TextArea commentsTextArea;
    public ScrollPane moreInfoScrollPane;
    public BorderPane moreInfoPane;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;

    // TODO: для дат заменить текстовые поля на пикеры
//    public DatePicker registerDatePicker;
//    public DatePicker finishDatePicker;
    public TextField registerDateTF;
    public TextField finishDateTF;

    public Button refreshListBtn;
    public Button createOrCheckReportBtn;
    private Database database;
    private final String DB_URL = Database.URL;
    private final String LOGIN = Database.ROOT_LOGIN;
    private final String PASSWORD = Database.ROOT_PASS;
    private Connection connection = null;
    private String initialQuery;
    private String query;
    private int currentRequestNumber = -1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moreInfoPane.setVisible(false);
        createOrCheckReportBtn.setVisible(false);
        database = Database.getInstance();

        try {
            connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
            initialQuery = "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = false " +
                    "ORDER BY r.id";
            query = initialQuery;
            loadRepairRequests(connection, query);
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при соединении с базой данных.");
        }

        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");
        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании", "Закрыта");

        ArrayList<String> repairersList = database.stringListQuery("name", "members", "role = 'repairer'", "name");
        responsibleRepairerChoice.getItems().addAll(repairersList);
        additionalRepairerChoice.getItems().add("Нет");
        additionalRepairerChoice.getItems().addAll(repairersList);
        additionalRepairerChoice.setValue("Нет");

        repairRequestListView.setOnMouseClicked(event -> {
            int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = repairRequestListView.getItems().get(selectedIndex);
                currentRequestNumber = Integer.parseInt(selectedItem);
                System.out.println("Выбрана заявка №" + currentRequestNumber);
                showMoreInfo(currentRequestNumber);
            }
        });
    }

    @FXML
    public void applyFilters(ActionEvent event) {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных

        StringBuilder queryBuilder = new StringBuilder("SELECT r.id " +
                "FROM requests r " +
                "JOIN request_processes rp ON r.id = rp.request_id " +
                "JOIN assignments a ON r.id = a.id_request " +
                "WHERE r.status != 'Новая' " +
                "AND a.member_id = " + MainViewController.userID + " " +
                "AND a.is_responsible = false AND ");

        List<String> conditions = new ArrayList<>();

        // Фильтры по состояниям (используем оператор OR между выбранными состояниями)
        List<String> stateConditions = new ArrayList<>();
        for (Node node : statesVBox.getChildren()) {
            if (node instanceof CheckBox) {
                CheckBox checkbox = (CheckBox) node;
                if (checkbox.isSelected()) {
                    stateConditions.add("r.status = '" + checkbox.getText() + "'");
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
                    priorityConditions.add("rp.priority = '" + checkbox.getText() + "'");
                }
            }
        }
        if (!priorityConditions.isEmpty()) {
            conditions.add("(" + String.join(" OR ", priorityConditions) + ")");
        }

        // Фильтр по дате создания заявки
        String selectedDate = dateFilterTF.getText();
        if (selectedDate != null && !selectedDate.trim().isEmpty()) {
            conditions.add("r.id IN (SELECT request_id FROM request_regs WHERE date_start = '" + selectedDate + "')");
        }

        // Фильтр по номеру заявки
        String requestNumber = idFilterTF.getText().trim();
        if (!requestNumber.isEmpty()) {
            try {
                int requestId = Integer.parseInt(requestNumber);
                conditions.add("r.id = " + requestId);
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

        queryBuilder.append(" ORDER BY r.id");

        // Выполняем SQL-запрос и заполняем ListView
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = queryBuilder.toString();
            loadRepairRequests(connection, query);

            // Проверяем, видима ли подробная информация после применения фильтров
            if (!isRequestIdInFilteredResults(currentRequestNumber)) {
                moreInfoPane.setVisible(false);
            }

        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при соединении с базой данных.");
        }

        MyAlert.showInfoAlert("Фильтры успешно применены");
    }


    private boolean isRequestIdInFilteredResults(int requestId) {
        ObservableList<String> filteredItems = repairRequestListView.getItems();
        for (String item : filteredItems) {
            if (Integer.parseInt(item) == requestId) {
                return true;
            }
        }
        return false;
    }

    private void loadRepairRequests(Connection connection, String query) {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных

        try (PreparedStatement preparedStatement = connection.prepareStatement(query);
             ResultSet resultSet = preparedStatement.executeQuery()) {

            while (resultSet.next()) {
                // Добавляем элементы в ListView
                int requestId = resultSet.getInt("id");

                repairRequestListView.getItems().add(String.valueOf(requestId));
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

    @FXML
    public void onActionRefresh(ActionEvent event) {
        repairRequestListView.getItems().clear();
        loadRepairRequests(connection, query);
    }


    // TODO: можно упростить, т.к. по факту он может редактировать только комментарии
    public void onActionSave(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement updateRespAssignStatement = null;
        PreparedStatement updateAdditAssignStatement = null;

        try {
            connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);

            // Обновляем запись в таблице requests
            String updateQuery = "UPDATE requests " +
                    "SET equip_num = ?, equip_type = ?, problem_desc = ?, request_comments = ?, status = ? " +
                    "WHERE id = ?";
            preparedStatement1 = connection.prepareStatement(updateQuery);
            preparedStatement1.setString(1, equipSerialField.getText());
            preparedStatement1.setString(2, equipTypeField.getText());
            preparedStatement1.setString(3, descriptionTextArea.getText());
            preparedStatement1.setString(4, commentsTextArea.getText());
            preparedStatement1.setString(5, stateChoice.getValue());
            preparedStatement1.setInt(6, currentRequestNumber);
            preparedStatement1.executeUpdate();

            // Обновляем запись в таблице request_processes
            String updateProcessQuery = "UPDATE request_processes " +
                    "SET priority = ?, date_finish_plan = ? " +
                    "WHERE request_id = ?";
            preparedStatement2 = connection.prepareStatement(updateProcessQuery);
            preparedStatement2.setString(1, priorityChoice.getValue());
            preparedStatement2.setDate(2, Date.valueOf(finishDateTF.getText()));
            preparedStatement2.setInt(3, currentRequestNumber);
            preparedStatement2.executeUpdate();

            // Обновляем запись в таблице assignments для ответственного исполнителя
            String updateRespAssignQuery = "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                    "VALUES (?, (SELECT id FROM members WHERE name = ?), ?) " +
                    "ON CONFLICT (id_request, is_responsible) DO UPDATE " +
                    "SET member_id = EXCLUDED.member_id";
            updateRespAssignStatement = connection.prepareStatement(updateRespAssignQuery);
            updateRespAssignStatement.setInt(1, currentRequestNumber);
            updateRespAssignStatement.setString(2, responsibleRepairerChoice.getValue());
            updateRespAssignStatement.setBoolean(3, true);
            updateRespAssignStatement.executeUpdate();

            if (additionalRepairerChoice.getValue().equals("Нет")) {
                updateAdditAssignStatement = connection.prepareStatement(
                        "DELETE FROM assignments WHERE is_responsible = false AND id_request = " + currentRequestNumber);
                updateAdditAssignStatement.executeUpdate();
            } else {
                // Обновляем запись в таблице assignments для дополнительного исполнителя
                String updateAdditAssignQuery = "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                        "VALUES (?, (SELECT id FROM members WHERE name = ?), ?) " +
                        "ON CONFLICT (id_request, is_responsible) DO UPDATE " +
                        "SET member_id = EXCLUDED.member_id";
                updateAdditAssignStatement = connection.prepareStatement(updateAdditAssignQuery);
                updateAdditAssignStatement.setInt(1, currentRequestNumber);
                updateAdditAssignStatement.setString(2, additionalRepairerChoice.getValue());
                updateAdditAssignStatement.setBoolean(3, false);
                updateAdditAssignStatement.executeUpdate();
            }

            MyAlert.showInfoAlert("Информация по заявке обновлена успешно.");

            showMoreInfo(currentRequestNumber);

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при обновлении информации по заявке.");

        } finally {
            // Закрываем все ресурсы в блоке finally
            try {
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (updateRespAssignStatement != null) {
                    updateRespAssignStatement.close();
                }
                if (updateAdditAssignStatement != null) {
                    updateAdditAssignStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }


    public void onActionCreateOrCheckReport(ActionEvent actionEvent) {

        Connection connection;
        try {
            connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
            PreparedStatement preparedStatement =
                    connection.prepareStatement("SELECT * FROM reports WHERE request_id = " + currentRequestNumber);
            ResultSet resultSet = preparedStatement.executeQuery();
            if (resultSet.next()) {
                // TODO: логика открытия отчёта
                String report =
                        "Номер заявки: " + resultSet.getInt(1) + "\n\n" +
                                "Тип ремонта: " + resultSet.getString(2) + "\n\n" +
                                "Время выполнения, дней: " + resultSet.getString(3) + "\n\n" +
                                "Стоимость: " + resultSet.getDouble(4) + "\n\n" +
                                "Ресурсы: " + resultSet.getString(5) + "\n\n" +
                                "Причина неисправности: " + resultSet.getString(6) + "\n\n" +
                                "Оказанная помощь: " + resultSet.getString(7);
                MyAlert.showInfoAlert(report);
            } else {
                MyAlert.showInfoAlert("Отчёт для заявки №" + currentRequestNumber + " отсутствует");
            }
            preparedStatement.close();
            connection.close();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

    }

    public void showMoreInfo(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT r.id, r.equip_type, r.problem_desc, rr.client_name, rr.client_phone, " +
                    "r.equip_num, r.status, rp.priority, rr.date_start, rp.date_finish_plan, r.request_comments " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "JOIN request_processes rp ON r.id = rp.request_id " +
                    "WHERE r.id = ?";

            String assignRespQuery = "SELECT m.name AS responsible_repairer " +
                    "FROM assignments a " +
                    "JOIN members m ON a.member_id = m.id " +
                    "WHERE a.id_request = ? AND is_responsible = true";

            String assignAdditQuery = "SELECT m.name AS additional_repairer " +
                    "FROM assignments a " +
                    "JOIN members m ON a.member_id = m.id " +
                    "WHERE a.id_request = ? AND is_responsible = false";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, requestNumber);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        requestNumberLabel.setText("Заявка №" + resultSet.getString("id"));
                        equipTypeField.setText(resultSet.getString("equip_type"));
                        descriptionTextArea.setText(resultSet.getString("problem_desc"));
                        clientNameLabel.setText("ФИО: " + resultSet.getString("client_name"));
                        clientPhoneLabel.setText("Телефон: " + resultSet.getString("client_phone"));
                        equipSerialField.setText(resultSet.getString("equip_num"));
                        commentsTextArea.setText(resultSet.getString("request_comments"));
                        stateChoice.setValue(resultSet.getString("status"));
                        priorityChoice.setValue(resultSet.getString("priority"));

                        // Преобразуем даты из SQL Date в строковый формат для отображения
                        registerDateTF.setText(resultSet.getDate("date_start").toString());
                        finishDateTF.setText(resultSet.getDate("date_finish_plan").toString());

                        // Отображение кнопки для проверки отчета в зависимости от состояния
                        String currentState = resultSet.getString("status");

                        if (currentState.equals("Выполнено") || currentState.equals("Закрыта")) {
                            Connection connection1 = database.getConnection();
                            PreparedStatement statement =
                                    connection1.prepareStatement("SELECT * FROM reports WHERE request_id = " + currentRequestNumber);
                            ResultSet resultSet1 = statement.executeQuery();
                            if (resultSet1.next()) {
                                createOrCheckReportBtn.setVisible(true);
                            } else {
                                createOrCheckReportBtn.setVisible(false);
                            }
                        } else {
                            createOrCheckReportBtn.setVisible(false);
                        }


                        responsibleRepairerChoice.setValue("");
                        // Запрос для получения имени ответственного исполнителя
                        try (PreparedStatement responsibleRepairerStatement = connection.prepareStatement(assignRespQuery)) {
                            responsibleRepairerStatement.setInt(1, requestNumber);

                            try (ResultSet responsibleRepairerResult = responsibleRepairerStatement.executeQuery()) {
                                if (responsibleRepairerResult.next()) {
                                    String responsibleRepairer = responsibleRepairerResult.getString("responsible_repairer");
                                    responsibleRepairerChoice.setValue(responsibleRepairer);
                                }
                            }
                        }

                        additionalRepairerChoice.setValue("Нет");
                        // Запрос для получения имени доп исполнителя
                        try (PreparedStatement additionalRepairerStatement = connection.prepareStatement(assignAdditQuery)) {
                            additionalRepairerStatement.setInt(1, requestNumber);

                            try (ResultSet additionalRepairerResult = additionalRepairerStatement.executeQuery()) {
                                if (additionalRepairerResult.next()) {
                                    String additionalRepairer = additionalRepairerResult.getString("additional_repairer");
                                    additionalRepairerChoice.setValue(additionalRepairer);
                                }
                            }
                        }

                        moreInfoPane.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при загрузке дополнительной информации о заявке.");
        }
    }
}
