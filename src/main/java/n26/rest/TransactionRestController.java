package n26.rest;

import n26.domain.Statistics;
import n26.domain.Transaction;
import n26.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;

@RestController
public class TransactionRestController {

    @Autowired
    private TransactionService transactionService;

    @RequestMapping(method = RequestMethod.GET, name = "/statistics")
    public Statistics getStatistics() {
        return transactionService.getStatistics();
    }

    @RequestMapping(method = RequestMethod.DELETE, name = "/statistics")
    public void clearStatistics() {
        transactionService.clearStatistics();
    }

    @RequestMapping(method = RequestMethod.POST, name = "/transactions")
    public void transactions(@RequestBody Transaction transaction, HttpServletResponse response) {
        if (transactionService.save(transaction)) {
            response.setStatus(201);
        } else {
            response.setStatus(204);
        }

    }
}
