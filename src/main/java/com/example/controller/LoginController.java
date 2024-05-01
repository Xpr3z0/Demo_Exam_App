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

public class LoginController implements Initializable {
    public Button btnLogin;
    @FXML
    private TextField txtUsername;
    @FXML
    private PasswordField txtPassword;
    private String role;
    private int userID;
    private Database database;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        database = Database.getInstance();
    }


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
