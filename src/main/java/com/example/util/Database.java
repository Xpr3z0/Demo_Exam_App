package com.example.util;

import java.sql.*;
import java.util.ArrayList;

/**
 * Класс Database предоставляет методы для доступа и управления базой данных PostgreSQL.
 * Позволяет выполнять запросы, обновления, удаление данных и обеспечивает соединение с базой.
 */
public class Database {
    private static Database instance;
    public static final String URL = "jdbc:postgresql://localhost:8888/postgres";
    public static final String ROOT_LOGIN = "postgres";
    public static final String ROOT_PASS = "root";
    Connection externalConnection = null;

    /**
     * Возвращает единственный экземпляр класса Database (реализация паттерна Singleton).
     *
     * @return единственный экземпляр класса Database.
     */
    public static Database getInstance() {
        if (instance == null) {
            instance = new Database();
        }
        return instance;
    }

    /**
     * Проверяет возможность доступа к базе данных с заданным логином и паролем.
     *
     * @param login    логин пользователя.
     * @param password пароль пользователя.
     * @return true, если доступ успешен, иначе false.
     */
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

    /**
     * Получает соединение с базой данных.
     *
     * @return объект Connection.
     * @throws SQLException если возникает ошибка соединения.
     */
    public Connection getConnection() throws SQLException {
        externalConnection = null;
        externalConnection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
        return externalConnection;
    }

    /**
     * Закрывает текущее соединение с базой данных.
     *
     * @throws SQLException если возникает ошибка при закрытии соединения.
     */
    public void closeConnection() throws SQLException {
        if (externalConnection != null) {
            externalConnection.close();
        }
    }

    /**
     * Выполняет запрос на получение всех записей из указанной таблицы с сортировкой по заданному столбцу.
     *
     * @param selectedTable имя таблицы, из которой требуется получить данные.
     * @param orderBy       имя столбца, по которому производится сортировка.
     * @return объект ResultSet с результатами запроса.
     */
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

    /**
     * Получает все названия столбцов в указанной таблице.
     *
     * @param tableName имя таблицы.
     * @return список строк с названиями столбцов.
     */
    public ArrayList<String> getAllTableColumnNames(String tableName) {
        ArrayList<String> columnNames = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS)) {
            DatabaseMetaData metaData = connection.getMetaData();

            try (ResultSet columns = metaData.getColumns(null, null, tableName, null)) {
                while (columns.next()) {
                    String columnName = columns.getString("COLUMN_NAME");
                    columnNames.add(columnName);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        System.out.printf("Заголовки таблицы %s: %s\n", tableName, columnNames);
        return columnNames;
    }

    /**
     * Выполняет запрос для получения данных в виде списка строк.
     *
     * @param neededColumn название требуемого столбца.
     * @param table        название таблицы.
     * @param where        условие фильтрации (опционально).
     * @param orderBy      столбец для сортировки (опционально).
     * @return список строк, соответствующий результатам запроса.
     */
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

        System.out.printf("Список %s из таблицы %s: %s\n", neededColumn, table, finalList);
        return finalList;
    }

    /**
     * Выполняет запрос по заранее подготовленной строке SQL и возвращает список значений из указанного столбца.
     *
     * @param neededColumn название столбца.
     * @param sql          SQL-запрос.
     * @return список значений из указанного столбца.
     */
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

        System.out.printf("Список %s: %s\n", neededColumn, finalList);
        return finalList;
    }

    /**
     * Выполняет запрос и возвращает единственное значение из первого столбца результата.
     *
     * @param sql SQL-запрос.
     * @return строковое значение из первого столбца.
     */
    public String singleValueQuery(String sql) {
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

        System.out.printf("Получено значение: %s\n", resultValue);
        return resultValue;
    }

    /**
     * Выполняет запрос и возвращает все значения из результата в виде списка строк.
     *
     * @param query SQL-запрос.
     * @return список значений всех столбцов в каждой записи результата.
     */
    public ArrayList<String> executeQueryAndGetColumnValues(String query) {
        ArrayList<String> columnValues = new ArrayList<>();

        try (Connection connection = DriverManager.getConnection(Database.URL, Database.ROOT_LOGIN, Database.ROOT_PASS);
             Statement statement = connection.createStatement();
             ResultSet resultSet = statement.executeQuery(query)) {

            ResultSetMetaData metaData = resultSet.getMetaData();
            int columnCount = metaData.getColumnCount();

            while (resultSet.next()) {
                for (int i = 1; i <= columnCount; i++) {
                    String value = resultSet.getString(i);
                    columnValues.add(value);
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException("Ошибка выполнения запроса к базе данных", e);
        }

        System.out.printf("Значения записи: %s\n", columnValues);
        return columnValues;
    }

    /**
     * Обновляет значения в таблице, выполняя запрос с фильтрацией по заданному столбцу.
     *
     * @param selectedTable    имя таблицы.
     * @param columnChangeName имя столбца, который нужно обновить.
     * @param newRecord        новое значение для обновления.
     * @param columnSearchName имя столбца, по которому производится поиск.
     * @param columnSearch     значение, по которому производится поиск.
     * @return true, если обновление прошло успешно, иначе false.
     */
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

    /**
     * Преобразует строку в целое число, если это возможно, иначе возвращает исходную строку.
     *
     * @param str исходная строка.
     * @return объект Integer или исходная строка.
     */
    private Object convertStringToInteger(String str) {
        try {
            return new Integer(str);
        } catch (NumberFormatException e) {
            return str;
        }
    }

    /**
     * Выполняет запрос на удаление записей из таблицы, фильтруя по заданному столбцу.
     *
     * @param selectedTable    имя таблицы.
     * @param columnSearchName имя столбца, по которому производится поиск.
     * @param columnSearch     значение, по которому производится поиск.
     * @return true, если удаление прошло успешно, иначе false.
     */
    public boolean deleteQuery(String selectedTable, String columnSearchName, String columnSearch) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(
                    "DELETE FROM " + selectedTable + " WHERE " + columnSearchName + " = " + columnSearch);

            if (statement.executeUpdate() != -1) {
                System.out.printf("Удалены записи из таблицы %s, где %s = %s\n", selectedTable, columnSearchName, columnSearch);
                return true;
            } else {
                System.out.println("Ни одна запись не была удалена");
                return false;
            }

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Ни одна запись не была удалена");
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

    /**
     * Выполняет запрос на обновление данных в таблице с фильтрацией по заданному условию.
     *
     * @param table имя таблицы.
     * @param set   выражение SET для обновления значений.
     * @param where условие WHERE для фильтрации записей.
     * @return true, если обновление прошло успешно, иначе false.
     */
    public boolean updateQuery(String table, String set, String where) {
        Connection connection = null;

        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement preparedStatement = connection.prepareStatement(
                    "UPDATE " + table + " SET " + set + " WHERE " + where);

            if (preparedStatement.executeUpdate() > 0) {
                System.out.printf("В таблице %s обновлены значения %s, где %s\n", table, set, where);
                return true;
            } else {
                System.out.printf("Ни одна запись в таблице %s не была обновлена\n", table);
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

    /**
     * Выполняет простой SQL-запрос (например, на обновление или удаление данных).
     *
     * @param sql строка SQL-запроса.
     * @return true, если запрос прошел успешно, иначе false.
     */
    public boolean simpleQuery(String sql) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(URL, ROOT_LOGIN, ROOT_PASS);
            PreparedStatement statement = connection.prepareStatement(sql);

            if (statement.executeUpdate() != -1) {
                System.out.println("Запрос выполнен успешно: " + sql);
                return true;
            } else {
                System.out.println("Ошибка при выполнении запроса: " + sql);
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
