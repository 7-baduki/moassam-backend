package com.moassam.observation.adapter.web;

import com.moassam.auth.adapter.web.annotation.CurrentUserId;
import com.moassam.auth.adapter.web.annotation.RequireAuth;
import com.moassam.observation.adapter.web.dto.ObservationCreateResponse;
import com.moassam.observation.adapter.web.dto.ObservationDetailResponse;
import com.moassam.observation.adapter.web.dto.ObservationListResponse;
import com.moassam.observation.application.provided.ObservationCreator;
import com.moassam.observation.application.provided.ObservationDeleter;
import com.moassam.observation.application.provided.ObservationFinder;
import com.moassam.observation.application.provided.ObservationRegenerator;
import com.moassam.observation.domain.ObservationCreateRequest;
import com.moassam.observation.domain.ObservationDetail;
import com.moassam.observation.domain.ObservationListDetail;
import com.moassam.shared.web.SuccessResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/observations")
@RequiredArgsConstructor
public class ObservationApi {

    private final ObservationCreator observationCreator;
    private final ObservationRegenerator observationRegenerator;
    private final ObservationFinder observationFinder;
    private final ObservationDeleter observationDeleter;

    @RequireAuth
    @PostMapping
    public SuccessResponse<ObservationCreateResponse> createObservation(
            @CurrentUserId Long userId,
            @RequestBody ObservationCreateRequest request
    ) {
        Long observationId = observationCreator.createObservation(userId, request);

        return SuccessResponse.of(new ObservationCreateResponse(observationId));
    }

    @RequireAuth
    @PostMapping("{observationId}/regenerate")
    public SuccessResponse<ObservationDetailResponse> regenerateObservation(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        ObservationDetail observationDetail = observationRegenerator.regenerateObservation(userId, observationId);

        return SuccessResponse.of(ObservationDetailResponse.from(observationDetail));
    }

    @RequireAuth
    @GetMapping("/{observationId}")
    public SuccessResponse<ObservationDetailResponse> getObservation(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        ObservationDetail observationDetail = observationFinder.getObservation(userId, observationId);

        return SuccessResponse.of(ObservationDetailResponse.from(observationDetail));
    }

    @RequireAuth
    @GetMapping
    public SuccessResponse<ObservationListResponse> getObservationList(
            @CurrentUserId Long userId,
            @RequestParam(required = false) Long cursor,
            @RequestParam(defaultValue = "20") int size
    ) {
        ObservationListDetail observationList = observationFinder.getObservationList(userId, cursor, size);

        return SuccessResponse.of(ObservationListResponse.from(observationList));
    }


    @RequireAuth
    @DeleteMapping("/{observationId}")
    public SuccessResponse<Void> deleteObservation(
            @CurrentUserId Long userId,
            @PathVariable Long observationId
    ) {
        observationDeleter.deleteObservation(userId, observationId);

        return SuccessResponse.of(null);
    }
}
