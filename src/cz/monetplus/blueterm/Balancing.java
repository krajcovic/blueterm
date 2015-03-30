package cz.monetplus.blueterm;

public class Balancing {
    private Integer shiftNumber = 0;
    private Integer batchNumber = 0;
    private Integer debitCount = 0;
    private Integer debitAmount = 0;
    private Integer creditCount = 0;
    private Integer creditAmount = 0;

    public Balancing() {
        super();
        // TODO Auto-generated constructor stub
    }

    public Balancing(Integer shiftNumber, Integer batchNumber,
            Integer debitsCount, Integer debitsAmount, Integer creditCount,
            Integer debitAmount) {
        super();
        this.shiftNumber = shiftNumber;
        this.batchNumber = batchNumber;
        this.debitCount = debitsCount;
        this.debitAmount = debitsAmount;
        this.creditCount = creditCount;
        this.creditAmount = debitAmount;
    }

    public Integer getShiftNumber() {
        return shiftNumber;
    }

    public void setShiftNumber(Integer shiftNumber) {
        this.shiftNumber = shiftNumber;
    }

    public Integer getBatchNumber() {
        return batchNumber;
    }

    public void setBatchNumber(Integer batchNumber) {
        this.batchNumber = batchNumber;
    }

    public Integer getDebitCount() {
        return debitCount;
    }

    public void setDebitCount(Integer debitCount) {
        this.debitCount = debitCount;
    }

    public Integer getDebitAmount() {
        return debitAmount;
    }

    public void setDebitAmount(Integer debitAmount) {
        this.debitAmount = debitAmount;
    }

    public Integer getCreditCount() {
        return creditCount;
    }

    public void setCreditCount(Integer creditCount) {
        this.creditCount = creditCount;
    }

    public Integer getCreditAmount() {
        return creditAmount;
    }

    public void setCreditAmount(Integer creditAmount) {
        this.creditAmount = creditAmount;
    }

}
