package com.example.controller.operator_tabs;

import com.example.util.Database;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.*;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AddRequestTabControllerTest {

    @Mock
    private Database database;

    @Mock
    private Connection connection;

    @Mock
    private PreparedStatement insertRequestsStatement;

    @Mock
    private PreparedStatement insertRequestRegsStatement;

    @Mock
    private PreparedStatement generatedIdStatement;

    @Mock
    private ResultSet resultSet;

    @InjectMocks
    private AddRequestTabController controller;

    @BeforeEach
    void setUp() throws SQLException {
        // Настройка моков для каждого SQL-запроса
        when(connection.prepareStatement("INSERT INTO requests (equip_num, equip_type, problem_desc, status) VALUES (?, ?, ?, ?)")).thenReturn(insertRequestsStatement);
        when(connection.prepareStatement("SELECT LASTVAL()")).thenReturn(generatedIdStatement);
        when(connection.prepareStatement("INSERT INTO request_regs (request_id, client_name, client_phone, date_start) VALUES (?, ?, ?, ?)")).thenReturn(insertRequestRegsStatement);

        // Настройка возвращаемых значений для ResultSet
        when(generatedIdStatement.executeQuery()).thenReturn(resultSet);
        when(resultSet.next()).thenReturn(true);
        when(resultSet.getInt(1)).thenReturn(1);
    }

    @Test
    void testAddRequest_Success() throws SQLException {
        // Настройка успешного выполнения executeUpdate
        when(insertRequestsStatement.executeUpdate()).thenReturn(1);
        when(insertRequestRegsStatement.executeUpdate()).thenReturn(1);

        // Вызов метода с передачей объекта connection
        boolean result = controller.addRequest(connection, "Иванов Иван Иванович", "1234567890", "SN12345", "Ноутбук", "Ноутбук не включается");

        // Проверка вызова executeUpdate и commit
        verify(insertRequestsStatement, times(1)).executeUpdate();
        verify(insertRequestRegsStatement, times(1)).executeUpdate();
        verify(connection, times(1)).commit();

        // Проверка результата
        assert result;
    }
}
