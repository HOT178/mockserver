package org.mockserver.serialization.deserializers.body;

import com.google.common.collect.ImmutableMap;
import org.apache.commons.text.StringEscapeUtils;
import org.junit.Test;
import org.mockserver.matchers.MatchType;
import org.mockserver.model.*;
import org.mockserver.serialization.ObjectMapperFactory;
import org.mockserver.serialization.model.*;

import javax.xml.bind.DatatypeConverter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static java.nio.charset.StandardCharsets.UTF_8;
import static junit.framework.TestCase.assertEquals;
import static org.mockserver.character.Character.NEW_LINE;
import static org.mockserver.model.JsonBody.json;
import static org.mockserver.model.NottableString.not;
import static org.mockserver.model.RegexBody.regex;
import static org.mockserver.model.StringBody.exact;

public class BodyDTODeserializerTest {

    @Test
    public void shouldParseJsonBodyWithInvalidType() throws IOException {
        // given
        String json = ("{\"httpRequest\":{\"body\":{\"type\":\"info\",\"body\":[{\"info\":{\"address\":\"192.168.0.0\",\"os\":\"Windows\",\"name\":\"HOMEPC\",\"version\":\"XP SP3\",\"key\":\"WINXPSP3H\"}}],\"timestamp\":\"2020-02-19T08:09:32.802\"}}}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO().setBody(new JsonBodyDTO(json("{\n" +
                    "  \"type\" : \"info\",\n" +
                    "  \"body\" : [ {\n" +
                    "    \"info\" : {\n" +
                    "      \"address\" : \"192.168.0.0\",\n" +
                    "      \"os\" : \"Windows\",\n" +
                    "      \"name\" : \"HOMEPC\",\n" +
                    "      \"version\" : \"XP SP3\",\n" +
                    "      \"key\" : \"WINXPSP3H\"\n" +
                    "    }\n" +
                    "  } ],\n" +
                    "  \"timestamp\" : \"2020-02-19T08:09:32.802\"\n" +
                    "}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithMissingTypeFromBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"regex\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO().setBody(new RegexBodyDTO(regex("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithMissingValueFromBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"STRING\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithWrongFieldInBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"wrong_name\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithWrongValueFieldTypeInStringBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : 1" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("1")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithWrongValueFieldTypeInBinaryBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"BINARY\"," + NEW_LINE +
            "            \"binary\" : 1" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO(new BinaryBody(new byte[0])))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithWrongValueFieldTypeInParameterBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : 1" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody()))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithWrongTypeFieldTypeInBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : 1," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO().setBody(BodyDTO.createDTO(exact("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithEmptyContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"\"," + NEW_LINE +
            "            \"json\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(json("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithInvalidContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"invalid_value\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithEmptyCharset() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"charset\" : \"\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithInvalidCharset() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"charset\" : \"invalid_value\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonObjectWithoutMatchingValueFieldAsJsonBodyDTO() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"\"," + NEW_LINE +
            "            \"foo\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(json("{" + NEW_LINE +
                        "  \"contentType\" : \"\"," + NEW_LINE +
                        "  \"foo\" : \"some_value\"" + NEW_LINE +
                        "}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonArrayWithoutMatchingValueFieldAsJsonBodyDTO() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : [{" + NEW_LINE +
            "            \"contentType\" : \"\"," + NEW_LINE +
            "            \"foo\" : \"some_value\"" + NEW_LINE +
            "        }]" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(json("[ {" + NEW_LINE +
                        "  \"contentType\" : \"\"," + NEW_LINE +
                        "  \"foo\" : \"some_value\"" + NEW_LINE +
                        "} ]")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseStringAsJsonBodyDTO() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : \"some_value\"" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(exact("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParametersBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"),
                        new Parameter("parameterTwoName", "parameterTwoValue")
                    )))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParametersBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"),
                        new Parameter("parameterTwoName", "parameterTwoValue")
                    ), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParametersBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO((ParameterBody) new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"),
                        new Parameter("parameterTwoName", "parameterTwoValue")
                    ).withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParametersBodyWithNotParameter() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "      \"parameters\" : [ {" + NEW_LINE +
            "        \"name\" : \"!parameterOneName\"," + NEW_LINE +
            "        \"values\" : [ \"!parameterOneValueOne\", \"!parameterOneValueTwo\" ]" + NEW_LINE +
            "      }, {" + NEW_LINE +
            "        \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "        \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "      } ]" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(
                        new ParameterBody(
                            new Parameter(not("parameterOneName"), not("parameterOneValueOne"), not("parameterOneValueTwo")),
                            new Parameter("parameterTwoName", "parameterTwoValue")
                        )
                    ))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParametersBodyWithNotParameterWithExclamationMark() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"!parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"!parameterOneValueOne\", \"!parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        ExpectationDTO expected = new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(
                        new ParameterBody(
                            new Parameter(not("parameterOneName"), not("parameterOneValueOne"), not("parameterOneValueTwo")),
                            new Parameter("parameterTwoName", "parameterTwoValue")
                        )
                    ))
            );
        assertEquals(expected, expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyAsString() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : \"some_value\"" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyWithContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"text/plain; charset=utf-8\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", MediaType.PLAIN_TEXT_UTF_8)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyWithCharset() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"charset\" : \"" + StandardCharsets.ISO_8859_1 + "\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", MediaType.PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.ISO_8859_1))))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyUsingStringPropertyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyUsingStringPropertyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO((StringBody) new StringBody("some_value").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithExactStringBodyUsingStringProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, true, null)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyAsExact() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : false" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, false, null)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyWithContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true," + NEW_LINE +
            "            \"contentType\" : \"text/plain; charset=utf-8\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, true, MediaType.PLAIN_TEXT_UTF_8)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyWithCharset() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"charset\" : \"" + StandardCharsets.ISO_8859_1 + "\"," + NEW_LINE +
            "            \"string\" : \"some_value\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", MediaType.PLAIN_TEXT_UTF_8.withCharset(StandardCharsets.ISO_8859_1))))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, true, null)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyUsingStringPropertyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, true, null), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyUsingStringPropertyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO((StringBody) new StringBody("some_value", null, true, null).withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithSubStringBodyUsingStringProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"STRING\"," + NEW_LINE +
            "            \"string\" : \"some_value\"," + NEW_LINE +
            "            \"subString\" : true" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new StringBodyDTO(new StringBody("some_value", null, true, null)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithRegexBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"regex\" : \"some[a-zA-Z]*\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new RegexBodyDTO(new RegexBody("some[a-zA-Z]*")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithRegexBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"regex\" : \"some[a-zA-Z]*\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new RegexBodyDTO(new RegexBody("some[a-zA-Z]*"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithRegexBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"regex\" : \"some[a-zA-Z]*\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new RegexBodyDTO((RegexBody) new RegexBody("some[a-zA-Z]*").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithRegexBodyUsingRegexProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"REGEX\"," + NEW_LINE +
            "            \"regex\" : \"some[a-zA-Z]*\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new RegexBodyDTO(new RegexBody("some[a-zA-Z]*")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO((JsonBody) new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyUsingJsonProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"JSON\"," + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyAsObject() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"JSON\"," + NEW_LINE +
            "      \"json\" : {" + NEW_LINE +
            "        \"employees\" : [ {" + NEW_LINE +
            "          \"firstName\" : \"John\"," + NEW_LINE +
            "          \"lastName\" : \"Doe\"" + NEW_LINE +
            "        } ]" + NEW_LINE +
            "      }" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{" + NEW_LINE +
                        "  \"employees\" : [ {" + NEW_LINE +
                        "    \"firstName\" : \"John\"," + NEW_LINE +
                        "    \"lastName\" : \"Doe\"" + NEW_LINE +
                        "  } ]" + NEW_LINE +
                        "}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyAsArray() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"JSON\"," + NEW_LINE +
            "      \"json\" : [ {" + NEW_LINE +
            "        \"firstName\" : \"John\"," + NEW_LINE +
            "        \"lastName\" : \"Doe\"" + NEW_LINE +
            "      } ]" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("[ {" + NEW_LINE +
                        "  \"firstName\" : \"John\"," + NEW_LINE +
                        "  \"lastName\" : \"Doe\"" + NEW_LINE +
                        "} ]")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithEmptyArray() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"JSON\"," + NEW_LINE +
            "      \"json\" : {" + NEW_LINE +
            "        \"emptyArray\" : \"[]\"" + NEW_LINE +
            "      }" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{" + NEW_LINE +
                        "  \"emptyArray\" : \"[]\"" + NEW_LINE +
                        "}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithSimpleEmptyArray() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : [ ]" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("[ ]")))
            ), expectationDTO);

    }

    @Test
    public void shouldParseJsonWithJsonBodyWithEmptyObject() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : { }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{ }")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyAsObjectFieldAsString() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\": {" + NEW_LINE +
            "            \"type\": \"JSON\"," + NEW_LINE +
            "            \"json\": \"{\\\"context\\\":[{\\\"source\\\":\\\"DECISION_REQUEST\\\"},{\\\"source\\\":\\\"DECISION_REQUEST\\\"},{\\\"source\\\":\\\"DECISION_REQUEST\\\"}]}\"," + NEW_LINE +
            "            \"matchType\" : \"ONLY_MATCHING_FIELDS\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{\"context\":[{\"source\":\"DECISION_REQUEST\"},{\"source\":\"DECISION_REQUEST\"},{\"source\":\"DECISION_REQUEST\"}]}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyAsObjectFieldAsObject() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"JSON\"," + NEW_LINE +
            "      \"json\" : {" + NEW_LINE +
            "        \"context\" : [ {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        }, {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        }, {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        } ]" + NEW_LINE +
            "      }" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{" + NEW_LINE +
                        "  \"context\" : [ {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  }, {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  }, {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  } ]" + NEW_LINE +
                        "}")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyAsObjectFieldAsArray() throws IOException {
        // given
        String json = "{" + NEW_LINE +
            "  \"httpRequest\" : {" + NEW_LINE +
            "    \"body\" : {" + NEW_LINE +
            "      \"type\" : \"JSON\"," + NEW_LINE +
            "      \"json\" : [ {" + NEW_LINE +
            "        \"context\" : [ {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        }, {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        }, {" + NEW_LINE +
            "          \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
            "        } ]" + NEW_LINE +
            "      } ]" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }" + NEW_LINE +
            "}";

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("[ {" + NEW_LINE +
                        "  \"context\" : [ {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  }, {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  }, {" + NEW_LINE +
                        "    \"source\" : \"DECISION_REQUEST\"" + NEW_LINE +
                        "  } ]" + NEW_LINE +
                        "} ]")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithMatchTypeAndContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"JSON\"," + NEW_LINE +
            "            \"matchType\" : \"STRICT\"," + NEW_LINE +
            "            \"contentType\" : \"application/json; charset=utf-8\"," + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}", null, MediaType.JSON_UTF_8, MatchType.STRICT)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonBodyWithContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"JSON\"," + NEW_LINE +
            "            \"charset\" : \"" + StandardCharsets.ISO_8859_1 + "\"," + NEW_LINE +
            "            \"json\" : \"{'employees':[{'firstName':'John', 'lastName':'Doe'}]}\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonBodyDTO(new JsonBody("{'employees':[{'firstName':'John', 'lastName':'Doe'}]}", null, MediaType.JSON_UTF_8.withCharset(StandardCharsets.ISO_8859_1), MatchType.ONLY_MATCHING_FIELDS)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonSchemaBodyWithNot() throws IOException {
        // given
        String jsonSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://json-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"jsonSchema\" : \"" + StringEscapeUtils.escapeJava(jsonSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonSchemaBodyDTO(new JsonSchemaBody(jsonSchema), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonSchemaBodyWithOptional() throws IOException {
        // given
        String jsonSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://json-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"jsonSchema\" : \"" + StringEscapeUtils.escapeJava(jsonSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonSchemaBodyDTO((JsonSchemaBody) new JsonSchemaBody(jsonSchema).withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonSchemaBodyWithoutType() throws IOException {
        // given
        String jsonSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://json-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"jsonSchema\" : \"" + StringEscapeUtils.escapeJava(jsonSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonSchemaBodyDTO(new JsonSchemaBody(jsonSchema)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonSchemaBodyUsingJsonProperty() throws IOException {
        // given
        String jsonSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://json-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"JSON_SCHEMA\"," + NEW_LINE +
            "            \"jsonSchema\" : \"" + StringEscapeUtils.escapeJava(jsonSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonSchemaBodyDTO(new JsonSchemaBody(jsonSchema)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonSchemaBodyWithParameterStyles() throws IOException {
        // given
        String jsonSchema = "{" + NEW_LINE +
            "  \"type\" : \"object\"," + NEW_LINE +
            "  \"properties\" : {" + NEW_LINE +
            "    \"id\" : {" + NEW_LINE +
            "      \"type\" : \"integer\"" + NEW_LINE +
            "    }" + NEW_LINE +
            "  }," + NEW_LINE +
            "  \"required\" : [ \"id\" ]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {\"type\":\"JSON_SCHEMA\",\"jsonSchema\":" + jsonSchema + ",\"parameterStyles\":{\"pipeDelimitedParameter\":\"PIPE_DELIMITED\"}}" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
                .setHttpRequest(
                    new HttpRequestDTO()
                        .setBody(new JsonSchemaBodyDTO(new JsonSchemaBody(jsonSchema)
                            .withParameterStyles(ImmutableMap.of("pipeDelimitedParameter", ParameterStyle.PIPE_DELIMITED))
                        ))),
            expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonPathBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"jsonPath\" : \"\\\\some\\\\path\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonPathBodyDTO(new JsonPathBody("\\some\\path")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonPathBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"jsonPath\" : \"\\\\some\\\\path\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonPathBodyDTO(new JsonPathBody("\\some\\path"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonPathBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"jsonPath\" : \"\\\\some\\\\path\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonPathBodyDTO((JsonPathBody) new JsonPathBody("\\some\\path").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithJsonPathBodyUsingXpathProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"JSON_PATH\"," + NEW_LINE +
            "            \"jsonPath\" : \"\\\\some\\\\path\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new JsonPathBodyDTO(new JsonPathBody("\\some\\path")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlSchemaBodyWithNot() throws IOException {
        // given
        String xmlSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://xml-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"xmlSchema\" : \"" + StringEscapeUtils.escapeJava(xmlSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlSchemaBodyDTO(new XmlSchemaBody(xmlSchema), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlSchemaBodyWithOptional() throws IOException {
        // given
        String xmlSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://xml-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"xmlSchema\" : \"" + StringEscapeUtils.escapeJava(xmlSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlSchemaBodyDTO((XmlSchemaBody) new XmlSchemaBody(xmlSchema).withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlSchemaBodyWithoutType() throws IOException {
        // given
        String xmlSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://xml-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"xmlSchema\" : \"" + StringEscapeUtils.escapeJava(xmlSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlSchemaBodyDTO(new XmlSchemaBody(xmlSchema)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlSchemaBodyUsingJsonProperty() throws IOException {
        // given
        String xmlSchema = "{" + NEW_LINE +
            "    \"$schema\": \"http://xml-schema.org/draft-04/schema#\"," + NEW_LINE +
            "    \"title\": \"Product\"," + NEW_LINE +
            "    \"description\": \"A product from Acme's catalog\"," + NEW_LINE +
            "    \"type\": \"object\"," + NEW_LINE +
            "    \"properties\": {" + NEW_LINE +
            "        \"id\": {" + NEW_LINE +
            "            \"description\": \"The unique identifier for a product\"," + NEW_LINE +
            "            \"type\": \"integer\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }," + NEW_LINE +
            "    \"required\": [\"id\"]" + NEW_LINE +
            "}";
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"XML_SCHEMA\"," + NEW_LINE +
            "            \"xmlSchema\" : \"" + StringEscapeUtils.escapeJava(xmlSchema) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        final ExpectationDTO expected = new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlSchemaBodyDTO(new XmlSchemaBody(xmlSchema)))
            );
        assertEquals(expected, expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXPathBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"xpath\" : \"\\\\some\\\\xpath\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XPathBodyDTO(new XPathBody("\\some\\xpath")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXPathBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"xpath\" : \"\\\\some\\\\xpath\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XPathBodyDTO(new XPathBody("\\some\\xpath"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXPathBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"xpath\" : \"\\\\some\\\\xpath\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XPathBodyDTO((XPathBody) new XPathBody("\\some\\xpath").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXPathBodyUsingXpathProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"XPATH\"," + NEW_LINE +
            "            \"xpath\" : \"\\\\some\\\\xpath\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XPathBodyDTO(new XPathBody("\\some\\xpath")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>"), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO((XmlBody) new XmlBody("<some><xml></xml></some>").withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyWithContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"text/xml; charset=utf-8\"," + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.XML_UTF_8)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyWithCharset() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"charset\" : \"" + StandardCharsets.US_ASCII + "\"," + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>", MediaType.APPLICATION_XML_UTF_8.withCharset(StandardCharsets.US_ASCII))))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithXmlBodyUsingXpathProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"XPATH\"," + NEW_LINE +
            "            \"xml\" : \"<some><xml></xml></some>\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new XmlBodyDTO(new XmlBody("<some><xml></xml></some>")))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithBinaryBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"base64Bytes\" : \"" + DatatypeConverter.printBase64Binary("some_value".getBytes(UTF_8)) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO(new BinaryBody("some_value".getBytes(UTF_8))))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithBinaryBodyWithNot() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"not\" : true," + NEW_LINE +
            "            \"base64Bytes\" : \"" + DatatypeConverter.printBase64Binary("some_value".getBytes(UTF_8)) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO(new BinaryBody("some_value".getBytes(UTF_8)), true))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithBinaryBodyWithOptional() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"optional\" : true," + NEW_LINE +
            "            \"base64Bytes\" : \"" + DatatypeConverter.printBase64Binary("some_value".getBytes(UTF_8)) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO((BinaryBody) new BinaryBody("some_value".getBytes(UTF_8)).withOptional(true)))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithBinaryBodyWithContentType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"contentType\" : \"" + MediaType.ANY_VIDEO_TYPE + "\"," + NEW_LINE +
            "            \"base64Bytes\" : \"" + DatatypeConverter.printBase64Binary("some_value".getBytes(UTF_8)) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        ExpectationDTO expected = new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO(new BinaryBody("some_value".getBytes(UTF_8), MediaType.ANY_VIDEO_TYPE)))
            );
        assertEquals(expected, expectationDTO);
    }

    @Test
    public void shouldParseJsonWithBinaryBodyUsingBytesProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"BINARY\"," + NEW_LINE +
            "            \"base64Bytes\" : \"" + DatatypeConverter.printBase64Binary("some_value".getBytes(UTF_8)) + "\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new BinaryBodyDTO(new BinaryBody("some_value".getBytes(UTF_8))))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParameterBodyWithoutType() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"),
                        new Parameter("parameterTwoName", "parameterTwoValue")
                    )))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParameterBodyInWrongOrder() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\" ]" + NEW_LINE +
            "            } ]," + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne")
                    )))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithParameterBodyUsingParametersProperty() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"parameters\" : [ {" + NEW_LINE +
            "                    \"name\" : \"parameterOneName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }, {" + NEW_LINE +
            "                    \"name\" : \"parameterTwoName\"," + NEW_LINE +
            "                    \"values\" : [ \"parameterTwoValue\" ]" + NEW_LINE +
            "            } ]" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody(
                        new Parameter("parameterOneName", "parameterOneValueOne", "parameterOneValueTwo"),
                        new Parameter("parameterTwoName", "parameterTwoValue")
                    )))
            ), expectationDTO);
    }

    @Test
    public void shouldParseJsonWithInvalidArrayParameterBody() throws IOException {
        // given
        String json = ("{" + NEW_LINE +
            "    \"httpRequest\": {" + NEW_LINE +
            "        \"body\" : {" + NEW_LINE +
            "            \"type\" : \"PARAMETERS\"," + NEW_LINE +
            "            \"wrong_field\" : {" + NEW_LINE +
            "                    \"parameterOneName\" : [ \"parameterOneValueOne\", \"parameterOneValueTwo\" ]" + NEW_LINE +
            "                }" + NEW_LINE +
            "        }" + NEW_LINE +
            "    }" + NEW_LINE +
            "}");

        // when
        ExpectationDTO expectationDTO = ObjectMapperFactory.createObjectMapper().readValue(json, ExpectationDTO.class);

        // then
        assertEquals(new ExpectationDTO()
            .setHttpRequest(
                new HttpRequestDTO()
                    .setBody(new ParameterBodyDTO(new ParameterBody()))
            ), expectationDTO);
    }

}
