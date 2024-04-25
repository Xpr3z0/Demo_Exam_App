package com.example.bdclient;

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

    public boolean deleteRowTable(String selectedTable, String columnSearchName, String columnSearch) {
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
