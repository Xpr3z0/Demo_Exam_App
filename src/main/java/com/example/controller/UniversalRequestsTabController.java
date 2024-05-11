package com.example.controller;

import com.example.util.Request;
import com.example.util.Database;
import com.example.util.MyAlert;
import com.example.util.UniversalAddDialog;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.ResourceBundle;

/**
 * Контроллер для универсальной вкладки заявок. Отображает и управляет заявками на ремонт.
 * С помощью этого контроллера формируются вкладки "Принятые заявки" и "Новые заявки" у менеджера,
 * а также "Ответственные заявки" и "Обычные заявки" у исполнителя.
 *
 * От роли пользователя зависит доступность определённых функций.
 */
public class UniversalRequestsTabController implements Initializable {
    // Поле ввода для фильтрации по ID заявки
    public TextField idFilterTF;

    // Компоненты FXML
    @FXML
    private ListView<String> repairRequestListView;
    public Label requestNumberLabel;
    public TextField equipTypeField;
    public TextArea descriptionTextArea;
    public Label clientNameLabel;
    public Label clientPhoneLabel;
    public TextField equipSerialField;
    public TextArea commentsTextArea;
    public ScrollPane moreInfoScrollPane;
    public BorderPane moreInfoPane;
    public VBox infoVBox;
    public VBox stateVBox;
    public ChoiceBox<String> stateChoice;
    public ChoiceBox<String> responsibleRepairerChoice;
    public ChoiceBox<String> additionalRepairerChoice;
    public ChoiceBox<String> priorityChoice;
    public TextField registerDateTF;
    public TextField finishDateTF;

    public Button refreshListBtn;
    public Button createOrCheckReportBtn;

    // Экземпляры базы данных и текущего запроса
    private Database database;
    private int currentRequestNumber = -1;
    private String role;

    // Карта заявок
    private LinkedHashMap<Integer, Request> requestMap;
    private boolean filterApplied = false;

    /**
     * Конструктор, принимающий роль пользователя.
     *
     * @param role Роль текущего пользователя, например, "manager", "resp_repairer" и т.д.
     */
    public UniversalRequestsTabController(String role) {
        this.role = role;
    }

    /**
     * Инициализирует контроллер и настраивает интерфейс согласно роли пользователя.
     *
     * @param location URL для загрузки ресурсов (не используется)
     * @param resources Набор ресурсов для локализации (не используется)
     */
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Инициализация базы данных и карты заявок
        database = Database.getInstance();
        requestMap = new LinkedHashMap<>();

        // Начальные настройки интерфейса
        moreInfoPane.setVisible(false);
        createOrCheckReportBtn.setVisible(false);
        setUnavailableFields();
        loadRepairRequests();

        // Инициализация элементов выбора
        priorityChoice.getItems().addAll("Срочный", "Высокий", "Нормальный", "Низкий");
        stateChoice.getItems().addAll("В работе", "Выполнено", "В ожидании", "Закрыта");

        ArrayList<String> repairersList = database.stringListQuery("name", "members", "role = 'repairer'", "name");
        responsibleRepairerChoice.getItems().addAll(repairersList);
        additionalRepairerChoice.getItems().add("Нет");
        additionalRepairerChoice.getItems().addAll(repairersList);
        additionalRepairerChoice.setValue("Нет");

        // Настройка выбора элементов в списке заявок
        repairRequestListView.setOnMouseClicked(event -> {
            int selectedIndex = repairRequestListView.getSelectionModel().getSelectedIndex();
            if (selectedIndex != -1) {
                String selectedItem = repairRequestListView.getItems().get(selectedIndex);
                currentRequestNumber = Integer.parseInt(selectedItem);
                System.out.println("Выбрана заявка №" + currentRequestNumber);
                showMoreInfo(currentRequestNumber);
            }
        });
    }

    /**
     * Настраивает недоступные поля в зависимости от роли пользователя.
     */
    private void setUnavailableFields() {
        if (role.equals("resp_repairer")) {
            responsibleRepairerChoice.setDisable(true);
        } else if (role.equals("addit_repairer")) {
            stateChoice.setDisable(true);
            priorityChoice.setDisable(true);
            responsibleRepairerChoice.setDisable(true);
            additionalRepairerChoice.setDisable(true);
            finishDateTF.setEditable(false);
        } else if (role.equals("manager_new")) {
            infoVBox.getChildren().remove(stateVBox);
        }
    }

    /**
     * Применяет фильтры для поиска заявок по ID.
     */
    @FXML
    public void applyFilters() {
        if (!idFilterTF.getText().trim().equals("")) {
            int requestId = Integer.parseInt(idFilterTF.getText());

            if (requestMap.containsKey(requestId)) {
                repairRequestListView.getItems().clear();
                repairRequestListView.getItems().add(String.valueOf(requestId));
                filterApplied = true;
                showMoreInfo(requestId);

            } else {
                filterApplied = false;
                moreInfoPane.setVisible(false);
                clearFilters();
                MyAlert.showErrorAlert(String.format("Заявка с номером %d не найдена", requestId));
            }
        } else {
            filterApplied = false;
        }
    }

    /**
     * Загружает и отображает список заявок в зависимости от роли пользователя.
     */
    public void loadRepairRequests() {
        // Очистка предыдущих данных
        repairRequestListView.getItems().clear();
        requestMap.clear();

        // Получение запросов в зависимости от роли пользователя
        String query = getQueryForRole();
        if (query != null) {
            ArrayList<String> idList = database.stringListQuery("id", query);

            // Заполнение списка заявок
            for (String idStr : idList) {
                int id = Integer.parseInt(idStr);
                requestMap.put(id, new Request(id));
                repairRequestListView.getItems().add(idStr);
            }

            if (filterApplied) {
                applyFilters();
            }

            // Установка фабрики ячеек для отображения информации о каждой заявке
            repairRequestListView.setCellFactory(param -> {
                return new ListCell<String>() {
                    @Override
                    protected void updateItem(String item, boolean empty) {
                        super.updateItem(item, empty);

                        if (item == null || empty) {
                            setGraphic(null);
                        } else {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/view/ListItem.fxml"));
                            try {
                                Parent root = loader.load();
                                ListItemController controller = loader.getController();
                                controller.setRequestNumber(Integer.parseInt(item));
                                setGraphic(root);
                            } catch (IOException e) {
                                e.printStackTrace();
                                setGraphic(null);
                            }
                        }
                    }
                };
            });

        } else {
            MyAlert.showErrorAlert("Ошибка: роль " + role + " не найдена");
        }
    }

    /**
     * Формирует SQL-запрос для получения списка заявок в зависимости от роли пользователя.
     *
     * @return SQL-запрос или null, если роль не поддерживается.
     */
    private String getQueryForRole() {
        if (role.equals("manager")) {
            return "SELECT id FROM requests WHERE status != 'Новая' ORDER BY id";

        } else if (role.equals("manager_new")) {
            return "SELECT id FROM requests WHERE status = 'Новая' ORDER BY id";

        } else if (role.equals("resp_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = true " +
                    "ORDER BY r.id";

        } else if (role.equals("addit_repairer")) {
            return "SELECT r.id " +
                    "FROM requests r " +
                    "JOIN assignments a ON r.id = a.id_request " +
                    "WHERE r.status != 'Новая' " +
                    "AND a.member_id = " + MainViewController.userID + " " +
                    "AND a.is_responsible = false " +
                    "ORDER BY r.id";
        } else {
            return null;
        }
    }

    /**
     * Очищает все примененные фильтры и заново загружает список заявок.
     */
    @FXML
    public void clearFilters() {
        idFilterTF.clear();
        loadRepairRequests();
    }

    /**
     * Перезагружает список заявок.
     */
    @FXML
    public void onActionRefresh() {
        loadRepairRequests();
    }

    /**
     * Сохраняет изменения в заявке на ремонт.
     */
    public void onActionSave() {
        // Выбор статуса заявки в зависимости от роли
        String stateValue;
        if (role.equals("manager_new")) {
            stateValue = "В работе";
        } else {
            stateValue = stateChoice.getValue();
        }

        // Обновление данных в базе данных
        requestMap.get(currentRequestNumber).updateRequestInDB(
                equipSerialField.getText(),
                equipTypeField.getText(),
                descriptionTextArea.getText(),
                commentsTextArea.getText(),
                stateValue,
                priorityChoice.getValue(),
                finishDateTF.getText(),
                responsibleRepairerChoice.getValue(),
                additionalRepairerChoice.getValue());

        if (role.equals("manager_new")) {
            moreInfoPane.setVisible(false);
            loadRepairRequests();
        } else {
            showMoreInfo(currentRequestNumber);
        }
    }

    /**
     * Открывает или создает отчет для текущей заявки на ремонт.
     */
    public void onActionCreateOrCheckReport() {
        // Проверка наличия отчета или создание нового
        if (createOrCheckReportBtn.getText().equals("Посмотреть отчёт")) {
            ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                    "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

            if (reportValues != null && reportValues.size() > 0) {
                String report = String.format(
                        "Номер заявки: %s\n\nТип ремонта: %s\n\nВремя выполнения, дней: %s\n\nСтоимость: %s\n\nРесурсы: %s\n\nПричина неисправности: %s\n\nОказанная помощь: %s",
                        reportValues.get(0), reportValues.get(1), reportValues.get(2), reportValues.get(3),
                        reportValues.get(4), reportValues.get(5), reportValues.get(6));
                MyAlert.showInfoAlert(report);
            } else {
                MyAlert.showInfoAlert("Отчёт для заявки №" + currentRequestNumber + " отсутствует");
            }

        } else {
            new UniversalAddDialog("reports", database.getAllTableColumnNames("reports"));
            showMoreInfo(currentRequestNumber);
        }

    }

    /**
     * Отображает более подробную информацию о заявке на ремонт.
     *
     * @param requestId Идентификатор заявки для отображения.
     */
    public void showMoreInfo(int requestId) {
        Request request = requestMap.get(requestId);

        requestNumberLabel.setText("Заявка №" + requestId);
        equipTypeField.setText(request.getEquip_type());
        descriptionTextArea.setText(request.getProblem_desc());
        clientNameLabel.setText("ФИО: " + request.getClient_name());
        clientPhoneLabel.setText("Телефон: " + request.getClient_phone());
        equipSerialField.setText(request.getEquip_num());
        commentsTextArea.setText(request.getRequest_comments());
        registerDateTF.setText(request.getDate_start());

        if (role.equals("manager_new")) {
            finishDateTF.setText("");
            priorityChoice.setValue(null);
            responsibleRepairerChoice.setValue(null);
            additionalRepairerChoice.setValue("Нет");
        } else {
            stateChoice.setValue(request.getStatus());
            finishDateTF.setText(request.getDate_finish_plan());
            priorityChoice.setValue(request.getPriority());
            responsibleRepairerChoice.setValue(request.getResponsible_repairer_name());
            additionalRepairerChoice.setValue(request.getAdditional_repairer_name());
        }

        // Отображение кнопки для проверки отчета в зависимости от состояния
        String currentState = request.getStatus();
        if (currentState.equals("Выполнено") || currentState.equals("Закрыта")) {

            if (role.equals("resp_repairer")) {
                ArrayList<String> reportValues = database.executeQueryAndGetColumnValues(
                        "SELECT * FROM reports WHERE request_id = " + currentRequestNumber);

                if (reportValues != null && reportValues.size() > 0) {
                    createOrCheckReportBtn.setText("Посмотреть отчёт");
                } else {
                    createOrCheckReportBtn.setText("Создать отчёт");
                }
            }

            createOrCheckReportBtn.setVisible(true);
        } else {
            createOrCheckReportBtn.setVisible(false);
        }

        moreInfoPane.setVisible(true);
    }
}
