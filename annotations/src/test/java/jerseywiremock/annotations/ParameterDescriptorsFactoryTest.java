package jerseywiremock.annotations;

import jerseywiremock.core.ParamMatchingStrategy;
import jerseywiremock.core.ParameterDescriptors;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;

import java.lang.annotation.Annotation;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.assertj.core.api.Assertions.tuple;

public class ParameterDescriptorsFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private ParameterDescriptorsFactory factory;

    @Before
    public void setUp() {
        factory = new ParameterDescriptorsFactory();
    }

    @Test
    public void paramsDescriptorsForNoParamsMethodHasNoPathOrQueryParamDescriptors() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerNoParamsAnnotations(), params(0), "noParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchDescriptors()).isEmpty();
    }

    @Test
    public void paramsDescriptorsForOnePathParamMethodHasOnePathParamDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerOnePathParamAnnotations(), params(1), "onePathParam");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "val1"));
        assertThat(descriptors.getQueryParamMatchDescriptors()).isEmpty();
    }

    @Test
    public void paramsDescriptorsForTwoPathParamsMethodHasTwoPathParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoPathParamsAnnotations(), params(2), "twoPathParams");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "val1"), entry("two", "val2"));
        assertThat(descriptors.getQueryParamMatchDescriptors()).isEmpty();
    }

    @Test
    public void paramsDescriptorsForOneQueryParamMethodHasOneQueryParamDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerOnePathParamAnnotations(), params(1), "oneQueryParam");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchDescriptors())
                .extracting("paramName", "value", "matchingStrategy")
                .containsOnly(tuple("one", "val1", ParamMatchingStrategy.EQUAL_TO));
    }

    @Test
    public void paramsDescriptorsForTwoQueryParamsMethodHasTwoQueryParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoPathParamsAnnotations(), params(2), "twoQueryParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchDescriptors())
                .extracting("paramName", "value", "matchingStrategy")
                .containsOnly(
                        tuple("one", "val1", ParamMatchingStrategy.EQUAL_TO),
                        tuple("two", "val2", ParamMatchingStrategy.EQUAL_TO));
    }

    @Test
    public void paramsDescriptorsForMixedPathAndQueryParamsMethodHasBothPathAndQueryParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoPathParamsAnnotations(), params(2), "mixedPathAndQueryParams");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("path", "val1"));
        assertThat(descriptors.getQueryParamMatchDescriptors())
                .extracting("paramName", "value", "matchingStrategy")
                .containsOnly(tuple("query", "val2", ParamMatchingStrategy.EQUAL_TO));
    }

    @Test
    public void exceptionIsRaisedCreatingParamsDescriptorsWithWrongNumberOfParamsForTargetMethod() throws Exception {
        // when
        expectedException.expectMessage("Invocation of noParams had 1 params, but 0 are desired");
        createDescriptors(mockerNoParamsAnnotations(), params(1), "noParams");
    }

    @Test
    public void paramsDescriptorsForMethodWithUnimportantParamsIgnoreUnimportantParams() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerNoParamsAnnotations(), params(0), "noImportantParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchDescriptors()).isEmpty();
    }

    // TODO: @ParamFormat and @ParamMatchedBy tests

    private ParameterDescriptors createDescriptors(
            Annotation[][] mockerAnnotations,
            Object[] params,
            String resourceMethod
    ) throws NoSuchMethodException {
        return factory.createParameterDescriptors(
                params,
                mockerAnnotations,
                ParamDescTestResource.class,
                resourceMethod);
    }

    private Object[] params(int num) {
        Object[] params = new Object[num];

        for (int i = 0; i < num; i++) {
            params[i] = "val" + (i+1);
        }

        return params;
    }

    private Annotation[][] mockerNoParamsAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("noParams").getParameterAnnotations();
    }

    private Annotation[][] mockerOnePathParamAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("oneNakedParam", String.class).getParameterAnnotations();
    }

    private Annotation[][] mockerTwoPathParamsAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("twoNakedParams", String.class, String.class)
                .getParameterAnnotations();
    }

    @SuppressWarnings("unused")
    private static class ParamDescTestResource {
        void noParams() {}
        void onePathParam(@PathParam("one") String one) {}
        void twoPathParams(@PathParam("one") String one, @PathParam("two") String two) {}
        void oneQueryParam(@QueryParam("one") String one) {}
        void twoQueryParams(@QueryParam("one") String one, @QueryParam("two") String two) {}
        void mixedPathAndQueryParams(@PathParam("path") String path, @QueryParam("query") String query) {}
        void noImportantParams(String notPathOrQuery) {}
    }

    @SuppressWarnings("unused")
    private static class TestMocker {
        void noParams() {}
        void oneNakedParam(String one) {}
        void twoNakedParams(String one, String two) {}
    }
}