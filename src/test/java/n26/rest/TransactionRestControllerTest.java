package n26.rest;

import n26.Application;
import n26.domain.Transaction;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.mock.http.MockHttpOutputMessage;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.web.context.WebApplicationContext;

import java.io.IOException;
import java.math.BigDecimal;
import java.nio.charset.Charset;
import java.time.Instant;
import java.util.Arrays;

import static org.junit.Assert.*;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.setup.MockMvcBuilders.*;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
public class TransactionRestControllerTest {

    private static final String EMPTY_STRING = "";


    private static final MediaType CONTENT_TYPE = new MediaType(MediaType.APPLICATION_JSON.getType(),
            MediaType.APPLICATION_JSON.getSubtype(),
            Charset.forName("utf8"));

    private MockMvc mockMvc;
    private HttpMessageConverter mappingJackson2HttpMessageConverter;

    @Autowired
    private WebApplicationContext webApplicationContext;

    @Autowired
    void setConverters(HttpMessageConverter<?>[] converters) {

        this.mappingJackson2HttpMessageConverter = Arrays.asList(converters).stream()
                .filter(hmc -> hmc instanceof MappingJackson2HttpMessageConverter)
                .findAny()
                .orElse(null);

        assertNotNull("the JSON message converter must not be null",
                this.mappingJackson2HttpMessageConverter);
    }

    @Before
    public void setup() throws Exception {
        this.mockMvc = webAppContextSetup(webApplicationContext).build();
        clearStatistics();

    }

    @Test
    public void shouldPost1TransactionsAndReturnCorrectStatus() throws Exception {
        Transaction transaction1 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(12.0));

        ResultActions resultActions1 = postTransaction(transaction1);

        resultActions1.andExpect(status().is(201));
        resultActions1.andExpect(content().string(EMPTY_STRING));
    }


    @Test
    public void shouldPost2TransactionsAndReturnCorrectStatistics() throws Exception {
        Transaction transaction1 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(12.55));
        Transaction transaction2 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(14.0));

        postTransaction(transaction1);
        postTransaction(transaction2);

        getStatistics().andExpect(jsonPath("$.count", is(2)));
        getStatistics().andExpect(jsonPath("$.max", is(14.0)));
        getStatistics().andExpect(jsonPath("$.min", is(12.55)));
        getStatistics().andExpect(jsonPath("$.sum", is(26.55)));
        getStatistics().andExpect(jsonPath("$.avg", is(13.28))); //rounding half even
    }

    @Test
    public void shouldPost2TransactionsAfterHalfMinuteAndReturnCorrectStatistics() throws Exception {
        Transaction transaction1 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(12.55));

        postTransaction(transaction1);
        sleepHalfMinute();
        Transaction transaction2 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(14.0));
        postTransaction(transaction2);
        sleepHalfMinute();

        getStatistics().andExpect(jsonPath("$.count", is(1)));
        getStatistics().andExpect(jsonPath("$.max", is(14.0)));
        getStatistics().andExpect(jsonPath("$.min", is(14.00)));
        getStatistics().andExpect(jsonPath("$.sum", is(14.00)));
        getStatistics().andExpect(jsonPath("$.avg", is(14.00)));
    }

    @Test
    public void shouldPost1TransactionsAndReturnEmptyStatisticsAfter1Minute() throws Exception {
        Transaction transaction1 = new Transaction(Instant.now().toEpochMilli(), BigDecimal.valueOf(12.55));

        postTransaction(transaction1);
        sleepOneMinute();

        ResultActions statistics = getStatistics();
        statistics.andExpect(jsonPath("$.count", is(0)));
        statistics.andExpect(jsonPath("$.max", is(0.0)));
        statistics.andExpect(jsonPath("$.min", is(0.0)));
        statistics.andExpect(jsonPath("$.sum", is(0.0)));
        statistics.andExpect(jsonPath("$.avg", is(0.0))); //rounding half even
    }


    @Test
    public void shouldPost1TransactionWithOldTimestamp() throws Exception {
        Transaction transaction = new Transaction(Instant.now().toEpochMilli() - (61 * 1000), BigDecimal.valueOf(12.1));

        ResultActions resultActions = postTransaction(transaction);

        resultActions.andExpect(status().is(204));
        resultActions.andExpect(content().string(EMPTY_STRING));
        getStatistics().andExpect(jsonPath("$.count", is(0)));
    }

    @Test
    public void shouldReturnEmptyStatisticsForMissingTransactions() throws Exception {
        ResultActions statistics = getStatistics();

        statistics.andExpect(jsonPath("$.count", is(0)));
        statistics.andExpect(jsonPath("$.max", is(0.0)));
        statistics.andExpect(jsonPath("$.min", is(0.0)));
        statistics.andExpect(jsonPath("$.sum", is(0.0)));
        statistics.andExpect(jsonPath("$.avg", is(0.0)));
    }

    private String json(Object o) throws IOException {
        MockHttpOutputMessage mockHttpOutputMessage = new MockHttpOutputMessage();
        this.mappingJackson2HttpMessageConverter.write(
                o, MediaType.APPLICATION_JSON, mockHttpOutputMessage);
        return mockHttpOutputMessage.getBodyAsString();
    }

    private ResultActions postTransaction(Transaction transaction) throws Exception {
        return mockMvc.perform(post("/transactions")
                .content(this.json(transaction))
                .contentType(CONTENT_TYPE));
    }

    private ResultActions getStatistics() throws Exception {
        return mockMvc.perform(get("/statistics")
                .contentType(CONTENT_TYPE));
    }

    private ResultActions clearStatistics() throws Exception {
        return mockMvc.perform(delete("/statistics")
                .contentType(CONTENT_TYPE));
    }

    private void sleepOneMinute() throws InterruptedException {
        Thread.sleep(61 * 1000);
    }
    private void sleepHalfMinute() throws InterruptedException {
        Thread.sleep(31 * 1000);
    }

}