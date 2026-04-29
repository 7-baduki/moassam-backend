package com.moassam.observation.domain;

import com.moassam.shared.domain.BaseEntity;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Observation extends BaseEntity {

    private Long id;
    private Long userId;
    private String memo;
    private Age age;
    private Curriculum curriculum;
    private List<Keyword> keywords = new ArrayList<>();
    private List<ObservationSection> sections = new ArrayList<>();
    private String summaryContent;
    private String phoneConsultationContent;
    private boolean derivedContentStale;
    private ObservationStatus status;
    private LocalDateTime savedAt;

    public static Observation create(
            Long userId,
            String memo,
            Age age,
            Curriculum curriculum,
            List<Keyword> keywords
    ) {
        Observation observation = new Observation();

        observation.userId = userId;
        observation.memo = memo;
        observation.age = age;
        observation.curriculum = curriculum;
        observation.status = ObservationStatus.TEMP;
        observation.replaceKeywords(keywords);

        return observation;
    }

    public void replaceGeneratedContent(
            List<ObservationSection> sections,
            String summaryContent,
            String phoneConsultationContent
    ) {
        replaceSections(sections);
        this.summaryContent = summaryContent;
        this.phoneConsultationContent = phoneConsultationContent;
        this.derivedContentStale = false;
    }

    public void replaceGeneratedSection(
            Long sectionId,
            ObservationSection generatedSection,
            String summaryContent,
            String phoneConsultationContent
    ) {
        ObservationSection section = findSection(sectionId);

        if (!section.isSameType(generatedSection.getType())) {
            throw new IllegalArgumentException("생성된 섹션 유형이 일치하지 않습니다.");
        }

        section.replaceByRegeneratedContent(generatedSection.getContent());
        this.summaryContent = summaryContent;
        this.phoneConsultationContent = phoneConsultationContent;
        this.derivedContentStale = false;
    }

    public void editSection(Long sectionId, String content) {
        ObservationSection section = findSection(sectionId);

        section.edit(content);
        this.derivedContentStale = true;
    }

    public void replaceDerivedContent(
            String summaryContent,
            String phoneConsultationContent
    ) {
        this.summaryContent = summaryContent;
        this.phoneConsultationContent = phoneConsultationContent;
        this.derivedContentStale = false;
    }

    public void save() {
        this.status = ObservationStatus.SAVED;
        this.savedAt = LocalDateTime.now();
    }

    public boolean isOwner(Long userId) {
        return this.userId != null && this.userId.equals(userId);
    }

    public boolean isSaved() {
        return this.status == ObservationStatus.SAVED;
    }

    private void replaceKeywords(List<Keyword> keywords) {
        this.keywords.clear();

        if (keywords != null) {
            this.keywords.addAll(keywords);
        }
    }

    private void replaceSections(List<ObservationSection> sections) {
        this.sections.clear();

        if (sections == null) {
            return;
        }

        sections.forEach(section -> {
            section.assignObservation(this);
            this.sections.add(section);
        });
    }

    private ObservationSection findSection(Long sectionId) {
        return this.sections.stream()
                .filter(section -> section.isSameId(sectionId))
                .findFirst()
                .orElseThrow(() -> new IllegalArgumentException("섹션이 존재하지 않습니다."));
    }
}
