package io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import static io.jerseywiremock.annotations.ParamMatchingStrategy.CONTAINING;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.EQUAL_TO;
import static io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.PATH;
import static io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.QUERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Date;
import java.util.LinkedList;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;

import org.junit.jupiter.api.Test;

import io.jerseywiremock.annotations.ParamFormat;
import io.jerseywiremock.annotations.ParamMatchedBy;
import io.jerseywiremock.annotations.ParamNamed;
import io.jerseywiremock.annotations.formatter.ParamFormatter;
import io.jerseywiremock.annotations.handler.util.ReflectionHelper;

public class ParameterAnnotationsProcessorTest {
    private final ParameterAnnotationsProcessor parameterAnnotationsProcessor = new ParameterAnnotationsProcessor();

    @Test
    public void targetMethodWithNoParamsProducesNoParamDescriptors() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors = createParameterDescriptors("noParams", "noParams");

        // then
        assertThat(parameterDescriptors).isEmpty();
    }

    @Test
    public void targetMethodWithPathParamProducesPathParamDescriptor() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors = createParameterDescriptors("pathParam", "oneNakedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(PATH, "one", null, null));
    }

    @Test
    public void targetMethodWithQueryParamWithUnspecifiedMatchingStrategyProducesQueryParamDescriptorUsingEqualTo() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors = createParameterDescriptors("queryParam", "oneNakedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(QUERY, "one", null, EQUAL_TO));
    }

    @Test
    public void targetMethodWithQueryAndPathParamsProducesParamDescriptorsForBoth() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("pathAndQueryParams", "twoNakedParams");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(PATH, "path", null, null), tuple(QUERY, "query", null, EQUAL_TO));
    }

    @Test
    public void targetMethodWithOneUnannotatedParamProducesNoParamDescriptors() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("unannotatedParam", "noParams");

        // then
        assertThat(parameterDescriptors).isEmpty();
    }

    @Test
    public void targetMethodWithUnsupportedJaxRsParamsProducesNoParamDescriptors() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("noSupportedParams", "noParams");

        // then
        assertThat(parameterDescriptors).isEmpty();
    }

    @Test
    public void targetMethodWithPathParamWithFormatterProducesParamDescriptorWithFormatter() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("formattedPathParam", "oneNakedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(PATH, "one", StaticFormatter.class, null));
    }

    @Test
    public void targetMethodWithQueryParamWithFormatterProducesParamDescriptorWithFormatter() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("formattedQueryParam", "oneNakedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(QUERY, "one", StaticFormatter.class, EQUAL_TO));
    }

    @Test
    public void formattersCanBeOverriddenOnMockerMethodParameters() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("formattedQueryParam", "formattedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsExactly(tuple(QUERY, "one", AnotherFormatter.class, EQUAL_TO));
    }

    @Test
    public void targetMethodWithQueryParamWithMatchingStrategyProducesParamDescriptorWithMatchingStrategy() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("queryParam", "containingParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(QUERY, "one", null, CONTAINING));
    }

    @Test
    public void targetMethodWithPathParamWithMatchingStrategyProducesParamDescriptorIgnoringMatchingStrategy() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("pathParam", "containingParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(PATH, "one", null, null));
    }

    @Test
    public void targetMethodWithMoreParamsThanMockerMethodCausesException() {
        assertThrows(Exception.class, () -> createParameterDescriptors("pathAndQueryParams", "oneNakedParam"));
    }

    @Test
    public void mockerMethodWithOnlySomeParamsAnnotatedWithParamNameCausesException() {
        assertThrows(Exception.class, () -> createParameterDescriptors("pathAndQueryParams", "onlySomeNamedParams"));
    }

    @Test
    public void mockerMethodParametersCanBeNamedAndCreatesParameterDescriptorsInOrderOfMockerMethod() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("pathAndQueryParams", "allNamedParams");

        // then
        // Note query and path parameters are in reverse order (i.e. order of mocker method params, not resource method)
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsExactly(tuple(QUERY, "query", null, EQUAL_TO), tuple(PATH, "path", null, null));
    }

    @Test
    public void queryParamsAreOptionalIfSpecifyingMockerParameterNames() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("pathAndQueryParams", "onlyPathParams");

        // then
        // Note query and path parameters are in reverse order (i.e. order of mocker method params, not resource method)
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsExactly(tuple(PATH, "path", null, null));
    }

    @Test
    public void queryParamsAreOptionalIfNoMockerParametersAreSpecified() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors = createParameterDescriptors("queryParam", "noParams");

        // then
        assertThat(parameterDescriptors).isEmpty();
    }

    @Test
    public void pathParamsAreRequiredEvenIfSpecifyingMockerParameterNames() {
        assertThrows(Exception.class, () -> createParameterDescriptors("pathAndQueryParams", "missingPathParams"));
    }

    @Test
    public void duplicatedNamedParametersCausesAnException() {
        // when
        assertThrows(Exception.class,
                () -> createParameterDescriptors("pathAndQueryParams", "duplicatedNamedParams"));
    }

    private LinkedList<ParameterDescriptor> createParameterDescriptors(String resourceMethod, String mockerMethod) {
        return parameterAnnotationsProcessor.createParameterDescriptors(
                ReflectionHelper.getMethod(TestResource.class, resourceMethod),
                ReflectionHelper.getMethod(TestMocker.class, mockerMethod));
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void noParams() {}

        void pathParam(@PathParam("one") String one) {}
        void queryParam(@QueryParam("one") String one) {}
        void pathAndQueryParams(@PathParam("path") String path, @QueryParam("query") String query) {}

        void unannotatedParam(String unannotated) {}

        void noSupportedParams(@Context String notPathOrQuery) {}

        void formattedPathParam(@ParamFormat(StaticFormatter.class) @PathParam("one") Date date) {}
        void formattedQueryParam(@ParamFormat(StaticFormatter.class) @QueryParam("one") Date date) {}
    }

    @SuppressWarnings("unused")
    private static class TestMocker {
        void noParams() {}

        void oneNakedParam(String one) {}
        void twoNakedParams(String one, String two) {}

        void containingParam(@ParamMatchedBy(CONTAINING) String containing) {}

        void onlySomeNamedParams(@ParamNamed("path") String path, String query) {}
        void allNamedParams(@ParamNamed("query") String query, @ParamNamed("path") String path) {}
        void onlyPathParams(@ParamNamed("path") String path) {}
        void missingPathParams(@ParamNamed("query") String query) {}
        void duplicatedNamedParams(@ParamNamed("path") String path1, @ParamNamed("path") String path2) {}

        void formattedParam(@ParamFormat(AnotherFormatter.class) String one) {}
    }

    static class StaticFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "formatted";
        }
    }

    static class AnotherFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "another-formatter";
        }
    }
}