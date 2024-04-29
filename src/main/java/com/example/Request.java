package com.example;

import com.example.bdclient.Database;
import com.example.controller.dialogs.MyAlert;

import java.sql.*;

public class Request {
    int id;
    String equip_type;
    String problem_desc;
    String client_name;
    String client_phone;
    String equip_num;
    String status;
    String priority;
    String date_start;
    String date_finish_plan;
    String request_comments;
    String responsible_repairer_name;
    String additional_repairer_name;

    Database database;


    public Request(int id) {
        this.id = id;
        loadInfoFromDB();
    }

    public void updateRequestInDB(String equip_num,
                                  String equip_type,
                                  String problem_desc,
                                  String request_comments,
                                  String status,
                                  String priority,
                                  String client_name,
                                  String client_phone,
                                  String date_start,
                                  String date_finish_plan,
                                  String responsible_repairer_name,
                                  String additional_repairer_name) {

        this.equip_num = equip_num;
        this.equip_type = equip_type;
        this.problem_desc = problem_desc;
        this.request_comments = request_comments;
        this.status = status;
        this.priority = priority;
        this.client_name = client_name;
        this.client_phone = client_phone;
        this.date_start = date_start;
        this.date_finish_plan = date_finish_plan;
        this.responsible_repairer_name = responsible_repairer_name;
        this.additional_repairer_name = additional_repairer_name;


        // Обновляем запись в таблице requests
        String updateRequestsQuery = String.format("UPDATE requests " +
                "SET equip_num = '%s', equip_type = '%s', problem_desc = '%s', request_comments = '%s', status = '%s' " +
                "WHERE id = %d", equip_num, equip_type, problem_desc, request_comments, status, id);
        database.simpleQuery(updateRequestsQuery);


        // Обновляем запись в таблице request_processes
        String insertOrUpdateProcessQuery = String.format(
                "INSERT INTO request_processes (request_id, priority, date_finish_plan) " +
                        "VALUES (%d, '%s', '%s') " +
                        "ON CONFLICT (request_id) DO UPDATE " +
                        "SET priority = '%s', date_finish_plan = '%s'",
                id, priority, date_finish_plan, priority, date_finish_plan);

        database.simpleQuery(insertOrUpdateProcessQuery);


        // Добавляем или обновляем запись в таблице assignments для ответственного исполнителя
        String updateRespAssignQuery = String.format(
                "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                        "VALUES (%d, (SELECT id FROM members WHERE name = '%s'), %s) " +
                        "ON CONFLICT (id_request, is_responsible) DO UPDATE " +
                        "SET member_id = EXCLUDED.member_id", id, responsible_repairer_name, true);
        database.simpleQuery(updateRespAssignQuery);


        if (additional_repairer_name.equals("Нет")) {
            database.simpleQuery("DELETE FROM assignments WHERE is_responsible = false AND id_request = " + id);

        } else {
            // Обновляем запись в таблице assignments для дополнительного исполнителя
            String updateAdditAssignQuery = String.format(
                    "INSERT INTO assignments (id_request, member_id, is_responsible) " +
                            "VALUES (%d, (SELECT id FROM members WHERE name = '%s'), %s) " +
                            "ON CONFLICT (id_request, is_responsible) DO UPDATE " +
                            "SET member_id = EXCLUDED.member_id", id, additional_repairer_name, false);
            database.simpleQuery(updateAdditAssignQuery);
        }

        MyAlert.showInfoAlert("Информация по заявке обновлена успешно.");
    }

    // TODO: будет работать только с заявками, которые есть во всех таблиццах (r, rr, rp)
    public void loadInfoFromDB() {
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            String query = "SELECT r.id, r.equip_type, r.problem_desc, rr.client_name, rr.client_phone, " +
                    "r.equip_num, r.status, rp.priority, rr.date_start, rp.date_finish_plan, r.request_comments " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "JOIN request_processes rp ON r.id = rp.request_id " +
                    "WHERE r.id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = Integer.parseInt(resultSet.getString("id"));
                        equip_type = resultSet.getString("equip_type");
                        problem_desc = resultSet.getString("problem_desc");
                        client_name = resultSet.getString("client_name");
                        client_phone = resultSet.getString("client_phone");
                        equip_num = resultSet.getString("equip_num");
                        request_comments = resultSet.getString("request_comments");
                        status = resultSet.getString("status");
                        priority = resultSet.getString("priority");

                        // Преобразуем даты из SQL Date в строковый формат для отображения
                        date_start = resultSet.getDate("date_start").toString();
                        date_finish_plan = resultSet.getDate("date_finish_plan").toString();

                        responsible_repairer_name = database.stringListQuery("m.name",
                                "FROM assignments a JOIN members m ON a.member_id = m.id",
                                "a.id_request = " + id + " AND is_responsible = true",
                                "m.name").get(0);

                        additional_repairer_name = database.stringListQuery("m.name",
                                "FROM assignments a JOIN members m ON a.member_id = m.id",
                                "a.id_request = " + id + " AND is_responsible = false",
                                "m.name").get(0);

                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении информации о заявке.");
        }
    }

    public int getId() {
        return id;
    }
}
