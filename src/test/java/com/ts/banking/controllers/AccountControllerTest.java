package com.ts.banking.controllers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.ts.banking.BankingApplication;
import com.ts.banking.persistence.entities.Person;
import com.ts.banking.persistence.repositories.PersonRepository;
import com.ts.banking.services.AccountService;
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

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = WebEnvironment.MOCK,
        classes = BankingApplication.class)
@AutoConfigureMockMvc
public class AccountControllerTest {

    @Autowired
    private MockMvc mvc;

    @Autowired
    private ObjectMapper mapper;

    @Autowired
    private PersonRepository personRepository;

    @AfterEach
    public void cleanup() {
        personRepository.deleteAll();
    }

    @Nested
    @DisplayName("Account API Tests")
    class AccountApiTests {

        @DisplayName("Given valid account id THEN return response")
        @Test
        public void whenGivenValidAccountId_thenReturnResponseWithBalance() throws Exception {

            Person person = TestingUtils.createPersonAndAccount("person@gmail.com", 12345L);
            personRepository.save(person);

            mvc.perform(get("/account/{accountId}", person.getAccounts().get(0).getId().toString()))
                    .andExpect(status().isOk())
                    .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.balance", is(0)))
                    .andExpect(jsonPath("$.data.name", is("Kobe Bryant")));
        }

        @DisplayName("Given invalid amount THEN return response with error message")
        @Test
        public void whenGivenInvalidAccountId_thenThrowError() throws Exception {
            mvc.perform(get("/account/{accountId}", 1))
                    .andExpect(status().isBadRequest())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON));
        }

        @DisplayName("Given valid request THEN return response")
        @Test
        public void whenGivenValidRequest_thenReturnResponseWithAccountSummary() throws Exception {

            mvc.perform(post("/account/create")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(mapper.writeValueAsString(AccountService.CreateAccountReq.builder()
                            .firstName("Kobe")
                            .lastName("Bryant")
                            .email("kb24@gmail.com")
                            .build())))
                    .andExpect(status().isOk())
                    .andExpect(content()
                            .contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                    .andExpect(jsonPath("$.data.balance", is(0)))
                    .andExpect(jsonPath("$.data.name", is("Kobe Bryant")));
        }
    }
}
