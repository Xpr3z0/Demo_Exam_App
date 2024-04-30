package com.example.controller.manager_tabs;

import com.example.Request;
import com.example.bdclient.Database;
import com.example.controller.dialogs.MyAlert;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;

import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class AllRequestsTabController implements Initializable {
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
    public BorderPane moreInfoPane;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;
    public TextField registerDateTF;
    public TextField finishDateTF;
    public Button checkReportBtn;
    private Database database;

    private int currentRequestNumber = -1;
    private LinkedHashMap<Integer, Request> requestMap;
    private boolean filterApplied = false;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
        checkReportBtn.setVisible(false);

        loadRepairRequests();

        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");
        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании", "Закрыта");

        ArrayList<String> repairersList = database.stringListQuery(
                "name", "members", "role = 'repairer'", "name");
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


    @FXML
    public void clearFilters() {
        idFilterTF.clear();
    }


    public void loadRepairRequests() {
        repairRequestListView.getItems().clear(); // Очищаем ListView перед загрузкой новых данных
        requestMap.clear();

        String query = "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";

        ArrayList<String> idList = database.stringListQuery("id", query);

        for (String idStr : idList) {
            int id = Integer.parseInt(idStr);
            requestMap.put(id, new Request(id));
            repairRequestListView.getItems().add(idStr);
        }

        if (filterApplied) {
            applyFilters();
        }
    }


    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }


    public void onActionSave() {
        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipSerialField.getText(),
                equipTypeField.getText(),
                descriptionTextArea.getText(),
                commentsTextArea.getText(),
                stateChoice.getValue(),
                priorityChoice.getValue(),
                finishDateTF.getText(),
                responsibleRepairerChoice.getValue(),
                additionalRepairerChoice.getValue());
    }


    public void onActionCheckReport() {
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
    }

    private void showMoreInfo(int requestId) {
        Request request = requestMap.get(requestId);

        requestNumberLabel.setText("Заявка №" + requestId);
        equipTypeField.setText(request.getEquip_type());
        descriptionTextArea.setText(request.getProblem_desc());
        clientNameLabel.setText("ФИО: " + request.getClient_name());
        clientPhoneLabel.setText("Телефон: " + request.getClient_phone());
        equipSerialField.setText(request.getEquip_num());
        commentsTextArea.setText(request.getRequest_comments());
        stateChoice.setValue(request.getStatus());
        priorityChoice.setValue(request.getPriority());

        // Преобразуем даты из SQL Date в строковый формат для отображения
        registerDateTF.setText(request.getDate_start());
        finishDateTF.setText(request.getDate_finish_plan());

        // Отображение кнопки для проверки отчета в зависимости от состояния
        String currentState = request.getStatus();

        if (currentState.equals("Выполнено") || currentState.equals("Закрыта")) {
            checkReportBtn.setVisible(true);
        } else {
            checkReportBtn.setVisible(false);
        }

        responsibleRepairerChoice.setValue(request.getResponsible_repairer_name());
        additionalRepairerChoice.setValue(request.getAdditional_repairer_name());


        moreInfoPane.setVisible(true);
    }
}
