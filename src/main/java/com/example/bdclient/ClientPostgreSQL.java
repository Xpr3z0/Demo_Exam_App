package com.example.bdclient;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class ClientPostgreSQL {
    private static ClientPostgreSQL instance;
    private Properties dbProperties;
    private String login = null;
    private String password = null;
    private String dbUrl = null;
    private String dbSchema = null;
    Connection externalConnection = null;

    public static ClientPostgreSQL getInstance() {
        return instance == null ? instance = new ClientPostgreSQL() : instance;
    }

    private ClientPostgreSQL() {
        try {
            dbProperties = getDbProperties(getClass().getResource("/config.properties").openStream());
            if (dbProperties != null) {
                Class.forName(dbProperties.getProperty("db.driver"));
                dbUrl = dbProperties.getProperty("db.url");
                dbSchema = dbProperties.getProperty("db.schema");
            }
        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл настроек");
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Properties getDbProperties(InputStream configFileInput) {
        Properties property = new Properties();
        try {
            property.load(configFileInput);
            return property;
        } catch (FileNotFoundException e) {
            System.out.println("Не найден файл настроек");
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean accessToDB(String login, String password) {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, login, password);
            connection.close();
            this.login = login;
            this.password = password;
        } catch (SQLException e) {
            System.out.println(e);
            return false;
        }
        createTablesIfNeeded();
        return true;
    }


    public String getLogin() {
        return login;
    }

    public List<String> getTableNames() {
        Connection connection = null;
        try {
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(dbProperties.getProperty("tableNamesSql"));
            statement.setString(1, dbSchema);
            ResultSet resultSet = statement.executeQuery();
            ArrayList<String> arrayList = new ArrayList<>();
            while (resultSet.next()) {
                arrayList.add(resultSet.getString(1));
            }
            return arrayList;
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

    public Connection getConnection() throws SQLException {
        externalConnection = null;
        externalConnection = DriverManager.getConnection(dbUrl, login, password);
        return externalConnection;
    }

    public void closeConnection() throws SQLException {
        if (externalConnection != null) {
            externalConnection.close();
        }
    }


    public ResultSet getTable(String selectedTable) {
        Connection connection = null;
        String orderBy = "";
        switch (selectedTable) {
            case "drugs": orderBy = "drug_id"; break;
            case "employees": orderBy = "employee_id"; break;
            case "records": orderBy = "transaction_id"; break;
            default:
                System.out.println("orderBy detection error");
        }
        try {
            String query = String.format(dbProperties.getProperty("getTableSql"), dbSchema, selectedTable, orderBy);
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query.toString());
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

    public ArrayList<String> stringListQuery(String query) {
        Connection connection = null;
        ResultSet resultSet;
        ArrayList<String> finalList = new ArrayList<>();
        try {

            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query.trim());
            resultSet = statement.executeQuery();
            while (resultSet.next()) {
                finalList.add(resultSet.getString("name"));
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


    public boolean updateTable(String selectedTable, String columnChangeName, String newRecord, String columnSearchName, String columnSearch) {
        Connection connection = null;
        try {
            String query = String.format(dbProperties.getProperty("updateTable"), dbSchema, selectedTable, columnChangeName, columnSearchName, columnSearch);
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query);
            statement.setObject(1, convertStringToInteger(newRecord));
            return statement.executeUpdate() != -1 ? true : false;
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


    public boolean deleteRowTable(String selectedTable, String columnSearchName, String columnSearch) {
        Connection connection = null;
        try {
            String query = String.format(dbProperties.getProperty("deleteRowTable"), dbSchema, selectedTable, columnSearchName, columnSearch);
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeUpdate() != -1 ? true : false;
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


    public boolean simpleQuery(String selectedTable, String sql) {
        Connection connection = null;
        try {
            String query = String.format(sql, dbSchema, selectedTable);
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeUpdate() != -1 ? true : false;
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

    public boolean simpleQuery(String sql) {
        Connection connection = null;
        try {
            String query = String.format(sql, dbSchema, "public");
            connection = DriverManager.getConnection(dbUrl, login, password);
            PreparedStatement statement = connection.prepareStatement(query);
            return statement.executeUpdate() != -1 ? true : false;
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
        }catch (NumberFormatException e){
            return str;
        }
    }

    public void createTablesIfNeeded() {
        List<String> tableNames = getTableNames();
        Connection connection = null;

        if (tableNames == null || !tableNames.contains("drugs")) {

            boolean created = simpleQuery("public", dbProperties.getProperty("createDrugsTable"));
            if (created) {
                System.out.println("Table 'drugs' created.");
                boolean inserted = simpleQuery("public", drugValues);
                if (inserted) {
                    System.out.println("Initial data inserted into table 'drugs'.");
                } else {
                    System.out.println("Error inserting initial data into table 'drugs'.");
                }
            } else {
                System.out.println("Error creating table 'drugs'.");
            }
        }

        if (tableNames == null || !tableNames.contains("employees")) {
            boolean created = simpleQuery("public", dbProperties.getProperty("createEmployeesTable"));
            if (created) {
                System.out.println("Table 'employees' created.");
                boolean inserted = simpleQuery("public", employeeValues);
                if (inserted) {
                    System.out.println("Initial data inserted into table 'employees'.");
                } else {
                    System.out.println("Error inserting initial data into table 'employees'.");
                }

            } else {
                System.out.println("Error creating table 'employees'.");
            }
        }

        if (tableNames == null || !tableNames.contains("records")) {
            boolean created = simpleQuery("public", dbProperties.getProperty("createDrugRecordsTable"));
            if (created) {
                System.out.println("Table 'drug_records' created.");
            } else {
                System.out.println("Error creating table 'drug_records'.");
            }
        }
    }




    /** SQL запросы **/
    private final String drugValues =
            "INSERT INTO Drugs (name, manufacturer, packaging_quantity, measure_unit, dosage_mg, in_price, out_price, in_stock) VALUES " +
                    "('Парацетамол', 'ФармаКорп', 20, 'таб', '500', 100.00, 150.00, 53), " +
                    "('Ибупрофен', 'ЗдоровьеМед', 30, 'таб', '400', 150.00, 220.00, 21), " +
                    "('Аспирин', 'Медикс', 50, 'таб', '300', 50.00, 80.00, 13), " +
                    "('Сироп от кашля', 'ЛекФарм', 100, 'мл', '10', 300.00, 450.00, 22), " +
                    "('Амоксициллин', 'БиоМед', 10, 'таб', '250', 250.00, 380.00, 32), " +
                    "('Лоратадин', 'ЗдоровьеМед', 30, 'таб', '10', 120.00, 180.00, 15), " +
                    "('Эритромицин', 'БиоМед', 20, 'таб', '500', 350.00, 500.00, 28), " +
                    "('Омепразол', 'ФармаКорп', 14, 'капс', '20', 220.00, 320.00, 32), " +
                    "('Симвастатин', 'Медикс', 28, 'таб', '20', 200.00, 300.00, 8), " +
                    "('Диазепам', 'ЛекФарм', 20, 'таб', '5', 150.00, 220.00, 19);";

    private final String employeeValues =
            "INSERT INTO Employees (last_name, first_name, patronymic, phone) VALUES " +
                    "('Иванов', 'Алексей', 'Петрович', '+79123456789'), " +
                    "('Смирнова', 'Ольга', 'Андреевна', '+79098765432'), " +
                    "('Петров', 'Иван', 'Сергеевич', '+79216547890'), " +
                    "('Сидорова', 'Екатерина', 'Дмитриевна', '+79325648701'), " +
                    "('Михайлов', 'Павел', 'Александрович', '+79436985214'), " +
                    "('Николаева', 'Мария', 'Ивановна', '+79547851236');";

}
