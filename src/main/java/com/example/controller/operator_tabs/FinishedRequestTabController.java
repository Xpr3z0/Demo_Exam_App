package com.example.controller.operator_tabs;

import com.example.controller.ListItemController;
import com.example.util.Request;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Контроллер вкладки "Готово к выдаче" для оператора.
 * Управляет отображением и обработкой завершенных заявок, включая QR-код.
 */
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

    // Дополнительная информация об активной заявке
    public BorderPane moreInfoPane;

    // Кнопки для обновления и закрытия заявки
    public Button refreshListBtn;

    // Отображение QR-кода
    public ImageView qrImageView;

    // Объект базы данных для выполнения запросов
    private Database database;

    // Текущий выбранный номер заявки
    private int currentRequestNumber = -1;

    // URL, закодированный в QR-коде
    private final String QR_CODE_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";

    // Карта завершенных заявок
    private LinkedHashMap<Integer, Request> requestMap;

    /**
     * Метод инициализации контроллера.
     * @param location URL местоположения
     * @param resources Ресурсный пакет для локализации
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        // По умолчанию панель с информацией скрыта
        moreInfoPane.setVisible(false);

        // Загрузка QR-кода
        qrImageView.setImage(generateQRCode(QR_CODE_URL));

        // Загрузка списка завершенных заявок
        loadRepairRequests();

        // Обработка клика на элементе списка заявок
        repairRequestListView.setOnMouseClicked(event -> {
            int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = repairRequestListView.getItems().get(selectedIndex);
                currentRequestNumber = Integer.parseInt(selectedItem);
                showMoreInfo(currentRequestNumber);
            }
        });
    }

    /**
     * Загрузка списка завершенных заявок из базы данных.
     * Отображает заявки в ListView с использованием настраиваемого формата.
     */
    private void loadRepairRequests() {
        repairRequestListView.getItems().clear();
        requestMap.clear();

        // Получаем список завершенных заявок
        ArrayList<String> idList = database.stringListQuery(
                "id", "requests", "status = 'Выполнено'", "id");

        // Заполняем отображаемый список и карту заявок
        for (String idStr : idList) {
            int id = Integer.parseInt(idStr);
            requestMap.put(id, new Request(id));
            repairRequestListView.getItems().add(idStr);
        }

        // Настраиваем отображение списка
        repairRequestListView.setCellFactory(param -> new ListCell<String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                if (item == null || empty) {
                    setGraphic(null);
                } else {
                    try {
                        FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListItem.fxml"));
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
        });
    }

    /**
     * Обработчик действия обновления списка заявок.
     * Вызывается при нажатии кнопки обновления.
     */
    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }

    /**
     * Обработчик закрытия выбранной заявки.
     * Обновляет статус заявки в базе данных и обновляет список.
     */
    public void onActionCloseRequest() {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            database.updateQuery("requests", "status = 'Закрыта'", "id = " + currentRequestNumber);
            MyAlert.showInfoAlert("Заявка успешно закрыта.");
            loadRepairRequests();
        }
    }

    /**
     * Отображение подробной информации о выбранной заявке.
     * @param requestId Идентификатор заявки для отображения
     */
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

    /**
     * Генерирует изображение QR-кода из заданного URL.
     * @param url URL, который будет закодирован в QR-код
     * @return Изображение QR-кода или null при ошибке
     */
    private Image generateQRCode(String url) {
        try {
            // Создаем BitMatrix для QR-кода
            BitMatrix bitMatrix = new MultiFormatWriter().encode(
                    url, BarcodeFormat.QR_CODE, 300, 300);

            // Конвертируем BitMatrix в изображение
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            MatrixToImageWriter.writeToStream(bitMatrix, "PNG", out);

            return new Image(new ByteArrayInputStream(out.toByteArray()));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
