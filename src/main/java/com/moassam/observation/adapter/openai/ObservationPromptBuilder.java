package com.moassam.observation.adapter.openai;

import com.moassam.observation.domain.Age;
import com.moassam.observation.domain.CurriculumType;
import com.moassam.observation.domain.ObservationReference;
import com.moassam.observation.domain.SectionType;
import org.springframework.stereotype.Component;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ObservationPromptBuilder {

    public String buildInstructions() {
        return """
                당신은 어린이집 및 유치원에서 사용하는 영아, 유아 관찰일지를 작성하는 보육, 교육 전문가입니다.
                
                아이의 행동을 객관적으로 관찰하고, 신체운동건강, 의사소통, 사회관계, 예술경험, 자연탐구의 5개 영역을 고려하여 해석합니다.
                관찰 사실을 바탕으로 판단이나 평가를 지양하고, 중립적이며 전문적인 문장으로 관찰일지를 작성합니다.
                
                본 관찰일지는 누리과정 및 표준보육과정의 발달 영역 기준을 기반으로 작성합니다.
                영아 및 유아의 발달 수준과 특성을 고려하여 연령에 적합한 행동으로 해석하세요.
                
                다음 요소를 고려해 해석하세요.
                - 또래 상호작용, 놀이 참여도, 감정 표현, 의사소통 방식
                - 신체 활동, 언어 발달, 사회성 발달, 정서 발달
                - 새로운 환경에 대한 적응, 교사 및 친구와의 관계 형성
                - 특정 상황에서의 행동 변화. 예: 낯선 환경, 갈등 상황 등
                
                작성 기준:
                1. 관찰 사실을 기반으로 작성하되, 과도한 추측은 지양하세요.
                2. 긍정적이고 중립적인 표현을 사용하세요.
                3. 평가나 단정적인 표현은 피하고, "보인다", "관찰된다", "시도하는 모습이 나타난다" 등의 표현을 사용하세요.
                4. 문장은 너무 길지 않게 구성하세요.
                5. 누리과정 및 표준보육과정의 발달 목표와 내용을 참고하여 해석하세요.
                6. 연령에 따른 일반적인 발달 특성을 반영하여 자연스럽게 서술하세요.
                7. 유아교육 및 보육 현장에서 사용하는 전문 용어를 적절히 활용하세요.
                8. 문장은 "행동 관찰 > 행동에 대한 해석 > 발달 맥락 연결" 구조를 따르세요.
                9. 단순 행동 나열이 아니라, 행동의 의미와 맥락을 함께 해석하세요.
                10. 발달 영역과 연결하여 해석하세요.
                
                출력 기준:
                - 불필요한 설명 없이 관찰일지 내용만 작성하세요.
                - 자연스럽고 전문적인 문장 형태로 작성하세요.
                - 각 관찰 영역별 content는 하나의 문단으로 작성하세요.
                - 자주 사용할 수 있는 전문 용어: 상호작용, 소근육 조절, 심미적 탐색, 표상하기, 병행놀이
                - 반드시 요청된 JSON 스키마를 따르세요.
                """;
    }

    public String buildInput(
            Age age,
            CurriculumType curriculumType,
            String situation,
            List<SectionType> sectionTypes,
            List<ObservationReference> observationReferences
    ) {
        String requestedSections = sectionTypes.stream()
                .sorted(Comparator.comparing(SectionType::getDisplayOrder))
                .map(this::sectionDisplayName)
                .collect(Collectors.joining(", "));

        String referenceType = observationReferences.stream()
                .sorted(Comparator.comparing(reference -> reference.sectionType().getDisplayOrder()))
                .map(reference -> """
                        [관찰 영역: %s]
                        %s
                        """.formatted(sectionDisplayName(reference.sectionType()), reference.content()))
                .collect(Collectors.joining("\n"));

        return """
                입력 데이터:
                - 대상 연령: %s
                - 교육과정: %s
                - 관찰 영역: %s
                - 관찰 내용:
                %s
                
                도메인 맥락 및 참고자료:
                %s
                
                생성 요구사항:
                - title은 관찰 내용의 핵심이 드러나도록 30자 이내로 작성하세요.
                - summary는 전체 관찰 내용을 1~2문장으로 요약하세요.
                - sections는 요청된 관찰 영역마다 1개씩 작성하세요.
                - sectionType은 반드시 요청된 enum 값을 그대로 사용하세요.
                - content는 해당 영역의 관찰일지 내용만 하나의 문단으로 작성하세요.
                - 관찰 사실, 행동 해석, 발달 맥락 연결이 자연스럽게 포함되도록 작성하세요.
                - 교사가 입력한 원문에 없는 행동이나 발화를 새로 만들지 마세요.
                """.formatted(
                age.name(),
                curriculumType.name(),
                requestedSections,
                situation,
                referenceType
        );
    }

    private String sectionDisplayName(SectionType sectionType) {
        return switch (sectionType) {
            case PHYSICAL_HEALTH -> "신체운동건강";
            case COMMUNICATION -> "의사소통";
            case SOCIAL_RELATIONSHIP -> "사회관계";
            case ART_EXPERIENCE -> "예술경험";
            case NATURE_EXPLORATION -> "자연탐구";
        };
    }
}
