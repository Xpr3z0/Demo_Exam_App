package com.example.util;

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
        database = Database.getInstance();
        loadInfoFromDB();
    }

    public void updateRequestInDB(String equip_num,
                                  String equip_type,
                                  String problem_desc,
                                  String request_comments,
                                  String status,
                                  String priority,
                                  String date_finish_plan,
                                  String responsible_repairer_name,
                                  String additional_repairer_name) {

        this.equip_num = equip_num;
        this.equip_type = equip_type;
        this.problem_desc = problem_desc;
        this.request_comments = request_comments;
        this.status = status;
        this.priority = priority;
        this.date_finish_plan = date_finish_plan;
        this.responsible_repairer_name = responsible_repairer_name;
        this.additional_repairer_name = additional_repairer_name;

        database = Database.getInstance();

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

    public void loadInfoFromDB() {
        database = Database.getInstance();
        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            String query = "SELECT r.id, r.equip_type, r.problem_desc, rr.client_name, rr.client_phone, " +
                    "r.equip_num, r.status, rp.priority, rr.date_start, rp.date_finish_plan, r.request_comments, " +
                    "m1.name AS responsible_repairer_name, m2.name AS additional_repairer_name " +
                    "FROM requests r " +
                    "JOIN request_regs rr ON r.id = rr.request_id " +
                    "LEFT JOIN request_processes rp ON r.id = rp.request_id " +
                    "LEFT JOIN assignments a1 ON r.id = a1.id_request AND a1.is_responsible = true " +
                    "LEFT JOIN assignments a2 ON r.id = a2.id_request AND a2.is_responsible = false " +
                    "LEFT JOIN members m1 ON a1.member_id = m1.id " +
                    "LEFT JOIN members m2 ON a2.member_id = m2.id " +
                    "WHERE r.id = ?";

            try (PreparedStatement preparedStatement = connection.prepareStatement(query)) {
                preparedStatement.setInt(1, id);

                try (ResultSet resultSet = preparedStatement.executeQuery()) {
                    if (resultSet.next()) {
                        id = resultSet.getInt("id");
                        equip_type = resultSet.getString("equip_type");
                        problem_desc = resultSet.getString("problem_desc");
                        client_name = resultSet.getString("client_name");
                        client_phone = resultSet.getString("client_phone");
                        equip_num = resultSet.getString("equip_num");
                        request_comments = resultSet.getString("request_comments");
                        status = resultSet.getString("status");
                        priority = resultSet.getString("priority");

                        // Используется getString вместо getDate, чтобы избежать NullPointerException в случае,
                        // когда эти данные у заявки ещё отсутствуют
                        date_start = resultSet.getString("date_start");
                        date_finish_plan = resultSet.getString("date_finish_plan");

                        responsible_repairer_name = resultSet.getString("responsible_repairer_name");
                        additional_repairer_name = resultSet.getString("additional_repairer_name");
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
            MyAlert.showErrorAlert("Ошибка при получении информации о заявке.");
        }
    }


    public boolean deleteRequestInDB() {
        String idStr = String.valueOf(id);
        database.deleteQuery("assignments", "id_request", idStr);
        database.deleteQuery("request_processes", "request_id", idStr);
        database.deleteQuery("request_regs", "request_id", idStr);
        boolean deletedSuccessfully = database.deleteQuery("requests", "id", idStr);

        // TODO: мб проверку условий доделать
        return deletedSuccessfully;
    }


    public int getId() {
        return id;
    }

    public String getEquip_type() {
        return equip_type;
    }

    public String getProblem_desc() {
        return problem_desc;
    }

    public String getClient_name() {
        return client_name;
    }

    public String getClient_phone() {
        return client_phone;
    }

    public String getEquip_num() {
        return equip_num;
    }

    public String getStatus() {
        return status;
    }

    public String getPriority() {
        return priority;
    }

    public String getDate_start() {
        return date_start;
    }

    public String getDate_finish_plan() {
        return date_finish_plan;
    }

    public String getRequest_comments() {
        return request_comments;
    }

    public String getResponsible_repairer_name() {
        return responsible_repairer_name;
    }

    public String getAdditional_repairer_name() {
        return additional_repairer_name;
    }
}
