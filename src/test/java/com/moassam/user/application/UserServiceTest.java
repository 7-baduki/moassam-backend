package com.moassam.user.application;

import com.moassam.shared.exception.BusinessException;
import com.moassam.support.UserFixture;
import com.moassam.user.application.required.UserRepository;
import com.moassam.user.domain.User;
import com.moassam.user.exception.UserErrorCode;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    void updateNickname() {
        User user = UserFixture.create();
        given(userRepository.findById(1L)).willReturn(Optional.of(user));

        User result = userService.updateNickname(1L, "새닉네임");

        assertThat(result.getNickname()).isEqualTo("새닉네임");
    }

    @Test
    void updateNickname_userNotFound() {
        given(userRepository.findById(999L)).willReturn(Optional.empty());

        assertThatThrownBy(() -> userService.updateNickname(999L, "새닉네임"))
                .isInstanceOf(BusinessException.class)
                .extracting("errorCode")
                .isEqualTo(UserErrorCode.USER_NOT_FOUND);
    }
}