package com.minair.minair.config;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
/**
 * 갑자기 LocalDate 타입이 직력화가 안되서... 그냥 직접 커스텀해서 LocalDate타입 serialize 만들어줌
 * 블로그랑 조금 다르긴하지만 기본적으로 DATE_FORMAT을 DateTimeFormatter.ofPattern()로 정의해줌
 * 그리고 원하는 타입을 JsonSerializer의 제네릭 타입으로 정의하고 상속받아 확장해줌.
 * serialize 메서드 오버라이드해주면 LocalDate타입을 jackson이 만날때 마다 해당 메서드를 통해
 * 직렬화해줌(objectMapper에 등록을 해줘야 하지만 뒤에서 설명함 일단 이러함) JsonGenerator에 DATE_FORMAT패턴으로 포맷팅
 * 역직렬화 역시 같은 원리로 진행후 objectMapper에 만든 역,직렬화 클래스를 등록해줘야 하기에 objectMapper를 상속받아
 * 커스텀 objectMapper클래스를 만들어 SimpleModule안에 역,직렬화 클래스 넣어주고 모듈에 등록하는 원리임.
 * */
@Configuration
public class JacksonConfig {

    public static final DateTimeFormatter LOCALDATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    public static final DateTimeFormatter LOCALTIME_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    public static class LocalDateSerializer extends JsonSerializer<LocalDate> {

        @Override
        public void serialize(LocalDate value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            //gen.writeString(value.format(FORMATTER));
            gen.writeString(LOCALDATE_FORMAT.format(value));
        }
    }

    public static class LocalDateDeserializer extends JsonDeserializer<LocalDate> {

        @Override
        public LocalDate deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalDate.parse(p.getText(), LOCALDATE_FORMAT);
        }
    }

    public static class LocalTimeSerializer extends JsonSerializer<LocalTime> {

        @Override
        public void serialize(LocalTime value, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            gen.writeString(LOCALTIME_FORMAT.format(value));
        }
    }

    public static class LocalTimeDeserializer extends JsonDeserializer<LocalTime> {

        @Override
        public LocalTime deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
            return LocalTime.parse(p.getText(), LOCALTIME_FORMAT);
        }
    }

    public static class CustomObjectMapper1 extends ObjectMapper {

        public CustomObjectMapper1() {

            SimpleModule simpleModule = new SimpleModule();
            simpleModule.addSerializer(LocalDate.class,new LocalDateSerializer());
            simpleModule.addDeserializer(LocalDate.class,new LocalDateDeserializer());
            simpleModule.addSerializer(LocalTime.class,new LocalTimeSerializer());
            simpleModule.addDeserializer(LocalTime.class,new LocalTimeDeserializer());


            registerModule(simpleModule);
        }
    }

}
