package expense_tally.csv_parser.exception;

public class MonetaryAmountException extends Exception {
  public MonetaryAmountException(String message) {
    super(message);
  }
}