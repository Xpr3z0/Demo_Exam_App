package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainViewController implements Initializable {
    public static String role;
    public static int userID;
    @FXML
    public TabPane mainPane;


    public MainViewController(String role, int userID) {
        MainViewController.role = role;
        MainViewController.userID = userID;
    }


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.getTabs().clear();

        if (role.equals("operator")) {
            addTab("Новая заявка",      "/view/operator_tabs/AddRequestTab.fxml", null);
            addTab("Готово к выдаче",   "/view/operator_tabs/FinishedRequestsTab.fxml", null);

        } else if (role.equals("manager")) {
            addTab("Новые заявки",  "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager_new"));
            addTab("Все заявки",    "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager"));
            addTab("Пользователи",  "/view/UniversalTableTab.fxml", new UniversalTableTabController("members"));
            addTab("Статистика",    "/view/manager_tabs/StatisticsTab.fxml", null);

        } else if (role.equals("repairer")) {
            addTab("Ответственные заявки",  "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("resp_repairer"));
            addTab("Обычные заявки",        "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("addit_repairer"));
            addTab("Заказ запчастей",       "/view/UniversalTableTab.fxml", new UniversalTableTabController("orders"));
        }
    }



    private void addTab(String title, String pathToFXML, Object controller) {
        Tab newRequestTab = new Tab(title);
        try {
            newRequestTab.setStyle("-fx-font-family: Arial; -fx-font-size: 14;");
            if (!pathToFXML.equals("")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
                if (controller != null) {
                    loader.setController(controller); // Установка контроллера
                }
                newRequestTab.setContent(loader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
        mainPane.getTabs().add(newRequestTab);
    }
}
