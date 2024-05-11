package com.example.util;

import javafx.scene.control.Alert;

/**
 * Утилитный класс для отображения сообщений в виде всплывающих окон с различными типами предупреждений.
 */
public class MyAlert {

    /**
     * Отображает всплывающее окно с информационным сообщением.
     *
     * @param message Текст сообщения для отображения.
     */
    public static void showInfoAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Информация");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Отображает всплывающее окно с сообщением об ошибке.
     *
     * @param message Текст сообщения для отображения.
     */
    public static void showErrorAlert(String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Ошибка");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

}
