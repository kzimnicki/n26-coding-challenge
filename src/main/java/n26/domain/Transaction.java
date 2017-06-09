package n26.domain;

import org.hibernate.validator.constraints.NotEmpty;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

public class Transaction {

    @NotEmpty
    @Min(0)
    private Long timestamp;

    @NotEmpty
    @Min(0)
    private BigDecimal amount;

    public Transaction(){
        // for spring
    }

    public Transaction(Long timestamp, BigDecimal amount) {
        this.timestamp = timestamp;
        this.amount = amount;
    }

    public Long getTimestamp() {
        return timestamp;
    }

    public Long getTimestampInSeconds() {
        return timestamp / 1000;
    }

    public void setTimestamp(Long timestamp) {
        this.timestamp = timestamp;
    }

    public BigDecimal getAmount() {
        return amount;
    }

    public void setAmount(BigDecimal amount) {
        this.amount = amount;
    }
}
