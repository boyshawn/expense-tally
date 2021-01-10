package expense_tally.expense_manager.persistence;

import expense_tally.expense_manager.persistence.database.DatabaseConnectable;
import expense_tally.expense_manager.persistence.database.sqlite.SqLiteConnection;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class SqLiteConnectionTest {
  private DatabaseConnectable spyDatabaseConnectable;

  @Test
  void create() {
    String testDatabaseConnection = "testSql";
    assertThat(SqLiteConnection.create(testDatabaseConnection))
        .isNotNull();
  }

  @Test
  void connect_connectionSuccess() throws SQLException {
    spyDatabaseConnectable = SqLiteConnection.create("test error string");
    Connection mockedConnection = Mockito.mock(Connection.class);
    try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class)) {
      mockDriverManager.when(() -> DriverManager.getConnection(Mockito.anyString()))
          .thenReturn(mockedConnection);
      assertThat(spyDatabaseConnectable.connect())
          .isNotNull()
          .isEqualTo(mockedConnection);
    }
  }

  @Test
  void connect_error() {
    spyDatabaseConnectable = SqLiteConnection.create("test error string");
    SQLException testSqlException = new SQLException("test error");
    try (MockedStatic<DriverManager> mockDriverManager = Mockito.mockStatic(DriverManager.class)) {
      mockDriverManager.when(() -> DriverManager.getConnection(Mockito.anyString()))
          .thenThrow(testSqlException);
      assertThatThrownBy(() -> spyDatabaseConnectable.connect())
          .isInstanceOf(SQLException.class)
          .hasMessage("test error");
    }
  }
}