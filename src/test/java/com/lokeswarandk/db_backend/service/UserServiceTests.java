package com.lokeswarandk.db_backend.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import com.lokeswarandk.db_backend.dto.request.UpsertUserRequest;
import com.lokeswarandk.db_backend.exception.ResourceNotFoundException;
import com.lokeswarandk.db_backend.model.User;
import com.lokeswarandk.db_backend.repository.UserRepository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class UserServiceTests {

    @Mock private UserRepository userRepository;

    @InjectMocks private UserService userService;

    @Test
    void create_setsIdNullAndCreatedAtBeforeSave() {
        UpsertUserRequest request = validRequest();
        when(userRepository.save(any(User.class)))
                .thenAnswer(
                        invocation -> {
                            User saved = invocation.getArgument(0);
                            assertThat(saved.getId()).isNull();
                            assertThat(saved.getCreatedAt()).isNotNull();
                            saved.setId(1L);
                            return saved;
                        });

        var response = userService.create(request);

        verify(userRepository).save(any(User.class));
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void findById_returnsUserResponseWhenFound() {
        when(userRepository.findById(1L)).thenReturn(Optional.of(sampleUser(1L)));

        var response = userService.findById(1L);

        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Jane Doe");
    }

    @Test
    void findById_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.findById(99L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("No user with id 99 exists");
    }

    @Test
    void findByMobileNo_throwsWhenBlank() {
        assertThatThrownBy(() -> userService.findByMobileNo(" "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("mobile is required");
    }

    @Test
    void searchMobileNosByPrefix_returnsWrappedResults() {
        when(userRepository.findDistinctMobileNosByPrefix("98", 5))
                .thenReturn(List.of("9876543210", "9876543211"));

        var response = userService.searchMobileNosByPrefix("98", null);

        assertThat(response.getMobileNos()).containsExactly("9876543210", "9876543211");
    }

    @Test
    void searchMobileNosByPrefix_throwsWhenPrefixTooShort() {
        assertThatThrownBy(() -> userService.searchMobileNosByPrefix("9", null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("prefix must be at least 2 characters");
    }

    @Test
    void searchMobileNosByPrefix_throwsWhenLimitTooLow() {
        assertThatThrownBy(() -> userService.searchMobileNosByPrefix("98", 0))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("limit must be at least 1");
    }

    @Test
    void update_appliesFieldsAndSaves() {
        User existing = sampleUser(1L);
        when(userRepository.findById(1L)).thenReturn(Optional.of(existing));
        when(userRepository.save(existing)).thenReturn(existing);

        UpsertUserRequest request = validRequest();
        request.setName("updated name");

        var response = userService.update(1L, request);

        assertThat(existing.getName()).isEqualTo("updated name");
        assertThat(response.getName()).isEqualTo("updated name");
        verify(userRepository).save(existing);
    }

    @Test
    void update_throwsWhenNotFound() {
        when(userRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> userService.update(99L, validRequest()))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void deleteById_deletesWhenExists() {
        when(userRepository.existsById(1L)).thenReturn(true);

        userService.deleteById(1L);

        verify(userRepository).deleteById(1L);
    }

    @Test
    void deleteById_throwsWhenNotFound() {
        when(userRepository.existsById(99L)).thenReturn(false);

        assertThatThrownBy(() -> userService.deleteById(99L))
                .isInstanceOf(ResourceNotFoundException.class);

        verify(userRepository, never()).deleteById(eq(99L));
    }

    private static UpsertUserRequest validRequest() {
        UpsertUserRequest request = new UpsertUserRequest();
        request.setName("Jane Doe");
        request.setMobileNo("9876543210");
        request.setAddressLine("123 Main Street");
        request.setLocality("Springfield");
        request.setState("KA");
        request.setCountry("India");
        request.setPincode("560001");
        return request;
    }

    private static User sampleUser(Long id) {
        User user = new User();
        user.setId(id);
        user.setName("Jane Doe");
        user.setMobileNo("9876543210");
        user.setAddressLine("123 Main Street");
        user.setLocality("Springfield");
        user.setState("KA");
        user.setCountry("India");
        user.setPincode("560001");
        user.setCreatedAt(LocalDateTime.parse("2026-05-31T12:00:00"));
        return user;
    }
}
