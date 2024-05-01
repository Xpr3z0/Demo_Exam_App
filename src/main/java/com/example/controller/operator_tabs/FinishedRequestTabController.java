package com.example.controller.operator_tabs;


import com.example.util.Request;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

public class FinishedRequestTabController implements Initializable {

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
    public Button refreshListBtn;
    private Database database;
    private int currentRequestNumber = -1;

    private final String QR_CODE_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    public ImageView qrImageView;
    private LinkedHashMap<Integer, Request> requestMap;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        moreInfoPane.setVisible(false);
        qrImageView.setImage(generateQRCode(QR_CODE_URL));
        loadRepairRequests();

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
                "id", "requests", "status = 'Выполнено'", "id");

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


    public void onActionCloseRequest() {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            database.updateQuery("requests", "status = 'Закрыта'", "id = " + currentRequestNumber);
            MyAlert.showInfoAlert("Заявка успешно закрыта.");
            loadRepairRequests();
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

        moreInfoPane.setVisible(true);
    }



    private Image generateQRCode(String url) {
        try {
            // Используем MultiFormatWriter из ZXing для генерации QR-кода
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    url, BarcodeFormat.QR_CODE, 300, 300);

            // Преобразование BitMatrix в Image
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return new Image(new ByteArrayInputStream(out.toByteArray()));

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

}
