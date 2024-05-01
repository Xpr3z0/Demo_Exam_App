package com.example.util;

import java.sql.*;
import java.util.ArrayList;

public class Database {
    private static Database instance;
    public static final String URL = "jdbc:postgresql://localhost:8888/postgres";
    public static final String ROOT_LOGIN = "postgres";
    public static final String ROOT_PASS = "root";
    Connection externalConnection = null;

    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    public boolean accessToDB(String login, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            connection.close();
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        return true;
    }


    public Connection getConnection() throws SQLException {
        externalConnection = null;
        externalConnection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
        return externalConnection;
    }

    public void closeConnection() throws SQLException {
        if (externalConnection != null) {
            externalConnection.close();
        }
    }


    public ResultSet getTable(String selectedTable, String orderBy) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(
                    "SELECT * FROM " + selectedTable + " ORDER BY " + orderBy + " ASC");
            return statement.executeQuery();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    // Метод для получения названий всех полей определенной таблицы
    public ArrayList<String> getAllTableColumnNames(String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            DatabaseMetaData metaData = connection.getMetaData();

            // Получаем метаданные таблицы
            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    // Получаем название столбца (поля)
                    String columnName = columns.getString("COLUMN_NAME");
                    columnNames.add(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Обработка ошибки доступа к базе данных
        }

        return columnNames;
    }



    public ArrayList<String> stringListQuery(String neededColumn, String table, String where, String orderBy) {
        Connection connection = null;
        ResultSet resultSet;

        String finalQuery = "SELECT " + neededColumn + " FROM " + table;
        if (where != null) {
            finalQuery += " WHERE " + where;
        }
        if (orderBy != null) {
            finalQuery += " ORDER BY " + orderBy;
        }

        ArrayList<String> finalList = new ArrayList<>();
        try {

            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(finalQuery.trim());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                finalList.add(resultSet.getString(neededColumn));
            }
            System.out.println(finalList);
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalList;
    }


    public ArrayList<String> stringListQuery(String neededColumn, String sql) {
        Connection connection = null;
        ResultSet resultSet;

        ArrayList<String> finalList = new ArrayList<>();
        try {

            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(sql.trim());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                finalList.add(resultSet.getString(neededColumn));
            }
            System.out.println(finalList);
            resultSet.close();
            statement.close();

        } catch (SQLException e) {
            e.printStackTrace();
            return null;

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return finalList;
    }

    public String singleValueQuery(String sql)  {

        String resultValue = "";

        try (Connection connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS)) {
            try (Statement statement = connection.createStatement();
                 ResultSet resultSet = statement.executeQuery(sql)) {
                if (resultSet.next()) {
                    resultValue = resultSet.getString(1);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return resultValue;
    }

    public ArrayList<String> executeQueryAndGetColumnValues(String query) {
        ArrayList<String> columnValues = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i); // Получаем значение как строку
                    columnValues.add(value); // Добавляем в список
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса к базе данных", e);
        }

        return columnValues;
    }



    // TODO: возможно не надо
    public boolean updateTable(String selectedTable, String columnChangeName, String newRecord, String columnSearchName, String columnSearch) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(
                    "UPDATE " + selectedTable +
                            " SET " + columnChangeName + " = ?" +
                            " WHERE " + columnSearchName + " = " + columnSearch);
            statement.setObject(1, convertStringToInteger(newRecord));

            if (statement.executeUpdate() != -1) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }


    private Object convertStringToInteger(String str){
        try {
            return new Integer(str);

        } catch (NumberFormatException e) {
            return str;
        }
    }

    public boolean deleteQuery(String selectedTable, String columnSearchName, String columnSearch) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + selectedTable + " WHERE " + columnSearchName + " = " + columnSearch);

            if (statement.executeUpdate() != -1) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public boolean updateQuery(String table, String set, String where) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + table + " SET " + set + " WHERE " + where);

            if (preparedStatement.executeUpdate() > 0) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;

        } finally {
            try {
                if (connection != null) {
                    connection.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean simpleQuery(String sql) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(sql);

            if (statement.executeUpdate() != -1) {
                return true;
            } else {
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        } finally {
            if (connection != null) {
                try {
                    connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
