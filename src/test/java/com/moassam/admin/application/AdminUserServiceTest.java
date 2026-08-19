package com.moassam.admin.application;

import com.moassam.admin.application.dto.AdminUserSearchResult;
import com.moassam.admin.application.required.AdminUserQueryRepository;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.then;
import static org.mockito.Mockito.mock;

class AdminUserServiceTest {

    private final AdminUserQueryRepository adminUserQueryRepository = mock(AdminUserQueryRepository.class);
    private final AdminUserService adminUserService = new AdminUserService(adminUserQueryRepository);

    @Test
    void getUsers_withInclusiveDateRange() {
        PageRequest pageRequest = PageRequest.of(0, 20);
        given(adminUserQueryRepository.count()).willReturn(10L);
        given(adminUserQueryRepository.findAdminUserSummaries(
                LocalDateTime.of(2026, 6, 1, 0, 0),
                LocalDateTime.of(2026, 7, 1, 0, 0),
                pageRequest
        )).willReturn(Page.empty(pageRequest));

        AdminUserSearchResult result = adminUserService.getUsers(
                LocalDate.of(2026, 6, 1),
                LocalDate.of(2026, 6, 30),
                0,
                20
        );

        assertThat(result.totalUserCount()).isEqualTo(10L);
        assertThat(result.periodUserCount()).isZero();
    }

    @Test
    void getUsers_withOpenDateRange() {
        PageRequest pageRequest = PageRequest.of(1, 10);
        given(adminUserQueryRepository.count()).willReturn(3L);
        given(adminUserQueryRepository.findAdminUserSummaries(
                LocalDateTime.of(2026, 6, 25, 0, 0),
                null,
                pageRequest
        )).willReturn(Page.empty(pageRequest));

        adminUserService.getUsers(LocalDate.of(2026, 6, 25), null, 1, 10);

        then(adminUserQueryRepository).should().findAdminUserSummaries(
                LocalDateTime.of(2026, 6, 25, 0, 0),
                null,
                pageRequest
        );
    }

    @Test
    void getUsers_rejectsReversedDateRange() {
        assertThatThrownBy(() -> adminUserService.getUsers(
                LocalDate.of(2026, 7, 1),
                LocalDate.of(2026, 6, 1),
                0,
                20
        )).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void getUsers_rejectsInvalidPagination() {
        assertThatThrownBy(() -> adminUserService.getUsers(null, null, -1, 20))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> adminUserService.getUsers(null, null, 0, 0))
                .isInstanceOf(IllegalArgumentException.class);
        assertThatThrownBy(() -> adminUserService.getUsers(null, null, 0, 101))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
