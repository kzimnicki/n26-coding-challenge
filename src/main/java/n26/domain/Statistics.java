package n26.domain;

import java.math.BigDecimal;

public class Statistics {

    private long count;
    private BigDecimal sum;
    private BigDecimal min;
    private BigDecimal max;
    private BigDecimal avg;

    public Statistics(long count, BigDecimal sum, BigDecimal min, BigDecimal max, BigDecimal avg) {
        this.min = min;
        this.max = max;
        this.sum = sum;
        this.avg = avg;
        this.count = count;
    }


    public long getCount() {
        return count;
    }

    public void setCount(long count) {
        this.count = count;
    }

    public BigDecimal getSum() {
        return sum;
    }

    public void setSum(BigDecimal sum) {
        this.sum = sum;
    }

    public BigDecimal getMin() {
        return min;
    }

    public void setMin(BigDecimal min) {
        this.min = min;
    }

    public BigDecimal getMax() {
        return max;
    }

    public void setMax(BigDecimal max) {
        this.max = max;
    }

    public BigDecimal getAvg() {
        return avg;
    }

    public void setAvg(BigDecimal avg) {
        this.avg = avg;
    }
}
