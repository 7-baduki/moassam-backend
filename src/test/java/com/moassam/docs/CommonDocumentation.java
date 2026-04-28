package com.moassam.docs;

import org.springframework.restdocs.payload.FieldDescriptor;
import org.springframework.restdocs.payload.JsonFieldType;

import static org.springframework.restdocs.payload.PayloadDocumentation.fieldWithPath;

public class CommonDocumentation {

    public static FieldDescriptor[] successResponseFields(FieldDescriptor... dataFields) {
        FieldDescriptor[] base = {
                fieldWithPath("data").type(JsonFieldType.OBJECT).description("응답 데이터")
        };

        FieldDescriptor[] result = new FieldDescriptor[base.length + dataFields.length];
        System.arraycopy(base, 0, result, 0, base.length);
        System.arraycopy(dataFields, 0, result, base.length, dataFields.length);
        return result;
    }

    public static FieldDescriptor[] errorResponseFields() {
        return new FieldDescriptor[]{
                fieldWithPath("type").type(JsonFieldType.STRING).description("에러 타입"),
                fieldWithPath("title").type(JsonFieldType.STRING).description("HTTP 상태 메시지"),
                fieldWithPath("status").type(JsonFieldType.NUMBER).description("HTTP 상태 코드"),
                fieldWithPath("detail").type(JsonFieldType.STRING).description("에러 상세 메시지"),
                fieldWithPath("instance").type(JsonFieldType.STRING).description("요청 URI"),
                fieldWithPath("properties").type(JsonFieldType.OBJECT).description("추가 정보"),
                fieldWithPath("properties.timestamp").type(JsonFieldType.STRING).description("에러 발생 시간"),
                fieldWithPath("properties.code").type(JsonFieldType.STRING).description("에러 코드")
        };
    }
}