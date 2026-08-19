package com.moassam.admin.application;

import com.moassam.admin.application.dto.AdminUserSearchResult;
import com.moassam.admin.application.required.AdminUserQueryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Transactional(readOnly = true)
@RequiredArgsConstructor
@Service
public class AdminUserService {

    private static final int MAX_PAGE_SIZE = 100;

    private final AdminUserQueryRepository adminUserQueryRepository;

    public AdminUserSearchResult getUsers(LocalDate startDate, LocalDate endDate, int page, int size) {
        validate(startDate, endDate, page, size);

        LocalDateTime startAt = startDate == null ? null : startDate.atStartOfDay();
        LocalDateTime endAt = endDate == null ? null : endDate.plusDays(1).atStartOfDay();

        return new AdminUserSearchResult(
                adminUserQueryRepository.count(),
                adminUserQueryRepository.findAdminUserSummaries(
                        startAt,
                        endAt,
                        PageRequest.of(page, size)
                )
        );
    }

    private void validate(LocalDate startDate, LocalDate endDate, int page, int size) {
        if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
            throw new IllegalArgumentException("startDate must not be after endDate");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page must be zero or greater");
        }
        if (size < 1 || size > MAX_PAGE_SIZE) {
            throw new IllegalArgumentException("size must be between 1 and " + MAX_PAGE_SIZE);
        }
    }
}
