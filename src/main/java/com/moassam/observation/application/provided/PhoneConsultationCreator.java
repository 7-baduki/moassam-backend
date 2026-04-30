package com.moassam.observation.application.provided;

import com.moassam.observation.domain.Observation;

public interface PhoneConsultationCreator {
    Observation createPhoneConsultation(Long userId, Long observationId);

}
