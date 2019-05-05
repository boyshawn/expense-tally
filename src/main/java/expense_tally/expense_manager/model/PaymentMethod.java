package expense_tally.expense_manager.model;

/**
 * Different type of payment method for {@link ExpenseManagerTransaction}
 */
public enum PaymentMethod {
    CASH("Cash"),
    CREDIT_CARD("Credit Card"),
    ELECTRONIC_TRANSFER("Electronic Transfer"),
    NETS("NETS"),
    GIRO("Giro"),
    DEBIT_CARD("Debit 5542-2741-0014-1067"),
    EZ_LINK("Ez-link"),
    GRAY_PAY("Grab Pay");
    private String value;

    /**
     * Construct a payment method with the <i>value</i>
     * @param value string representing the payment method
     */
    PaymentMethod(String value) {
        this.value = value;
    }

    /**
     * Returns the value of this payment method
     * @return the value of this payment method
     */
    public String value() {
        return this.value;
    }

    /**
     * Returns the payment method represented by this <i>value</i>
     * @param value content of the payment method
     * @return the payment method represented by this <i>value</i>
     */
    public static PaymentMethod resolve(String value) {
        if (value == null || "".equals(value.trim())) {
            return null;
        }
        for (PaymentMethod paymentMethod : values()) {
            if (paymentMethod.value.equals(value)) {
                return paymentMethod;
            }
        }
        return null;
    }
}