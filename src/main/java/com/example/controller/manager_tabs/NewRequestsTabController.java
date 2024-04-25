package com.example.controller.manager_tabs;

import com.example.bdclient.Database;
import com.example.controller.ListItemController;
import com.example.controller.dialogs.MyAlert;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ArrayList;
import java.util.ResourceBundle;

public class NewRequestsTabController implements Initializable {

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
    public BorderPane moreInfoBorderPane;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;
    public DatePicker finishDatePicker;
    public Button refreshListBtn;
    private Database database;
    private Connection connection = null;
    private int currentRequestNumber = -1;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moreInfoBorderPane.setVisible(false);
        database = Database.getInstance();
        loadRepairRequests();
        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");

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
                showMoreInfo(currentRequestNumber);
            }
        });
    }


    private void loadRepairRequests() {
        // Здесь вы должны использовать JDBC для подключения к вашей БД
        // и получения данных о заявках на ремонт
        repairRequestListView.getItems().clear();

        ArrayList<String> idList = database.stringListQuery(
                "id", "requests", "status = 'Новая'", "id");
        repairRequestListView.getItems().addAll(idList);
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

    @FXML
    public void onActionRefresh(ActionEvent event) {
        repairRequestListView.getItems().clear();
        loadRepairRequests();
    }


    public void onActionRegister(ActionEvent event) {
        Connection connection = null;
        PreparedStatement preparedStatement1 = null;
        PreparedStatement preparedStatement2 = null;
        PreparedStatement respAssignPrepStatement = null;
        PreparedStatement additAssignPrepStatement = null;

        try {
            connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);

            // Обновление записи в таблице requests
            String updateQuery = "UPDATE requests SET equip_num = ?, equip_type = ?, problem_desc = ?, " +
                    "request_comments = ?, status = ? WHERE id = ?";
            preparedStatement1 = connection.prepareStatement(updateQuery);
            preparedStatement1.setString(1, equipSerialField.getText());
            preparedStatement1.setString(2, equipTypeField.getText());
            preparedStatement1.setString(3, descriptionTextArea.getText());
            preparedStatement1.setString(4, commentsTextArea.getText());
            preparedStatement1.setString(5, "В работе");
            preparedStatement1.setInt(6, currentRequestNumber);
            preparedStatement1.executeUpdate();

            // Вставка новой записи в таблицу request_processes
            String insertQuery = "INSERT INTO request_processes (request_id, priority, date_finish_plan) VALUES (?, ?, ?)";
            preparedStatement2 = connection.prepareStatement(insertQuery);
            preparedStatement2.setInt(1, currentRequestNumber); // Устанавливаем request_id для вставки
            preparedStatement2.setString(2, priorityChoice.getValue()); // Устанавливаем priority для вставки
            preparedStatement2.setDate(3, Date.valueOf(finishDatePicker.getValue())); // Устанавливаем date_finish_plan для вставки
            preparedStatement2.executeUpdate();

            String insertRespAssignmentsQuery = "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                    "VALUES (?, (SELECT id FROM members WHERE name = ?), ?)";
            respAssignPrepStatement = connection.prepareStatement(insertRespAssignmentsQuery);
            respAssignPrepStatement.setInt(1, currentRequestNumber);
            respAssignPrepStatement.setString(2, responsibleRepairerChoice.getValue());
            respAssignPrepStatement.setBoolean(3, true);
            respAssignPrepStatement.executeUpdate();

            if (!additionalRepairerChoice.getValue().equals("Нет")) {
                String insertAdditAssignmentsQuery = "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                        "VALUES (?, (SELECT id FROM members WHERE name = ?), ?)";
                additAssignPrepStatement = connection.prepareStatement(insertAdditAssignmentsQuery);
                additAssignPrepStatement.setInt(1, currentRequestNumber);
                additAssignPrepStatement.setString(2, additionalRepairerChoice.getValue());
                additAssignPrepStatement.setBoolean(3, false);
                additAssignPrepStatement.executeUpdate();
            }

            MyAlert.showInfoAlert("Заявка успешно зарегистрирована.");
            moreInfoBorderPane.setVisible(false);
            repairRequestListView.getItems().remove(repairRequestListView.getSelectionModel().getSelectedIndex());

        } catch (SQLException | NumberFormatException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при регистрации заявки.");

        } finally {
            // Закрываем все ресурсы в блоке finally
            try {
                if (preparedStatement1 != null) {
                    preparedStatement1.close();
                }
                if (preparedStatement2 != null) {
                    preparedStatement2.close();
                }
                if (respAssignPrepStatement != null) {
                    respAssignPrepStatement.close();
                }
                if (additAssignPrepStatement != null) {
                    additAssignPrepStatement.close();
                }
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    @FXML
    public void onActionDelete(ActionEvent actionEvent) {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            String selectedItemId = repairRequestListView.getItems().get(selectedIndex);

            database.deleteRowTable("assignments", "id_request", selectedItemId);
            database.deleteRowTable("request_processes", "request_id", selectedItemId);
            database.deleteRowTable("request_regs", "request_id", selectedItemId);
            boolean deletedSuccessfully = database.deleteRowTable("requests", "id", selectedItemId);

            // TODO: мб проверку условий доделать
            if (deletedSuccessfully) {
                MyAlert.showInfoAlert("Заявка успешно удалена.");
                repairRequestListView.getItems().remove(Integer.parseInt(selectedItemId));
                moreInfoBorderPane.setVisible(false);
            } else {
                MyAlert.showErrorAlert("Не удалось удалить заявку. Возможно, такая заявка не существует.");
            }

        } else {
            MyAlert.showErrorAlert("Выберите заявку для удаления.");
        }
    }


    private void showMoreInfo(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            String query = "SELECT r.id, r.equip_type, r.problem_desc, rr.client_name, rr.client_phone, " +
                    "r.equip_num, r.request_comments " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "WHERE r.id = ?";

            responsibleRepairerChoice.setValue(null);
            additionalRepairerChoice.setValue("Нет");
            priorityChoice.setValue(null);
            finishDatePicker.setValue(null);
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

                        moreInfoBorderPane.setVisible(true);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при загрузке дополнительной информации о заявке.");
        }
    }
}
