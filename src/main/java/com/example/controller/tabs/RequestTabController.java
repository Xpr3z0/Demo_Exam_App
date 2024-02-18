package com.example.controller.tabs;

import com.example.controller.ListItemController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.layout.AnchorPane;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class RequestTabController implements Initializable {

    @FXML
    private ListView<String> repairRequestListView;
    private final String DB_URL = "jdbc:postgresql://localhost:8888/postgres";
    private final String LOGIN = "postgres";
    private final String PASSWORD = "root";

    private void loadRepairRequests() {
        // Здесь вы должны использовать JDBC для подключения к вашей БД
        // и получения данных о заявках на ремонт

        try (Connection connection = DriverManager.getConnection(DB_URL, LOGIN, PASSWORD)) {
            String query = "SELECT request_number, description, client_name FROM repair_requests";
            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    while (resultSet.next()) {
                        // Добавляем элементы в ListView
                        String requestNumber = resultSet.getString("request_number");
//                        String description = resultSet.getString("description");
//                        String clientName = resultSet.getString("client_name");

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
//                                            controller.updateFromDatabase(item);  // Вызываем метод обновления из базы данных
                                            setGraphic(root);
                                        } catch (IOException e) {
                                            e.printStackTrace();
                                            setGraphic(null);
                                        }
                                    }
                                }
                            };
                        });

//                        repairRequestListView.setCellFactory(param -> {
//                            try {
//                                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListItem.fxml"));
//                                Parent root = loader.load();
//                                ListItemController controller = loader.getController();
////                                controller.setRequestNumber(requestNumber);
////                                controller.setDescription(description);
////                                controller.setClientName(clientName);
//                                return new ListCell<String>() {
//                                    @Override
//                                    protected void updateItem(String item, boolean empty) {
//                                        super.updateItem(item, empty);
//
//                                        if (item == null || empty) {
//                                            setGraphic(null);
//                                        } else {
//                                            controller.setRequestNumber(item);
//                                            controller.setDescription(description);
//                                            controller.setClientName(clientName);
//                                            // Дополнительно настройте другие поля с использованием данных из БД, если необходимо
//                                            setGraphic(root);
//                                        }
//                                    }
//                                };
//                            } catch (IOException e) {
//                                e.printStackTrace();
//                                return null;
//                            }
//                        });


                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadRepairRequests();
    }
}
