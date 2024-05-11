package com.example;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.util.Locale;

/**
 * Основной класс приложения, запускающий JavaFX-приложение и загружающий начальное окно.
 */
public class Main extends Application {
    /**
     * Метод запуска JavaFX-приложения.
     *
     * @param stage основной этап, на котором отображается начальное окно.
     * @throws Exception если возникает ошибка при загрузке ресурса.
     */
    @Override
    public void start(Stage stage) throws Exception {
        // Установка локали по умолчанию на русский язык
        Locale.setDefault(new Locale("ru"));

        // Загрузка файла разметки Login.fxml для начального окна авторизации
        Parent root = FXMLLoader.load(getClass().getResource("/view/Login.fxml"));

        // Создание сцены с загруженной разметкой
        Scene scene = new Scene(root);

        // Установка сцены для этапа и названия окна
        stage.setScene(scene);
        stage.setTitle("Авторизация");

        // Отображение окна
        stage.show();
    }

    /**
     * Главный метод программы, запускающий JavaFX-приложение.
     *
     * @param args аргументы командной строки.
     */
    public static void main(String[] args) {
        // Запуск JavaFX-приложения
        launch(args);
    }
}
