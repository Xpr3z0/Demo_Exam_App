package com.example.controller;

import com.example.bdclient.Database;
import com.example.controller.dialogs.MyAlert;
import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.sql.*;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Button btnLogin;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    private String role;
    private int userID;
    @FXML
    private void btnLoginAction(ActionEvent event) {
        String login = txtUsername.getText();
        String password = txtPassword.getText();
        try {
            if (login.isEmpty() || password.isEmpty()) {
                throw new Exception("Укажите логин или пароль!");
            }
            Database database = Database.getInstance();
            if (database.accessToDB(Database.ROOT_LOGIN, Database.ROOT_PASS)) {
                try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
                    // SQL запрос для проверки логина и пароля
                    String sql = "SELECT * FROM members WHERE login = ? AND pass = ?";

                    // Создание подготовленного запроса
                    try (PreparedStatement statement = connection.prepareStatement(sql)) {
                        statement.setString(1, login);
                        statement.setString(2, password);
                        System.out.println(statement);

                        // Выполнение запроса
                        try (ResultSet resultSet = statement.executeQuery()) {
                            // Если есть результаты, извлекаем роль
                            if (resultSet.next()) {
                                userID = resultSet.getInt("id");
                                role = resultSet.getString("role");
                                System.out.println("Роль пользователя: " + role);
                                logIn();
                            } else {
                                System.out.println("Логин и/или пароль неверны");
                                MyAlert.showErrorAlert("Неверный логин или пароль");
                            }
                        }
                    }
                } catch (SQLException e) {
                    System.err.println("Ошибка подключения к базе данных: " + e.getMessage());
                }


            } else {
                new Alert(Alert.AlertType.ERROR, "Подключение не произошло.\nПроверьте логин или пароль.").showAndWait();
            }
        } catch (NullPointerException e) {
            new Alert(Alert.AlertType.ERROR, "Не найдена view MainView.fxml").showAndWait();
            e.printStackTrace();
            System.exit(-1);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            new Alert(Alert.AlertType.ERROR, e.getMessage()).showAndWait();
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }

    private void logIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            MainViewController mainViewController = new MainViewController(role, userID);
            loader.setController(mainViewController);

            Stage stage = new Stage();
            stage.setTitle("Demo Exam App");
            stage.setScene(new Scene(loader.load()));
            stage.show();

            Platform.runLater(() -> ((Stage) btnLogin.getScene().getWindow()).close());
            System.out.println("Авторизация успешна прошла!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
