package com.moassam.observation.adapter.integration.ai;

import com.moassam.observation.application.command.KeywordCommand;
import com.moassam.observation.application.command.ObservationGenerateCommand;
import com.moassam.observation.domain.SectionType;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.stream.Collectors;

@Component
public class OpenAiPromptTemplate {

    public String systemBase() {
        return """
                당신은 유아교육기관 교사의 관찰일지 작성을 돕는 전문가입니다.
                입력으로 주어진 관찰 상황, 나이, 교육과정, 교육과정 영역, 키워드를 바탕으로 관찰일지를 작성합니다.
                
                반드시 입력으로 주어진 교육과정 영역별로만 관찰 문장을 생성합니다.
                sections[].type은 입력으로 주어진 SectionType enum 문자열(COMMUNITY, SOCIAL, SCIENCE, ART, PHYSICAL) 중 하나를 그대로 사용합니다.
                각 영역의 문장은 유아교육 전문 용어를 사용하되, 보호자가 이해할 수 있는 자연스러운 문장으로 작성합니다.
                관찰하지 않은 사실을 추측하거나 추가하지 않습니다.
                
                응답은 반드시 JSON 형식만 출력합니다.
                마크다운 코드블록, 설명 문장, 추가 텍스트는 출력하지 않습니다.
                
                JSON 형식:
                {
                  "sections": [
                    {
                      "type": "SectionType enum",
                      "content": "관찰 문장"
                    }
                  ],
                  "summaryContent": "전체 관찰 내용을 종합한 총평",
                  "phoneConsultationContent": "총평을 기반으로 전화 상담 시 보호자에게 전달하기 좋은 표현"
                }
                """;
    }

    public String observationBase(ObservationGenerateCommand command) {
        return """
                관찰 상황: %s
                나이: 만 %d세
                교육과정: %s
                생성할 교육과정 영역:
                %s
                
                키워드:
                %s
                """.formatted(
                command.memo(),
                command.age().toYearsOld(),
                command.curriculum().name(),
                formatSectionTypes(command.sectionTypes()),
                formatKeywords(command.keywords())
        );
    }

    private String formatSectionTypes(List<SectionType> sectionTypes) {
        if (sectionTypes == null || sectionTypes.isEmpty()) {
            return "없음";
        }

        return sectionTypes.stream()
                .map(sectionType -> "- %s".formatted(
                        sectionType.name()
                ))
                .collect(Collectors.joining("\n"));
    }

    private String formatKeywords(List<KeywordCommand> keywords) {
        if (keywords == null || keywords.isEmpty()) {
            return "없음";
        }

        return keywords.stream()
                .map(keyword -> "- %s: %s".formatted(
                        keyword.type().name(),
                        keyword.value()
                ))
                .collect(Collectors.joining("\n"));
    }
}
