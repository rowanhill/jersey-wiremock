package jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.ParamMatchedBy;
import jerseywiremock.annotations.ParamNamed;
import jerseywiremock.annotations.handler.util.ReflectionHelper;
import jerseywiremock.annotations.formatter.ParamFormatter;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.util.Date;
import java.util.LinkedList;

import static jerseywiremock.annotations.ParamMatchingStrategy.CONTAINING;
import static jerseywiremock.annotations.ParamMatchingStrategy.EQUAL_TO;
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
        expectedException.expectMessage("Expected oneNakedParam to have 2 param(s), but has 1");
        createParameterDescriptors("pathAndQueryParams", "oneNakedParam");
    }

    @Test
    public void mockerMethodWithOnlySomeParamsAnnotatedWithParamNameCausesException() {
        // when
        expectedException.expectMessage("Only some parameters were annotated with @ParamNamed; either all must be, or none");
        createParameterDescriptors("pathAndQueryParams", "onlySomeNamedParams");
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
        // when
        expectedException.expectMessage("Expected missingPathParams to specify all path parameters, but the following are missing: [path]");
        createParameterDescriptors("pathAndQueryParams", "missingPathParams");
    }

    @Test
    public void duplicatedNamedParametersCausesAnException() {
        // when
        expectedException.expectMessage("Named parameters must be unique, but the following are duplicated: [path]");
        createParameterDescriptors("pathAndQueryParams", "duplicatedNamedParams");
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
    }

    static class StaticFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "formatted";
        }
    }
}