package com.example.controller;

import com.example.util.Database;
import com.example.util.MyAlert;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

/**
 * Контроллер для управления логикой аутентификации пользователей.
 * Включает методы для обработки ввода имени пользователя и пароля и выполнения входа.
 */
public class LoginController implements Initializable {

    @FXML
    private Button btnLogin;

    @FXML
    private TextField txtUsername;

    @FXML
    private PasswordField txtPassword;

    // Роль пользователя после входа
    private String role;

    // ID пользователя после входа
    private int userID;

    // Ссылка на объект класса Database для взаимодействия с базой данных
    private Database database;

    /**
     * Инициализирует контроллер и устанавливает ссылку на объект Database.
     *
     * @param location URL для загрузки ресурсов (не используется)
     * @param resources Набор ресурсов для локализации (не используется)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
    }

    /**
     * Обрабатывает действие при нажатии на кнопку входа.
     * Выполняет проверку введенных имени пользователя и пароля, сравнивая с записями в базе данных.
     * Если учетные данные корректны, переходит на главное окно приложения. В противном случае выводит сообщение об ошибке.
     */
    @FXML
    private void btnLoginAction() {
        String login = txtUsername.getText();
        String password = txtPassword.getText();

        String sql = String.format("SELECT * FROM members WHERE login = '%s' AND pass = '%s'", login, password);
        ArrayList<String> result = database.executeQueryAndGetColumnValues(sql);

        if (result != null && !result.isEmpty()) {
            userID = Integer.parseInt(result.get(0));
            role = result.get(result.size() - 1);
            logIn();
        } else {
            MyAlert.showErrorAlert("Неверный логин или пароль");
        }
    }

    /**
     * Переходит на главное окно приложения, передавая в него информацию о роли и ID пользователя.
     * Закрывает окно входа после успешной аутентификации.
     */
    private void logIn() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            MainViewController mainViewController = new MainViewController(role, userID);
            loader.setController(mainViewController);

            Stage stage = new Stage();
            stage.setTitle("Demo Exam App");
            stage.setScene(new Scene(loader.load()));
            stage.show();

            // Закрытие окна входа
            Platform.runLater(() -> ((Stage) btnLogin.getScene().getWindow()).close());
            System.out.println("Авторизация успешна прошла!");

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
