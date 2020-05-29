package expense_tally.csv_parser.model;

import expense_tally.csv_parser.CsvTransaction;
import expense_tally.csv_parser.MasterCard;
import expense_tally.csv_parser.MonetaryAmountException;
import expense_tally.csv_parser.TransactionType;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MasterCardTest {

  @Test
  void from_success() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_nullBankTransactionDate() {
    // This is now an invalid test case. The only way to initialise MasterCard is from CsvTransaction, and
    // CsvTransaction can't have null transaction date. Transitively, there won't be a null transaction date as well.
  }

  /**
   * In this test, the reference 1 of the CsvTransaction is empty string, so we will not expect the change in the
   * transaction date.
   */
  @Test
  void extractTransactionDate_emptyReference1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void extractTransactionDate_noDateInReference1() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "TAPAS SI NG",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void extractTransactionDate_dateConversion() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 01, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2018, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_noCardNumber() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            null,
            LocalDate.of(2019, 12, 20),
            4.55,
            0.00,
            "TAPAS SI NG 20DEC",
            "",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void from_invalidCardNumber() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("MasterCard number is invalid.");
  }

  @Test
  void from_noTransactionType() throws MonetaryAmountException {
    // Create CsvTransaction
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, null, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CsvTransaction is not of MasterCard type.");
  }

  @Test
  void from_wrongTransactionType() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.PAY_NOW, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5632-4172-5981-4347")
        .build();
    assertThatThrownBy(() -> MasterCard.from(testCsvTransaction))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("CsvTransaction is not of MasterCard type.");
  }

  @Test
  void from_noDateInRef1() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("   a    ")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    assertThat(MasterCard.from(testCsvTransaction))
        .isNotNull()
        .extracting("cardNumber",
            "transactionDate",
            "debitAmount",
            "creditAmount",
            "transactionRef1",
            "transactionRef2",
            "transactionRef3",
            "transactionType")
        .contains(
            "5132-4172-5981-4347",
            LocalDate.of(2019, 12, 27),
            4.55,
            0.00,
            "   a    ",
            "5132-4172-5981-4347",
            "",
            TransactionType.MASTERCARD
        );
  }

  @Test
  void toString_test() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testCsvTransaction);
    assertThat(masterCard.toString())
        .isNotBlank()
        .isEqualTo("MasterCard[CsvTransaction[transactionDate=2019-12-20, debitAmount=4.55, creditAmount=0.0, transactionRef1='TAPAS SI NG 20DEC', transactionRef2='5132-4172-5981-4347', transactionRef3='', type=TransactionType[value='MST']], cardNumber=5132-4172-5981-4347]");
  }

  @Test
  void equals_sameObject() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testCsvTransaction);
    MasterCard masterCardDifferenceReference = masterCard;
    assertThat(masterCard.equals(masterCardDifferenceReference)).isTrue();
  }

  @Test
  void equals_null() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testCsvTransaction);
    MasterCard masterCardDifferenceReference = masterCard;
    assertThat(masterCard.equals(null)).isFalse();
  }

  @Test
  void equals_differentClass() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testCsvTransaction);
    assertThat(masterCard.equals(testCsvTransaction)).isFalse();
  }

  @Test
  void equals_differentBySuperClassComparison() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction1 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    CsvTransaction testCsvTransaction2 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI ")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard1 = MasterCard.from(testCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testCsvTransaction2);
    assertThat(masterCard1.equals(testCsvTransaction2)).isFalse();
  }

  @Test
  void equals_differentCardNumber() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction1 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    CsvTransaction testCsvTransaction2 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4346")
        .build();
    MasterCard masterCard1 = MasterCard.from(testCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testCsvTransaction2);
    assertThat(masterCard1.equals(masterCard2)).isFalse();
  }

  @Test
  void equals_allFieldsAreSame() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction1 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();

    CsvTransaction testCsvTransaction2 = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard1 = MasterCard.from(testCsvTransaction1);
    MasterCard masterCard2 = MasterCard.from(testCsvTransaction2);
    assertThat(masterCard1.equals(masterCard2)).isTrue();
  }

  @Test
  void hashCode_same() throws MonetaryAmountException {
    LocalDate transactionDate = LocalDate.of(2019, 12, 27);
    CsvTransaction testCsvTransaction = new CsvTransaction.Builder(transactionDate, TransactionType.MASTERCARD, 4.55)
        .transactionRef1("TAPAS SI NG 20DEC")
        .transactionRef2("5132-4172-5981-4347")
        .build();
    MasterCard masterCard = MasterCard.from(testCsvTransaction);
    assertThat(masterCard.hashCode())
        .isNotZero()
        .isEqualByComparingTo(Objects.hashCode(masterCard));
  }
}