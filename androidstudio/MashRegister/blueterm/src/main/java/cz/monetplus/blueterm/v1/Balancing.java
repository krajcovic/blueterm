package cz.monetplus.blueterm.v1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author krajcovic
 *
 */
public class Balancing {

    /**
     * 
     */
    private Integer shiftNumber = 0;

    /**
     * 
     */
    private Integer batchNumber = 0;

    /**
     * 
     */
    private Integer debitCount = 0;

    /**
     * 
     */
    private Integer debitAmount = 0;

    /**
     * 
     */
    private Integer creditCount = 0;

    /**
     * 
     */
    private Integer creditAmount = 0;

    public Balancing() {
        super();
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

    public Balancing(String batchFormat) {
        Pattern pattern = Pattern
                .compile("(^\\d{3})(\\d{3})(\\d{4})([+-])(\\d{17})(\\d{4})([+-])(\\d{17})");
        Matcher matcher = pattern.matcher(batchFormat);
        if (matcher.find()) {
            this.setShiftNumber(Integer.valueOf(matcher.group(1)));
            this.setBatchNumber(Integer.valueOf(matcher.group(2)));
            this.setDebitCount(Integer.valueOf(matcher.group(3)));
            char sign = matcher.group(4).charAt(0);
            this.setDebitAmount((sign == '+' ? 1 : -1)
                    * Integer.valueOf(matcher.group(5)));
            this.setCreditCount(Integer.valueOf(matcher.group(6)));
            sign = matcher.group(7).charAt(0);
            this.setCreditAmount((sign == '+' ? 1 : -1)
                    * Integer.valueOf(matcher.group(8)));
        }
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

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append("Balancing [");
        if (shiftNumber != null) {
            builder.append("shiftNumber=").append(shiftNumber).append(", ");
        }
        if (batchNumber != null) {
            builder.append("batchNumber=").append(batchNumber).append(", ");
        }
        if (debitCount != null) {
            builder.append("debitCount=").append(debitCount).append(", ");
        }
        if (debitAmount != null) {
            builder.append("debitAmount=").append(debitAmount).append(", ");
        }
        if (creditCount != null) {
            builder.append("creditCount=").append(creditCount).append(", ");
        }
        if (creditAmount != null) {
            builder.append("creditAmount=").append(creditAmount);
        }
        builder.append("]");
        return builder.toString();
    }

}
