package n26.job;

import n26.service.TransactionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class StatisticJob {

    private static final Logger logger = LoggerFactory.getLogger(StatisticJob.class);


    @Autowired
    private TransactionService service;

    @Scheduled(fixedRate = 1000)
    public void scheduled() {
        logger.info("Generating statistics...");
        service.generateStatistics();
    }
}
