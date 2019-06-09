package expense_tally;

import expense_tally.csv_parser.CsvParser;
import expense_tally.csv_parser.model.CsvTransaction;
import expense_tally.expense_manager.DatabaseConnectable;
import expense_tally.expense_manager.ExpenseReadable;
import expense_tally.expense_manager.ExpenseReportReader;
import expense_tally.expense_manager.ExpenseTransactionMapper;
import expense_tally.expense_manager.SqlLiteConnection;
import expense_tally.expense_manager.model.ExpenseManagerMapKey;
import expense_tally.expense_manager.model.ExpenseManagerTransaction;
import expense_tally.expense_manager.model.ExpenseReport;
import expense_tally.reconciliation.ExpenseReconciler;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class Driver {
  private static final Logger LOGGER = LogManager.getLogger(Driver.class);
  private static String csvFilename;
  private static String databaseFilename;

  public Driver() {
  }

  public void readArgs(String[] args) throws IllegalArgumentException {
    final String DATABASE_PARAMETER = "database-filepath";
    final String CSV_PARAMETER = "csv-filepath";
    final char EQUAL_SIGN = '=';
    final String EQUAL_SEPARATOR = "=";
    final char DOUBLE_QUOTATION = '"';
    /*
     * Expect to received --database-filepath = XXXX --csv-filepath= XXXX
     * Allow 3 format of declaring parameter
     * 1. parameter=XXXX //TODO
     * 2. parameter = xxxxx
     * 3. parameter =xxxxx
     */
    if (args.length % 2 != 0) {
      LOGGER.error("Argument is not in odd number. Args=" + Arrays.toString(args));
      throw new IllegalArgumentException("Odd number of parameters provided.");
    }
    this.csvFilename = args[0];
    this.databaseFilename = args[1];
    // Parse first string
    // Strip the equal sign at the place if any

    int argumentIndex = 0;
    while (argumentIndex < args.length) {
      String parameter = args[argumentIndex].trim().replace(EQUAL_SIGN, Character.MIN_VALUE);
      argumentIndex++;
      // Next string can be an equal, or an actual parameter with equal in front
      String value = args[argumentIndex].trim();
      boolean canHaveEqualInFront = true;
      if (EQUAL_SEPARATOR.equals(value)) {
        argumentIndex++;
        value = args[argumentIndex].replace(DOUBLE_QUOTATION, Character.MIN_VALUE); //Ignore the current parameter
        canHaveEqualInFront = false;
      }
      if (value.charAt(0) == EQUAL_SIGN && !canHaveEqualInFront) {
        throw new IllegalArgumentException("Unknown value found: " + value);
      } else {
        switch (parameter) {
          case DATABASE_PARAMETER:
            databaseFilename = value;
            break;
          case CSV_PARAMETER:
            csvFilename = value;
            break;
          default:
            throw new IllegalArgumentException("Unknown value found: " + value);
        }
      }
      argumentIndex++;
    }
  }

  public void reconcileData() throws IOException, SQLException {
    List<CsvTransaction> csvTransactions = new ArrayList<>();
    CsvParser transactionCsvParser = new CsvParser();
    csvTransactions = transactionCsvParser.parseCsvFile(csvFilename);

    DatabaseConnectable databaseConnectable = new SqlLiteConnection(databaseFilename);
    ExpenseReadable expenseReadable = new ExpenseReportReader(databaseConnectable);
    try {
      List<ExpenseReport> expenseReports = expenseReadable.getExpenseTransactions();
      reconcileData(csvTransactions, ExpenseTransactionMapper.mapExpenseReportsToMap(expenseReports));
    } catch (SQLException ex) {
      LOGGER.error("Problem accessing the database. Database file location=" + databaseFilename, ex);
      throw ex;
    }

  }

  public void reconcileData(List<CsvTransaction> csvTransactions, Map<ExpenseManagerMapKey, List<ExpenseManagerTransaction>> expenseTransactionMap) {
    ExpenseReconciler.reconcileBankData(csvTransactions, expenseTransactionMap);
  }
}
