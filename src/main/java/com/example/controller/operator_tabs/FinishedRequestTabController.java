package com.example.controller.operator_tabs;

import com.example.bdclient.DB;
import com.example.controller.ListItemController;
import com.example.controller.dialogs.MyAlert;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import javafx.event.ActionEvent;
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
import java.sql.*;
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
    public ScrollPane moreInfoScrollPane;
    public BorderPane moreInfoBorderPane;
    public Button refreshListBtn;
    private DB db;
    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";
    private int currentRequestNumber = -1;

    private final String QR_CODE_URL = "https://www.youtube.com/watch?v=dQw4w9WgXcQ";
    public ImageView qrImageView;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        moreInfoBorderPane.setVisible(false);
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
        // Здесь вы должны использовать JDBC для подключения к вашей БД
        // и получения данных о заявках на ремонт

        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT id FROM requests WHERE status = 'Выполнено' ORDER BY id";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Добавляем элементы в ListView
                        String requestNumber = resultSet.getString("id");

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


    public void onActionCloseRequest(ActionEvent actionEvent) {
        int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
        if (selectedIndex != -1) {
            db = DB.getInstance();
            Connection connection = null;

            try {
                connection = db.getConnection();
//                connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD);
                PreparedStatement preparedStatement = connection.prepareStatement(
                        "UPDATE requests SET status = 'Закрыта' WHERE id = " + currentRequestNumber);
                preparedStatement.executeUpdate();

                db.closeConnection();

                MyAlert.showInfoAlert("Заявка успешно закрыта.");
                moreInfoBorderPane.setVisible(false);
                repairRequestListView.getItems().remove(repairRequestListView.getSelectionModel().getSelectedIndex());

            } catch (SQLException | NumberFormatException e) {
                e.printStackTrace();
                MyAlert.showErrorAlert("Ошибка при закрытии заявки.");
            } finally {
                try {
                    db.closeConnection();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private void showMoreInfo(int requestNumber) {
        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT r.id, r.equip_type, r.problem_desc, rr.client_name, rr.client_phone, " +
                    "r.equip_num, r.request_comments " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "WHERE r.id = ?";

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

    private Image generateQRCode(String content) {
        try {
            // Используем MultiFormatWriter из ZXing для генерации QR-кода
            BitMatrix bitMatrix = new MultiFormatWriter()
                    .encode(content, BarcodeFormat.QR_CODE, 300, 300);

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
