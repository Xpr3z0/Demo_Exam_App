package com.example.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

/**
 * Главный контроллер для управления представлением основного окна приложения.
 * Добавляет вкладки в зависимости от роли пользователя.
 */
public class MainViewController implements Initializable {
    // Роль текущего пользователя
    public static String role;

    // ID текущего пользователя
    public static int userID;

    // Основной элемент TabPane, содержащий все вкладки
    @FXML
    public TabPane mainPane;

    /**
     * Конструктор, устанавливающий роль и ID пользователя.
     *
     * @param role  Роль текущего пользователя
     * @param userID  ID текущего пользователя
     */
    public MainViewController(String role, int userID) {
        MainViewController.role = role;
        MainViewController.userID = userID;
    }

    /**
     * Инициализирует контроллер и добавляет вкладки, основываясь на роли пользователя.
     *
     * @param location URL для загрузки ресурсов (не используется)
     * @param resources Набор ресурсов для локализации (не используется)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Очистка текущих вкладок
        mainPane.getTabs().clear();

        // Добавление вкладок в зависимости от роли пользователя
        if (role.equals("operator")) {
            addTab("Новая заявка", "/view/operator_tabs/AddRequestTab.fxml", null);
            addTab("Готово к выдаче", "/view/operator_tabs/FinishedRequestsTab.fxml", null);

        } else if (role.equals("manager")) {
            addTab("Новые заявки", "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager_new"));
            addTab("Принятые заявки", "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("manager"));
            addTab("Пользователи", "/view/UniversalTableTab.fxml", new UniversalTableTabController("members"));
            addTab("Статистика", "/view/manager_tabs/StatisticsTab.fxml", null);

        } else if (role.equals("repairer")) {
            addTab("Ответственные заявки", "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("resp_repairer"));
            addTab("Обычные заявки", "/view/UniversalRequestsTab.fxml", new UniversalRequestsTabController("addit_repairer"));
            addTab("Заказ запчастей", "/view/UniversalTableTab.fxml", new UniversalTableTabController("orders"));
        }
    }

    /**
     * Добавляет вкладку в главное окно.
     *
     * @param title  Название вкладки
     * @param pathToFXML  Путь к FXML-файлу
     * @param controller  Контроллер для привязки к FXML-файлу, может быть null
     */
    private void addTab(String title, String pathToFXML, Object controller) {
        Tab newRequestTab = new Tab(title);
        try {
            // Применение стиля для вкладки
            newRequestTab.setStyle("-fx-font-family: Arial; -fx-font-size: 14;");

            if (!pathToFXML.equals("")) {
                // Загрузка FXML-файла с возможной привязкой контроллера
                FXMLLoader loader = new FXMLLoader(getClass().getResource(pathToFXML));
                if (controller != null) {
                    loader.setController(controller);
                }
                newRequestTab.setContent(loader.load());
            }
        } catch (IOException e) {
            throw new RuntimeException("Error loading FXML", e);
        }
        // Добавление вкладки в основной TabPane
        mainPane.getTabs().add(newRequestTab);
    }
}
