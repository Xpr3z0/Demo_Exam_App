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
    public BorderPane moreInfoPane;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;
    public DatePicker finishDatePicker;
    public Button refreshListBtn;
    private Database database;
    private int currentRequestNumber = -1;
    private LinkedHashMap<Integer, Request> requestMap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
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
        repairRequestListView.getItems().clear();
        requestMap.clear();

        ArrayList<String> idList = database.stringListQuery(
                "id", "requests", "status = 'Новая'", "id");

        for (String idStr : idList) {
            int id = Integer.parseInt(idStr);
            requestMap.put(id, new Request(id));
            repairRequestListView.getItems().add(idStr);
        }
    }

    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }


    public void onActionRegister() {
        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipSerialField.getText(),
                equipTypeField.getText(),
                descriptionTextArea.getText(),
                commentsTextArea.getText(),
                "В работе",
                priorityChoice.getValue(),
                finishDatePicker.getValue().toString(),
                responsibleRepairerChoice.getValue(),
                additionalRepairerChoice.getValue());
        moreInfoPane.setVisible(false);
        loadRepairRequests();
    }



    @FXML
    public void onActionDelete() {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            int selectedItemId = Integer.parseInt(repairRequestListView.getItems().get(selectedIndex));

            boolean deletedSuccessfully = requestMap.get(selectedItemId).deleteRequestInDB();

            if (deletedSuccessfully) {
                MyAlert.showInfoAlert("Заявка успешно удалена.");
                moreInfoPane.setVisible(false);
                loadRepairRequests();
            } else {
                MyAlert.showErrorAlert("Не удалось удалить заявку. Возможно, такая заявка не существует.");
            }

        } else {
            MyAlert.showErrorAlert("Выберите заявку для удаления.");
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

        responsibleRepairerChoice.setValue(null);
        additionalRepairerChoice.setValue("Нет");
        priorityChoice.setValue(null);
        finishDatePicker.setValue(null);

        moreInfoPane.setVisible(true);
    }
}
