package com.example.controller;

import com.example.Request;
import com.example.bdclient.Database;
import com.example.controller.dialogs.MyAlert;
import com.example.controller.dialogs.UniversalFormDialog;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class UniversalRequestsTabController implements Initializable {
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
    public VBox infoVBox;
    public VBox stateVBox;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;
    public TextField registerDateTF;
    public TextField finishDateTF;

    public Button refreshListBtn;

    public Button createOrCheckReportBtn;

    private Database database;
    private int currentRequestNumber = -1;
    private String role;


    private LinkedHashMap<Integer, Request> requestMap;
    private boolean filterApplied = false;

    public UniversalRequestsTabController(String role) {
        this.role = role;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
        createOrCheckReportBtn.setVisible(false);

        setUnavailableFields();
        loadRepairRequests();

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

    private void setUnavailableFields() {
        if (role.equals("resp_repairer")) {
            responsibleRepairerChoice.setDisable(true);
        } else if (role.equals("addit_repairer")) {
            stateChoice.setDisable(true);
            priorityChoice.setDisable(true);
            responsibleRepairerChoice.setDisable(true);
            additionalRepairerChoice.setDisable(true);
            finishDateTF.setEditable(false);
        } else if (role.equals("manager_new")) {
            infoVBox.getChildren().remove(stateVBox);
        }
    }

    @FXML
    public void applyFilters() {
        if (!idFilterTF.getText().trim().equals("")) {
            int requestId = Integer.parseInt(idFilterTF.getText());

            if (requestMap.containsKey(requestId)) {
                repairRequestListView.getItems().clear();
                repairRequestListView.getItems().add(String.valueOf(requestId));
                filterApplied = true;
                showMoreInfo(requestId);

            } else {
                filterApplied = false;
                moreInfoPane.setVisible(false);
                clearFilters();
                MyAlert.showErrorAlert(String.format("Заявка с номером %d не найдена", requestId));
            }
        } else {
            filterApplied = false;
        }
    }



    public void loadRepairRequests() {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных
        requestMap.clear();

        String query = getQueryForRole();
        if (query != null) {
            ArrayList<String> idList = database.stringListQuery("id", query);

            for (String idStr : idList) {
                int id = Integer.parseInt(idStr);
                requestMap.put(id, new Request(id));
                repairRequestListView.getItems().add(idStr);
            }

            if (filterApplied) {
                applyFilters();
            }

        } else {
            MyAlert.showErrorAlert("Ошибка: роль " + role + " не найдена");
        }
    }

    private String getQueryForRole() {
        if (role.equals("manager")) {
            return "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";

        } else if (role.equals("manager_new")) {
            return "SELECT id FROM requests WHERE status = 'Новая' ORDER BY id";

        } else if (role.equals("resp_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = true " +
                    "ORDER BY r.id";

        } else if (role.equals("addit_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = false " +
                    "ORDER BY r.id";
        } else {
            return null;
        }
    }


    @FXML
    public void clearFilters() {
        idFilterTF.clear();
        loadRepairRequests();
    }

    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }


    public void onActionSave() {

        String stateValue;
        if (role.equals("manager_new")) {
            stateValue = "В работе";
        } else {
            stateValue = stateChoice.getValue();
        }

        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipSerialField.getText(),
                equipTypeField.getText(),
                descriptionTextArea.getText(),
                commentsTextArea.getText(),
                stateValue,
                priorityChoice.getValue(),
                finishDateTF.getText(),
                responsibleRepairerChoice.getValue(),
                additionalRepairerChoice.getValue());

        if (role.equals("manager_new")) {
            moreInfoPane.setVisible(false);
            loadRepairRequests();
        }
    }


    public void onActionCreateOrCheckReport() {

        if (createOrCheckReportBtn.getText().equals("Посмотреть отчёт")) {

            ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                    "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

            if (reportValues != null && reportValues.size() > 0) {
                String report =
                        "Номер заявки: " + reportValues.get(0) + "\n\n" +
                                "Тип ремонта: " + reportValues.get(1) + "\n\n" +
                                "Время выполнения, дней: " + reportValues.get(2) + "\n\n" +
                                "Стоимость: " + reportValues.get(3) + "\n\n" +
                                "Ресурсы: " + reportValues.get(4) + "\n\n" +
                                "Причина неисправности: " + reportValues.get(5) + "\n\n" +
                                "Оказанная помощь: " + reportValues.get(6);
                MyAlert.showInfoAlert(report);
            } else {
                MyAlert.showInfoAlert("Отчёт для заявки №" + currentRequestNumber + " отсутствует");
            }

        } else {
            new UniversalFormDialog("reports", database.getAllTableColumnNames("reports"));
            showMoreInfo(currentRequestNumber);
        }

    }

    public void showMoreInfo(int requestId) {
        Request request = requestMap.get(requestId);

        requestNumberLabel.setText("Заявка №" + requestId);
        equipTypeField.setText(request.getEquip_type());
        descriptionTextArea.setText(request.getProblem_desc());
        clientNameLabel.setText("ФИО: " + request.getClient_name());
        clientPhoneLabel.setText("Телефон: " + request.getClient_phone());
        equipSerialField.setText(request.getEquip_num());
        commentsTextArea.setText(request.getRequest_comments());
        registerDateTF.setText(request.getDate_start());

        if (role.equals("manager_new")) {
            finishDateTF.setText("");
            priorityChoice.setValue(null);
            responsibleRepairerChoice.setValue(null);
            additionalRepairerChoice.setValue("Нет");
        } else {
            stateChoice.setValue(request.getStatus());
            finishDateTF.setText(request.getDate_finish_plan());
            priorityChoice.setValue(request.getPriority());
            responsibleRepairerChoice.setValue(request.getResponsible_repairer_name());
            additionalRepairerChoice.setValue(request.getAdditional_repairer_name());
        }

        // Отображение кнопки для проверки отчета в зависимости от состояния
        String currentState = request.getStatus();
        if (currentState.equals("Выполнено") || currentState.equals("Закрыта")) {

            if (role.equals("resp_repairer")) {
                ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                        "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

                if (reportValues != null && reportValues.size() > 0) {
                    createOrCheckReportBtn.setText("Посмотреть отчёт");
                } else {
                    createOrCheckReportBtn.setText("Создать отчёт");
                }
            }

            createOrCheckReportBtn.setVisible(true);
        } else {
            createOrCheckReportBtn.setVisible(false);
        }

        moreInfoPane.setVisible(true);
    }
}
