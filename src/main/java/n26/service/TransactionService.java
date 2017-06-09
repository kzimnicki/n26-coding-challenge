package n26.service;

import com.google.common.collect.ImmutableMap;
import n26.domain.Statistics;
import n26.domain.Transaction;
import n26.domain.TransactionState;
import n26.utils.TimeUtils;
import org.joda.money.CurrencyUnit;
import org.joda.money.Money;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class TransactionService {

    private static final int RANGE = 60;
    private static final BigDecimal DEFAULT_DECIMAL = BigDecimal.valueOf(0.0);
    private static final Money DEFAULT_MONEY = Money.of(CurrencyUnit.EUR, DEFAULT_DECIMAL);


    private Map<Long, TransactionState> transactionStates;
    private Statistics statistics = new Statistics(0L, DEFAULT_DECIMAL, DEFAULT_DECIMAL, DEFAULT_DECIMAL, DEFAULT_DECIMAL);

    @PostConstruct
    private void init() {
        ImmutableMap.Builder<Long, TransactionState> mapBuilder = ImmutableMap.builder();
        for (long i = 0; i < RANGE; i++) {
            mapBuilder.put(i, new TransactionState());
        }
        transactionStates = mapBuilder.build();
    }


    public synchronized boolean save(Transaction transaction) {
        if (TimeUtils.isOlderThan60Seconds(transaction.getTimestampInSeconds())) {
            return false;
        }
        TransactionState state = getCircularBufferState(transaction);
        if (state.getTimestampInSeconds() != transaction.getTimestampInSeconds()) {
            state.reset();
        }
        updateState(transaction, state);
        return true;
    }

    private TransactionState getCircularBufferState(Transaction transaction) {
        long circularKey = transaction.getTimestampInSeconds() % RANGE;
        return transactionStates.get(circularKey);
    }

    public void generateStatistics() {
        List<TransactionState> states = getAllValidStates();
        Money min = states.stream().filter(state -> state.getMin().isGreaterThan(DEFAULT_MONEY)).map(TransactionState::getMin).min(Money::compareTo).orElse(DEFAULT_MONEY);
        Money max = states.stream().map(TransactionState::getMax).max(Money::compareTo).orElse(DEFAULT_MONEY);
        Money sum = states.stream().map(TransactionState::getSum).reduce(DEFAULT_MONEY, Money::plus);
        long countSum = states.stream().mapToLong(TransactionState::getCounter).sum();
        Money avg =  countSum > 0 ? sum.dividedBy(countSum, RoundingMode.HALF_EVEN) : DEFAULT_MONEY;
        statistics = new Statistics(countSum, sum.getAmount(), min.getAmount(), max.getAmount(), avg.getAmount());
    }

    private List<TransactionState> getAllValidStates() {
        return transactionStates.values().stream().filter(state -> !TimeUtils.isOlderThan60Seconds(state.getTimestampInSeconds())).collect(Collectors.toList());
    }

    public Statistics getStatistics() {
        return statistics;
    }

    public void clearStatistics() {
        transactionStates.values().forEach(TransactionState::reset);
        generateStatistics();
    }

    private void updateState(Transaction transaction, TransactionState state) {
        state.incrementCounter();
        Money amount = Money.of(CurrencyUnit.EUR, transaction.getAmount());
        state.setMax(state.getMax().isGreaterThan(amount) ? state.getMax() : amount);
        state.setMin(state.getMin().isEqual(DEFAULT_MONEY) ? amount :  state.getMin());
        state.setSum(state.getSum().plus(amount));
        generateStatistics();
    }
}