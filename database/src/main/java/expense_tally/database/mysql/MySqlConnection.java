package expense_tally.database.mysql;

import com.mysql.cj.conf.ConnectionUrl;
import com.mysql.cj.jdbc.MysqlDataSource;
import expense_tally.exception.StringResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.sql.DataSource;
import java.sql.SQLException;

/**
 * This class provide the default implementation to connection to a MySQL database.
 */
public class MySqlConnection {
  private static final Logger LOGGER = LogManager.getLogger(MySqlConnection.class);

  /**
   * Private constructor
   * Utility classes, which are collections of static members, are not meant to be instantiated
   */
  private MySqlConnection() {
  }

  public static DataSource createDataSource(String connectionUrl, String database, String username, String password)
      throws SQLException {
    if (StringUtils.isBlank(connectionUrl)) {
      LOGGER.atError().log("connectionUrl is blank:{}", StringResolver.resolveNullableString(connectionUrl));
      throw new IllegalArgumentException("Connection URL should not be null or blank.");
    }
    if (StringUtils.isBlank(database)) {
      LOGGER.atError().log("database is blank:{}", StringResolver.resolveNullableString(database));
      throw new IllegalArgumentException("Database name should not be null or blank.");
    }
    MysqlDataSource mysqlDataSource = new MysqlDataSource();
    String connectionString = constructConnectionString(connectionUrl, database);
    mysqlDataSource.setUrl(connectionString);
    mysqlDataSource.setDatabaseName(database);
    boolean isUserNameBlank = StringUtils.isBlank(username);
    boolean isPasswordBlank = StringUtils.isBlank(password);
    if (!isUserNameBlank) {
      mysqlDataSource.setUser(username);
      if (!isPasswordBlank) {
        mysqlDataSource.setPassword(password);
      }
    } else if (!isPasswordBlank) { // username is blank
      LOGGER.atError().log("Password is provided without username.");
      throw new IllegalArgumentException("Password needs to be accompanied by username.");
    }
    //FIXME: Need to find a way to test
    mysqlDataSource.setLogSlowQueries(true);
    LOGGER.atInfo().log("Creating MySqlConnection: connectionString:{}, database:{}, username:{}", connectionString,
        database, StringResolver.resolveNullableString(username));
    return mysqlDataSource;
  }

  /**
   * Create the database connection string that MySQLDriver requires.
   * @param connectionUrl URL of the database connection. Does not need to include database scheme.
   * @param database name of the database to be connected
   * @return the database connection string that MySQLDriver requires.
   */
  private static String constructConnectionString(String connectionUrl, String database) {
    StringBuilder stringBuilder = new StringBuilder();
    stringBuilder.append(ConnectionUrl.Type.SINGLE_CONNECTION.getScheme());
    stringBuilder.append("//");
    stringBuilder.append(connectionUrl);
    // Due to a bug in the MySQL driver, the database need to be included as part of the connection URL
    stringBuilder.append("/");
    stringBuilder.append(database);
    LOGGER.atDebug().log("MySQL connection string:{}", stringBuilder.toString());
    return stringBuilder.toString();
  }
}
