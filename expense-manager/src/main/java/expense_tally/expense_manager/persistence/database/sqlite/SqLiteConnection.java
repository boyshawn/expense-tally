package expense_tally.expense_manager.persistence.database.sqlite;

import expense_tally.exception.StringResolver;
import org.apache.commons.lang3.StringUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sqlite.JDBC;
import org.sqlite.SQLiteDataSource;

import javax.sql.DataSource;

/**
 * Manages the database connection to SQLite embedded database engine.
 * <p>All the database configuration and related information is stored inside this class</p>
 * <p>As quoted in the title <a href="https://en.wikipedia.org/wiki/SQLite">Wikipedia page</a>
 * , SQLite library is linked and form part of the application. Hence there isn't a database server. The entire
 * database (definitions, tables, indices, and the data itself) is stored inside a single cross-platform file.</p>
 */
public final class SqLiteConnection {
  private static final Logger LOGGER = LogManager.getLogger(SqLiteConnection.class);

  /**
   * Private constructor
   * Utility classes, which are collections of static members, are not meant to be instantiated
   */
  private SqLiteConnection() {
  }

  /**
   * Create a new data source based on the given parameters.
   * @param databaseFilePath URL of the database connection. Does not need to include database scheme.
   * @return a newly created instance of <i>data source</i> if creation succeeds.
   */
  public static DataSource createDataSource(String databaseFilePath) {
    if (StringUtils.isBlank(databaseFilePath)) {
      LOGGER.atWarn()
          .log("databaseFilePath is blank. databaseFilePath:{}",
              StringResolver.resolveNullableString(databaseFilePath));
      throw new IllegalArgumentException("database file path cannot be blank.");
    }
    SQLiteDataSource sqLiteDataSource = new SQLiteDataSource();
    String connectionUrl = constructConnectionUrl(databaseFilePath);
    sqLiteDataSource.setUrl(connectionUrl);
    LOGGER.atDebug().log("Creating new sqLiteDataSource. connectionUrl:{}", connectionUrl);
    return sqLiteDataSource;
  }

  private static String constructConnectionUrl(String databaseFile) {
    return JDBC.PREFIX + databaseFile;
  }
}
