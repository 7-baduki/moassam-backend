package com.moassam.observation.adapter.reference;

import com.moassam.observation.application.required.ObservationReferenceProvider;
import com.moassam.observation.domain.CurriculumType;
import com.moassam.observation.domain.ObservationReference;
import com.moassam.observation.domain.SectionType;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@Component
@RequiredArgsConstructor
public class FileObservationReferenceProvider implements ObservationReferenceProvider {

    private final ResourceLoader resourceLoader;

    @Override
    public List<ObservationReference> getObservationReferences(CurriculumType curriculumType, List<SectionType> sectionTypes) {
        return sectionTypes.stream()
                .map(sectionType -> loadReference(curriculumType, sectionType))
                .toList();
    }

    private ObservationReference loadReference(CurriculumType curriculumType, SectionType sectionType) {
        String path = buildPath(curriculumType, sectionType);
        Resource resource = resourceLoader.getResource(path);

        if (!resource.exists()) {
            throw new IllegalStateException("관찰일지 참고자료 파일이 존재하지 않습니다. path=" + path);
        }

        try {
            String content = resource.getContentAsString(StandardCharsets.UTF_8);

            return new ObservationReference(curriculumType, sectionType, content);
        } catch (IOException e) {
            throw new IllegalStateException("관찰일지 참고자료 파일을 읽을 수 없습니다. path =" + path, e);
        }
    }

    private String buildPath(CurriculumType curriculumType, SectionType sectionType) {
        return "classpath:observation-references/%s/%s.md".formatted(
                curriculumDirectory(curriculumType),
                sectionFileName(sectionType)
        );
    }

    private String curriculumDirectory(CurriculumType curriculumType) {
        return switch (curriculumType){
            case NURI -> "nuri";
            case STANDARD -> "standard";
        };
    }

    private String sectionFileName(SectionType sectionType) {
        return switch (sectionType){
            case PHYSICAL_HEALTH -> "physical_health";
            case COMMUNICATION -> "communication";
            case SOCIAL_RELATIONSHIP -> "social_relationship";
            case ART_EXPERIENCE -> "art_experience";
            case NATURE_EXPLORATION -> "nature_exploration";
        };
    }
}
