package com.moassam.observation.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.observation.adapter.web.dto.ObservationGenerateRequest;
import com.moassam.observation.adapter.web.dto.ObservationRegenerateRequest;
import com.moassam.observation.adapter.web.dto.ObservationResponse;
import com.moassam.observation.adapter.web.dto.ObservationSectionResponse;
import com.moassam.observation.adapter.web.dto.PhoneConsultationResponse;
import com.moassam.observation.adapter.web.dto.SectionRegenerateRequest;
import com.moassam.observation.adapter.web.dto.SectionUpdateRequest;
import com.moassam.observation.application.provided.ObservationUseCase;
import com.moassam.observation.application.result.ObservationResult;
import com.moassam.observation.application.result.ObservationSectionResult;
import com.moassam.observation.application.result.PhoneConsultationResult;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RequestMapping("/api/v1/observations")
@RestController
public class ObservationApi {

    private final ObservationUseCase observationUseCase;

    @RequireAuth
    @PostMapping
    public SuccessResponse<ObservationResponse> generateObservation(
            @CurrentUserId Long userId,
            @RequestBody ObservationGenerateRequest request
    ) {
        ObservationResult result = observationUseCase.generateObservation(userId, request.toCommand());

        return SuccessResponse.of(ObservationResponse.from(result));
    }

    @RequireAuth
    @GetMapping("/{observationId}")
    public SuccessResponse<ObservationResponse> get(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        ObservationResult result = observationUseCase.get(userId, observationId);

        return SuccessResponse.of(ObservationResponse.from(result));
    }

    @RequireAuth
    @GetMapping("/{observationId}/sections/{sectionId}")
    public SuccessResponse<ObservationSectionResponse> getSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId
    ) {
        ObservationSectionResult result = observationUseCase.getSection(userId, observationId, sectionId);

        return SuccessResponse.of(ObservationSectionResponse.from(result));
    }

    @RequireAuth
    @PostMapping("/{observationId}/regenerate")
    public SuccessResponse<ObservationResponse> regenerate(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @RequestBody ObservationRegenerateRequest request
    ) {
        ObservationResult result = observationUseCase.regenerate(userId, observationId, request.toCommand());

        return SuccessResponse.of(ObservationResponse.from(result));
    }

    @RequireAuth
    @PostMapping("/{observationId}/sections/{sectionId}/regenerate")
    public SuccessResponse<ObservationSectionResponse> regenerateSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId,
            @RequestBody SectionRegenerateRequest request
    ) {
        ObservationSectionResult result = observationUseCase.regenerateSection(
                userId,
                observationId,
                sectionId,
                request.toCommand()
        );

        return SuccessResponse.of(ObservationSectionResponse.from(result));
    }

    @RequireAuth
    @PatchMapping("/{observationId}/sections/{sectionId}")
    public SuccessResponse<ObservationSectionResponse> updateSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId,
            @RequestBody SectionUpdateRequest request
    ) {
        ObservationSectionResult result = observationUseCase.updateSection(
                userId,
                observationId,
                sectionId,
                request.toCommand()
        );

        return SuccessResponse.of(ObservationSectionResponse.from(result));
    }

    @RequireAuth
    @PostMapping("/{observationId}/phone")
    public SuccessResponse<PhoneConsultationResponse> createPhoneConsultation(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        PhoneConsultationResult result = observationUseCase.createPhoneConsultation(userId, observationId);

        return SuccessResponse.of(PhoneConsultationResponse.from(result));
    }

    @RequireAuth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{observationId}/save")
    public void save(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        observationUseCase.save(userId, observationId);
    }
}
