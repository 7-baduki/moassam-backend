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
import com.moassam.observation.application.provided.*;
import com.moassam.observation.domain.Observation;
import com.moassam.observation.domain.ObservationSection;
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

    private final ObservationCreator observationCreator;
    private final ObservationFinder observationFinder;
    private final ObservationRegenerator observationRegenerator;
    private final ObservationSectionModifier observationSectionModifier;
    private final PhoneConsultationCreator phoneConsultationCreator;
    private final ObservationSaver observationSaver;

    @RequireAuth
    @PostMapping
    public SuccessResponse<ObservationResponse> generateObservation(
            @CurrentUserId Long userId,
            @RequestBody ObservationGenerateRequest request
    ) {
        Observation observation = observationCreator.generateObservation(userId, request.toInput());

        return SuccessResponse.of(ObservationResponse.from(observation));
    }

    @RequireAuth
    @GetMapping("/{observationId}")
    public SuccessResponse<ObservationResponse> get(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        Observation observation = observationFinder.get(userId, observationId);

        return SuccessResponse.of(ObservationResponse.from(observation));
    }

    @RequireAuth
    @GetMapping("/{observationId}/sections/{sectionId}")
    public SuccessResponse<ObservationSectionResponse> getSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId
    ) {
        ObservationSection section = observationFinder.getSection(userId, observationId, sectionId);

        return SuccessResponse.of(ObservationSectionResponse.from(section));
    }

    @RequireAuth
    @PostMapping("/{observationId}/regenerate")
    public SuccessResponse<ObservationResponse> regenerate(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @RequestBody ObservationRegenerateRequest request
    ) {
        Observation regenerated = observationRegenerator.regenerate(userId, observationId, request.toInput());

        return SuccessResponse.of(ObservationResponse.from(regenerated));
    }

    @RequireAuth
    @PostMapping("/{observationId}/sections/{sectionId}/regenerate")
    public SuccessResponse<ObservationSectionResponse> regenerateSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId,
            @RequestBody SectionRegenerateRequest request
    ) {
        ObservationSection section = observationRegenerator.regenerateSection(
                userId,
                observationId,
                sectionId,
                request.toInput()
        );

        return SuccessResponse.of(ObservationSectionResponse.from(section));
    }

    @RequireAuth
    @PatchMapping("/{observationId}/sections/{sectionId}")
    public SuccessResponse<ObservationSectionResponse> updateSection(
            @CurrentUserId Long userId,
            @PathVariable Long observationId,
            @PathVariable Long sectionId,
            @RequestBody SectionUpdateRequest request
    ) {
        ObservationSection section = observationSectionModifier.updateSection(
                userId,
                observationId,
                sectionId,
                request.toInput()
        );

        return SuccessResponse.of(ObservationSectionResponse.from(section));
    }

    @RequireAuth
    @PostMapping("/{observationId}/phone")
    public SuccessResponse<PhoneConsultationResponse> createPhoneConsultation(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        Observation observation = phoneConsultationCreator.createPhoneConsultation(userId, observationId);

        return SuccessResponse.of(PhoneConsultationResponse.from(observation));
    }

    @RequireAuth
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/{observationId}/save")
    public void save(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        observationSaver.save(userId, observationId);
    }
}
