package jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.ParamMatchedBy;
import jerseywiremock.annotations.handler.util.ReflectionHelper;
import jerseywiremock.formatter.ParamFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.LinkedList;

import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamMatchingStrategy.CONTAINING;
import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamMatchingStrategy.EQUAL_TO;
import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.tuple;

public class ParameterAnnotationsProcessorTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ParameterAnnotationsProcessor parameterAnnotationsProcessor = new ParameterAnnotationsProcessor();

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
    public void targetMethodWithOneUnannotatedParamProducesEntityParamDescriptor() {
        // when
        LinkedList<ParameterDescriptor> parameterDescriptors =
                createParameterDescriptors("unannotatedParam", "oneNakedParam");

        // then
        assertThat(parameterDescriptors)
                .extracting("paramType", "paramName", "formatterClass", "matchingStrategy")
                .containsOnly(tuple(ENTITY, null, null, EQUAL_TO));
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
        // when
        expectedException.expectMessage("Expected noParams to have at least 1 params, but has 0");
        createParameterDescriptors("pathParam", "noParams");
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
    }

    static class StaticFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "formatted";
        }
    }
}