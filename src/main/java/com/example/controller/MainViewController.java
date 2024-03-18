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
    private String role;
    @FXML
    public TabPane mainPane;
    public MainViewController(String role) {
        this.role = role;
    }
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.getTabs().clear();

        if (role.equals("operator")) {
            addTab("Новая заявка", "/view/operator_tabs/AddRequestTab.fxml");
            addTab("Готово к выдаче", "/view/operator_tabs/FinishedRequestsTab.fxml");

        } else if (role.equals("manager")) {
            addTab("Новые заявки", "");
            addTab("Все заявки", "");
            addTab("Пользователи", "");
            addTab("Статистика", "");
            addTab("Настройки", "");

        } else if (role.equals("repairer")) {
            addTab("Текущие заявки", "");
            addTab("Выполненные заявки", "");
            addTab("Заказ запчастей", "");
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
            newRequestTab.setContent(new FXMLLoader(getClass().getResource(pathToFXML)).load());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        mainPane.getTabs().add(newRequestTab);
    }
}
