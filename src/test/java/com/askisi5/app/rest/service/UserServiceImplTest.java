package com.askisi5.app.rest.service;

import com.askisi5.app.rest.dto.UserRequest;
import com.askisi5.app.rest.dto.UserResponse;
import com.askisi5.app.rest.exception.ResourceNotFoundException;
import com.askisi5.app.rest.model.User;
import com.askisi5.app.rest.repository.UserRepository;
import com.askisi5.app.rest.service.impl.UserServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @Mock
    private UserRepository userRepository;

    private UserServiceImpl userService;

    private static final long USER_ID = 1L;
    private static final Date PAST_DATE = new Date(0L); // 1970-01-01, always in the past

    @BeforeEach
    void setUp() {
        userService = new UserServiceImpl(userRepository);
    }

    private User sampleUser() {
        User user = new User();
        user.setId(USER_ID);
        user.setFirstName("Ada");
        user.setLastName("Lovelace");
        user.setStreet("123 Analytical Engine Rd");
        user.setCity("London");
        user.setPostalCode(12345);
        user.setCountry("UK");
        user.setPhoneNumber("+441234567890");
        user.setBirthdayDate(PAST_DATE);
        user.setSex("F");
        return user;
    }

    private UserRequest sampleRequest() {
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

    @Nested
    @DisplayName("getAllUsers")
    class GetAllUsers {

        @Test
        @DisplayName("maps every entity returned by the repository to a response DTO")
        void returnsAllUsersMapped() {
            when(userRepository.findAll()).thenReturn(Arrays.asList(sampleUser(), sampleUser()));

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).hasSize(2);
            assertThat(result.get(0).getFirstName()).isEqualTo("Ada");
        }

        @Test
        @DisplayName("returns an empty list when there are no users")
        void returnsEmptyListWhenNoUsers() {
            when(userRepository.findAll()).thenReturn(Collections.emptyList());

            List<UserResponse> result = userService.getAllUsers();

            assertThat(result).isEmpty();
        }
    }

    @Nested
    @DisplayName("getUserById")
    class GetUserById {

        @Test
        @DisplayName("returns the mapped user when it exists")
        void returnsUserWhenFound() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser()));

            UserResponse result = userService.getUserById(USER_ID);

            assertThat(result.getId()).isEqualTo(USER_ID);
            assertThat(result.getLastName()).isEqualTo("Lovelace");
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the id does not exist")
        void throwsWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.getUserById(999L))
                    .isInstanceOf(ResourceNotFoundException.class)
                    .hasMessageContaining("999");
        }
    }

    @Nested
    @DisplayName("createUser")
    class CreateUser {

        @Test
        @DisplayName("saves and returns the new user")
        void createsUser() {
            when(userRepository.save(any(User.class))).thenReturn(sampleUser());

            UserResponse result = userService.createUser(sampleRequest());

            assertThat(result.getFirstName()).isEqualTo("Ada");
            ArgumentCaptor<User> captor = ArgumentCaptor.forClass(User.class);
            verify(userRepository).save(captor.capture());
            // id must never be settable from the request DTO - it's server-generated.
            assertThat(captor.getValue().getId()).isEqualTo(0L);
        }
    }

    @Nested
    @DisplayName("updateUser")
    class UpdateUser {

        @Test
        @DisplayName("updates and returns the user when it exists")
        void updatesUserWhenFound() {
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(sampleUser()));
            when(userRepository.save(any(User.class))).thenAnswer(invocation -> invocation.getArgument(0));

            UserRequest request = sampleRequest();
            request.setCity("Manchester");

            UserResponse result = userService.updateUser(USER_ID, request);

            assertThat(result.getCity()).isEqualTo("Manchester");
            assertThat(result.getId()).isEqualTo(USER_ID);
            verify(userRepository, times(1)).save(any(User.class));
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the user does not exist")
        void throwsWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.updateUser(999L, sampleRequest()))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).save(any(User.class));
        }
    }

    @Nested
    @DisplayName("deleteUser")
    class DeleteUser {

        @Test
        @DisplayName("deletes the user when it exists")
        void deletesUserWhenFound() {
            User user = sampleUser();
            when(userRepository.findById(USER_ID)).thenReturn(Optional.of(user));

            userService.deleteUser(USER_ID);

            verify(userRepository).delete(user);
        }

        @Test
        @DisplayName("throws ResourceNotFoundException when the user does not exist")
        void throwsWhenNotFound() {
            when(userRepository.findById(999L)).thenReturn(Optional.empty());

            assertThatThrownBy(() -> userService.deleteUser(999L))
                    .isInstanceOf(ResourceNotFoundException.class);

            verify(userRepository, never()).delete(any(User.class));
        }
    }
}
