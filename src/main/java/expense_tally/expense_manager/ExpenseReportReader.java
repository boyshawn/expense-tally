package expense_tally.expense_manager;

import expense_tally.expense_manager.model.ExpenseReport;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

/**
 * {@code ExpenseReportReader} provide the user ways to retrieve the
 * {@link expense_tally.expense_manager.model.ExpenseReport} from a database file.
 * <p>The current implementation only support reading from SQLite database system.</p>
 *
 * @see expense_tally.expense_manager.model.ExpenseReport
 */
public class ExpenseReportReader implements ExpenseReadable {
  private static final Logger LOGGER = LogManager.getLogger(ExpenseReportReader.class);

  private DatabaseConnectable databaseConnectable;

  /**
   * The default constructor file path of the database file
   *
   * @param databaseConnectable the database source of the expense report retrieval
   */
  public ExpenseReportReader(DatabaseConnectable databaseConnectable) {
    this.databaseConnectable = databaseConnectable;
  }

  @Override
  public List<ExpenseReport> getExpenseTransactions() throws SQLException {
    return importDataFromDatabase();
  }

  /**
   * Returns all the expenses stored in the database of expense manager application
   *
   * @return a list of expenses representing the transaction amount, catagroy, subcatagory, payment meothd,
   * description, time of expesnse, and other related field
   * @throws SQLException when there is an error accessing the database
   */
  private List<ExpenseReport> importDataFromDatabase() throws SQLException {
    // Connect to expense_tally.persistence
    try (Connection databaseConnection = databaseConnectable.connect()) {
      return importDataFromConnection(databaseConnection);
    } catch (SQLException ex) {
      LOGGER.error("Cannot read from database", ex);
      throw ex;
    }
  }

  private List<ExpenseReport> importDataFromConnection(Connection databaseConnection) throws SQLException {
    try (Statement retrieveAllStatement = databaseConnection.createStatement()) {
      return importDataFromStatement(retrieveAllStatement);
    }
  }

  private List<ExpenseReport> importDataFromStatement(Statement retrieveAllStatement) throws SQLException {
    List<ExpenseReport> expenseReports = new ArrayList<>();
    try (ResultSet retrieveAllResultSet = retrieveAllStatement.executeQuery("SELECT * FROM expense_report")) {
      while (retrieveAllResultSet.next()) {
        ExpenseReport expenseReport = new ExpenseReport();
        expenseReport.setId(retrieveAllResultSet.getInt(Column.ID.value()));
        expenseReport.setAccount(retrieveAllResultSet.getString(Column.ACCOUNT.value()));
        expenseReport.setAmount(retrieveAllResultSet.getString(Column.AMOUNT.value()));
        expenseReport.setCategory(retrieveAllResultSet.getString(Column.CATEGORY.value()));
        expenseReport.setSubcategory(retrieveAllResultSet.getString(Column.SUBCATEGORY.value()));
        expenseReport.setPaymentMethod(retrieveAllResultSet.getString(Column.PAYMENT_METHOD.value()));
        expenseReport.setDescription(retrieveAllResultSet.getString(Column.DESCRIPTION.value()));
        expenseReport.setExpensedTime(retrieveAllResultSet.getLong(Column.EXPENSED_TIME.value()));
        expenseReport.setModificationTime(retrieveAllResultSet.getLong(Column.MODIFICATION_TIME.value()));
        expenseReport.setReferenceNumber(retrieveAllResultSet.getString(Column.REFERENCE_NUMBER.value()));
        expenseReport.setStatus(retrieveAllResultSet.getString(Column.STATUS.value()));
        expenseReport.setProperty1(retrieveAllResultSet.getString(Column.PROPERTY_1.value()));
        expenseReport.setProperty2(retrieveAllResultSet.getString(Column.PROPERTY_2.value()));
        expenseReport.setProperty3(retrieveAllResultSet.getString(Column.PROPERTY_3.value()));
        expenseReport.setProperty4(retrieveAllResultSet.getString(Column.PROPERTY_4.value()));
        expenseReport.setProperty5(retrieveAllResultSet.getString(Column.PROPERTY_5.value()));
        expenseReport.setTax(retrieveAllResultSet.getString(Column.TAX.value()));
        expenseReport.setExpenseTag(retrieveAllResultSet.getString(Column.EXPENSE_TAG.value()));
        expenseReports.add(expenseReport);
      }
    }
    return expenseReports;
  }

  public enum Column {
    ID ("_id"),
    ACCOUNT ("account"),
    AMOUNT ("amount"),
    CATEGORY ("category"),
    SUBCATEGORY ("subcategory"),
    PAYMENT_METHOD ("payment_method"),
    DESCRIPTION ("description"),
    EXPENSED_TIME ("expensed"),
    MODIFICATION_TIME ("modified"),
    REFERENCE_NUMBER ("reference_number"),
    STATUS ("status"),
    PROPERTY_1 ("property"),
    PROPERTY_2 ("property2"),
    PROPERTY_3 ("property3"),
    PROPERTY_4 ("property4"),
    PROPERTY_5 ("property5"),
    TAX ("tax"),
    EXPENSE_TAG ("expense_tag");

    private String value;

    private Column(String value) {
      this.value = value;
    }

    public String value() {
      return value;
    }
  }
}
