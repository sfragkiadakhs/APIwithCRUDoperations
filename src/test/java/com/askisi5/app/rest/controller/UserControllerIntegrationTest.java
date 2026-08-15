package com.askisi5.app.rest.controller;

import com.askisi5.app.rest.dto.UserRequest;
import com.askisi5.app.rest.model.User;
import com.askisi5.app.rest.repository.UserRepository;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Calendar;
import java.util.Date;

import static org.assertj.core.api.Assertions.assertThat;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * End-to-end tests for {@link UserController}, exercised through MockMvc
 * against a real (in-memory H2) database via {@code @SpringBootTest}.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class UserControllerIntegrationTest {

    private static final String BASE_URL = "/api/v1/users";
    private static final Date PAST_DATE = new Date(0L); // 1970-01-01

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    private UserRequest validRequest() {
        UserRequest request = new UserRequest();
        request.setFirstName("Ada");
        request.setLastName("Lovelace");
        request.setStreet("123 Analytical Engine Rd");
        request.setCity("London");
        request.setPostalCode(12345);
        request.setCountry("UK");
        request.setPhoneNumber("+441234567890");
        request.setBirthdayDate(PAST_DATE);
        request.setSex("F");
        return request;
    }

    private User persistUser() {
        User user = new User();
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setStreet("123 Analytical Engine Rd");
        user.setCity("London");
        user.setPostalCode(12345);
        user.setCountry("UK");
        user.setPhoneNumber("+441234567890");
        user.setBirthdayDate(PAST_DATE);
        user.setSex("F");
        return userRepository.save(user);
    }

    @Test
    @DisplayName("GET /api/v1/users returns an empty array when there are no users")
    void getAllUsers_empty() throws Exception {
        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(content().json("[]"));
    }

    @Test
    @DisplayName("GET /api/v1/users returns every persisted user")
    void getAllUsers_returnsAll() throws Exception {
        persistUser();
        persistUser();

        mockMvc.perform(get(BASE_URL))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    @DisplayName("POST /api/v1/users with valid data creates the user and returns 201")
    void createUser_valid_returnsCreated() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.firstName").value("Ada"))
                .andExpect(jsonPath("$.id").isNumber());

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/users ignores a client-supplied id and always assigns one server-side")
    void createUser_ignoresClientSuppliedId() throws Exception {
        String payloadWithId = "{\"id\":999,\"firstName\":\"Ada\",\"lastName\":\"Lovelace\"," +
                "\"postalCode\":12345,\"sex\":\"F\"}";

        String responseBody = mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(payloadWithId))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        long returnedId = objectMapper.readTree(responseBody).get("id").asLong();

        assertThat(returnedId).isNotEqualTo(999L);
        assertThat(userRepository.findById(999L)).isEmpty();
        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    @DisplayName("POST /api/v1/users with a blank first name returns 400 with a field error")
    void createUser_blankFirstName_returnsBadRequest() throws Exception {
        UserRequest request = validRequest();
        request.setFirstName("");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.firstName").exists());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("POST /api/v1/users with a postal code below the minimum returns 400")
    void createUser_invalidPostalCodeTooLow_returnsBadRequest() throws Exception {
        UserRequest request = validRequest();
        request.setPostalCode(42);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.postalCode").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users with a postal code above the maximum returns 400")
    void createUser_invalidPostalCodeTooHigh_returnsBadRequest() throws Exception {
        UserRequest request = validRequest();
        request.setPostalCode(999999);

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.postalCode").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users with an invalid phone number format returns 400")
    void createUser_invalidPhoneNumber_returnsBadRequest() throws Exception {
        UserRequest request = validRequest();
        request.setPhoneNumber("not-a-phone-number");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.phoneNumber").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users with an invalid sex value returns 400")
    void createUser_invalidSex_returnsBadRequest() throws Exception {
        UserRequest request = validRequest();
        request.setSex("X");

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.sex").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users with a birthday in the future returns 400")
    void createUser_futureBirthday_returnsBadRequest() throws Exception {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.YEAR, 1);

        UserRequest request = validRequest();
        request.setBirthdayDate(calendar.getTime());

        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.birthdayDate").exists());
    }

    @Test
    @DisplayName("POST /api/v1/users with malformed JSON returns 400 instead of 500")
    void createUser_malformedJson_returnsBadRequest() throws Exception {
        mockMvc.perform(post(BASE_URL)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\": \"Ada\", "))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns the user when it exists")
    void getUserById_found() throws Exception {
        User saved = persistUser();

        mockMvc.perform(get(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isOk())
                // cast to int: JsonPath's default provider (json-smart) parses whole
                // numbers as Integer, and Long.equals(Integer) is always false in Java.
                .andExpect(jsonPath("$.id").value((int) saved.getId()));
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} returns 404 with an error body when the user does not exist")
    void getUserById_notFound() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("GET /api/v1/users/{id} with a non-numeric id returns 400 instead of 500")
    void getUserById_nonNumericId_returnsBadRequest() throws Exception {
        mockMvc.perform(get(BASE_URL + "/{id}", "not-a-number"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} updates the user when it exists")
    void updateUser_found_returnsUpdated() throws Exception {
        User saved = persistUser();

        UserRequest update = validRequest();
        update.setCity("Manchester");

        mockMvc.perform(put(BASE_URL + "/{id}", saved.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(update)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.city").value("Manchester"));

        assertThat(userRepository.findById(saved.getId())).isPresent()
                .get()
                .extracting(User::getCity)
                .isEqualTo("Manchester");
    }

    @Test
    @DisplayName("PUT /api/v1/users/{id} returns 404 when the user does not exist")
    void updateUser_notFound() throws Exception {
        mockMvc.perform(put(BASE_URL + "/{id}", 999)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(validRequest())))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} removes the user when it exists")
    void deleteUser_found_returnsNoContent() throws Exception {
        User saved = persistUser();

        mockMvc.perform(delete(BASE_URL + "/{id}", saved.getId()))
                .andExpect(status().isNoContent());

        assertThat(userRepository.count()).isEqualTo(0);
    }

    @Test
    @DisplayName("DELETE /api/v1/users/{id} returns 404 when the user does not exist")
    void deleteUser_notFound() throws Exception {
        mockMvc.perform(delete(BASE_URL + "/{id}", 999))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.error").exists());
    }
}
