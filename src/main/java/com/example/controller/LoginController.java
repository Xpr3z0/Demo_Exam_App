package com.example.controller;

import com.example.bdclient.ClientPostgreSQL;
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
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    public Button btnLogin;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    private final boolean autoLogin = true;
    private String role;
    @FXML
    private void btnLoginAction(ActionEvent event) {
        String login = txtUsername.getText();
        String password = txtPassword.getText();
        try {
            if (login.isEmpty() || password.isEmpty()) {
                throw new Exception("Укажите логин или пароль!");
            }
            ClientPostgreSQL jdbcClient = ClientPostgreSQL.getInstance();
            System.out.println(jdbcClient.getLogin());
            if (jdbcClient.accessToDB(login, password)) {
//                role = "operator";
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
                MainViewController mainViewController = new MainViewController(role);
                loader.setController(mainViewController);
                Stage stage = new Stage();
                stage.setTitle("Demo Exam App");
                stage.setScene(new Scene(loader.load()));
                stage.show();
                ((Stage) btnLogin.getScene().getWindow()).close();
                System.out.println("Авторизация успешна прошла!");
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
        role = "operator";
        autoLogin();
    }

    private void autoLogin() {
        if (autoLogin == true) {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/MainView.fxml"));
            MainViewController mainViewController = new MainViewController(role);
            loader.setController(mainViewController);
            Stage stage = new Stage();
            stage.setTitle("Demo Exam App");
            try {
                stage.setScene(new Scene(loader.load()));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
            stage.show();
            Platform.runLater(() -> ((Stage) btnLogin.getScene().getWindow()).close());
        }
    }
}
