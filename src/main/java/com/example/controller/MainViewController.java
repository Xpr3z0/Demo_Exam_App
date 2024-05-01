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
            addTab("Новая заявка",      "/view/operator_tabs/AddRequestTab.fxml");
            addTab("Готово к выдаче",   "/view/operator_tabs/FinishedRequestsTab.fxml");

        } else if (role.equals("manager")) {
            addTabWithController("Новые заявки",  "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager_new"));
            addTabWithController("Все заявки",    "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager"));
            addTab("Пользователи",  "/view/manager_tabs/UsersTab.fxml");
            addTab("Статистика",    "/view/manager_tabs/StatisticsTab.fxml");

        } else if (role.equals("repairer")) {
            addTabWithController("Ответственные заявки",  "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("resp_repairer"));
            addTabWithController("Обычные заявки",        "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("addit_repairer"));
            addTab("Заказ запчастей",       "/view/repairer_tabs/OrdersTab.fxml");
        }
    }

    /**
     * Метод для добавления вкладок на главное окно (TabPane)
     * @param title Надпись на вкладке
     * @param pathToFXML Путь к файлу-макету вкладки
     * **/
    private void addTab(String title, String pathToFXML) {
        Tab newRequestTab = new Tab(title);
        try {
            newRequestTab.setStyle("-fx-font-family: Arial; -fx-font-size: 14;");
            if (!pathToFXML.equals("")) {
                newRequestTab.setContent(new FXMLLoader(getClass().getResource(pathToFXML)).load());
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.getTabs().add(newRequestTab);
    }

    private void addTabWithController(String title, String pathToFXML, Object controller) {
        Tab newRequestTab = new Tab(title);
        try {
            newRequestTab.setStyle("-fx-font-family: Arial; -fx-font-size: 14;");
            if (!pathToFXML.equals("")) {
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
                loader.setController(controller); // Установка контроллера
                newRequestTab.setContent(loader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
        mainPane.getTabs().add(newRequestTab);
    }

}
