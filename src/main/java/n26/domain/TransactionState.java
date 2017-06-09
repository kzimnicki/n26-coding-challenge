package n26.domain;

import org.joda.money.CurrencyUnit;
import org.joda.money.Money;

import java.math.BigDecimal;
import java.time.Instant;

public class TransactionState {
    private static final BigDecimal DEFAULT_DECIMAL = BigDecimal.valueOf(0.0);
    private static final Money DEFAULT_VALUE = Money.of(CurrencyUnit.EUR, DEFAULT_DECIMAL);

    private long counter;
    private long timestampInSeconds;
    private Money sum;
    private Money max;
    private Money min;
    private Money avg;


    public Money getSum() {
        return sum;
    }

    public void setSum(Money sum) {
        this.sum = sum;
    }

    public Money getMax() {
        return max;
    }

    public void setMax(Money max) {
        this.max = max;
    }

    public Money getMin() {
        return min;
    }

    public void setMin(Money min) {
        this.min = min;
    }

    public Money getAvg() {
        return avg;
    }

    public void setAvg(Money avg) {
        this.avg = avg;
    }

    public TransactionState() {
        this.reset();
    }

    public long getCounter() {
        return counter;
    }

    public long getTimestampInSeconds() {
        return timestampInSeconds;
    }

    public void incrementCounter(){
        this.counter++;
    }


    public void reset(){
        this.counter = 0;
        this.timestampInSeconds = Instant.now().getEpochSecond();
        this.max = DEFAULT_VALUE;
        this.min = DEFAULT_VALUE;
        this.avg = DEFAULT_VALUE;
        this.sum = DEFAULT_VALUE;
    }
}
