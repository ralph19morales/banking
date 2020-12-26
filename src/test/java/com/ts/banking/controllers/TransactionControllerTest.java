package com.ts.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.banking.BankingApplication;
import com.ts.banking.commands.deposit.DepositReq;
import com.ts.banking.commands.transfer.TransferReq;
import com.ts.banking.commands.withdrawal.WithdrawalReq;
import com.ts.banking.persistence.entities.Person;
import com.ts.banking.persistence.repositories.AccountRepository;
import com.ts.banking.persistence.repositories.PersonRepository;
import com.ts.banking.utils.TestingUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,
        classes = BankingApplication.class)
@AutoConfigureMockMvc
public class TransactionControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersonRepository personRepository;

    @Autowired
    private AccountRepository accountRepository;

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
    }

    @Nested
    @DisplayName("Deposit API Tests")
    class DepositApiTests {

        @DisplayName("Given valid deposit request THEN return response with balance")
        @Test
        public void whenGivenValidDepositRequest_thenReturnResponseWithBalance() throws Exception {

            Person person = TestingUtils.createPersonAndAccount("destination@gmail.com", 12345L);
            personRepository.save(person);

            mvc.perform(post("/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(DepositReq.builder()
                            .amount(BigDecimal.valueOf(100))
                            .destination(person.getAccounts().get(0).getId())
                            .build())))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.currentBalance", is(100)));
        }

        @DisplayName("Given invalid amount THEN return response with error message")
        @Test
        public void whenGivenInvalidAmount_thenThrowError() throws Exception {
            mvc.perform(post("/transaction/deposit")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(DepositReq.builder()
                            .amount(BigDecimal.ZERO)
                            .destination(1L)
                            .build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("Withdraw API Tests")
    class WithdrawApiTests {

        @DisplayName("Given valid withdraw request THEN return response with balance")
        @Test
        public void whenGivenValidWithdrawalRequest_thenReturnResponseWithBalance() throws Exception {
            Person person = TestingUtils.createPersonAndAccount("source@gmail.com", 12345L);
            personRepository.save(person);

            mvc.perform(post("/transaction/withdrawal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(WithdrawalReq.builder()
                            .amount(BigDecimal.TEN)
                            .source(person.getAccounts().get(0).getId())
                            .build())))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.currentBalance", is(-10)));
        }

        @DisplayName("Given invalid amount THEN return response with error message")
        @Test
        public void whenGivenInvalidAmount_thenThrowError() throws Exception {
            mvc.perform(post("/transaction/withdrawal")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(WithdrawalReq.builder()
                            .amount(BigDecimal.ZERO)
                            .source(1L)
                            .build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }

    @Nested
    @DisplayName("Transfer API Tests")
    class TransferApiTests {

        @DisplayName("Given valid transfer request THEN return response")
        @Test
        public void whenGivenValidTransferRequest_thenReturnResponse() throws Exception {
            Person personSource = TestingUtils.createPersonAndAccount("source@gmail.com", 12345L);
            personRepository.save(personSource);

            Person personDestination = TestingUtils.createPersonAndAccount("destination@gmail.com", 12346L);
            personRepository.save(personDestination);

            mvc.perform(post("/transaction/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(TransferReq.builder()
                            .amount(BigDecimal.TEN)
                            .destination(personDestination.getAccounts().get(0).getId())
                            .source(personSource.getAccounts().get(0).getId())
                            .build())))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.transactionType", is("TRANSFER")));
        }

        @DisplayName("Given invalid amount THEN return response with error message")
        @Test
        public void whenGivenInvalidAmount_thenThrowError() throws Exception {
            mvc.perform(post("/transaction/transfer")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(TransferReq.builder()
                            .amount(BigDecimal.ZERO)
                            .destination(2L)
                            .source(1L)
                            .build())))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }
    }
}
