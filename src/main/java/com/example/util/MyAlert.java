package com.example.util;

import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;

import java.util.Optional;

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

    /**
     * Отображает всплывающее окно предупреждения с кнопками подтверждения и отмены.
     *
     * @param title Заголовок диалогового окна.
     * @param message Сообщение, отображаемое в диалоговом окне.
     * @return true, если пользователь нажал OK, иначе false.
     */
    public static boolean showWarningConfirmation(String title, String message) {
        // Создаем Alert с типом WARNING и добавляем стандартные кнопки OK и Cancel
        Alert alert = new Alert(Alert.AlertType.WARNING, message, ButtonType.OK, ButtonType.CANCEL);
        alert.setTitle(title);
        alert.setHeaderText(null); // Убираем заголовок внутри окна, если не нужен

        // Отображаем Alert и ждем решения пользователя
        Optional<ButtonType> result = alert.showAndWait();
        // Возвращаем true, если нажата кнопка OK
        return result.isPresent() && result.get() == ButtonType.OK;
    }
}
