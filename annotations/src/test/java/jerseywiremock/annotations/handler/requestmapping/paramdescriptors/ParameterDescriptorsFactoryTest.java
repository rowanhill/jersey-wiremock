package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.ParamMatchedBy;
import jerseywiremock.formatter.ParamFormatter;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.Context;
import java.lang.annotation.Annotation;
import java.util.Date;

import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy.CONTAINING;
import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy.EQUAL_TO;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.entry;
import static org.hamcrest.CoreMatchers.isA;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ParameterDescriptorsFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ValueMatchingStrategyFactory mockStrategyFactory;
    @InjectMocks
    private ParameterDescriptorsFactory factory;

    private ValueMatchingStrategy matchingStrategy = new ValueMatchingStrategy();

    @Before
    public void setUp() {
        when(mockStrategyFactory.toValueMatchingStrategy(any(ParamMatchingStrategy.class), anyString()))
                .thenReturn(matchingStrategy);
    }

    @Test
    public void paramsDescriptorsForNoParamsMethodHasNoPathOrQueryParamDescriptors() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerNoParamsAnnotations(), params(0), "noParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramsDescriptorsForOnePathParamMethodHasOnePathParamDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerOneParamAnnotations(), params(1), "onePathParam");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "val1"));
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramsDescriptorsForTwoPathParamsMethodHasTwoPathParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoParamsAnnotations(), params(2), "twoPathParams");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "val1"), entry("two", "val2"));
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramsDescriptorsForOneQueryParamMethodHasOneQueryParamDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerOneParamAnnotations(), params(1), "oneQueryParam");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(entry("one", matchingStrategy));
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramsDescriptorsForTwoQueryParamsMethodHasTwoQueryParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoParamsAnnotations(), params(2), "twoQueryParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(
                entry("one", matchingStrategy),
                entry("two", matchingStrategy)
        );
    }

    @Test
    public void paramsDescriptorsForMixedPathAndQueryParamsMethodHasBothPathAndQueryParamsDescriptor() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerTwoParamsAnnotations(), params(2), "mixedPathAndQueryParams");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("path", "val1"));
            assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(entry("query", matchingStrategy));
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void exceptionIsRaisedCreatingParamsDescriptorsWithWrongNumberOfParamsForTargetMethod() throws Exception {
        // when
        expectedException.expectMessage("Invocation of noParams had 1 params, but 0 are desired");
        createDescriptors(mockerNoParamsAnnotations(), params(1), "noParams");
    }

    @Test
    public void paramsDescriptorsForMethodWithUnannotatedParamsHasEntityParam() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerOneParamAnnotations(), params(1), "unannotatedParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isEqualTo(matchingStrategy);
    }

    @Test
    public void paramsDescriptorsForMethodWithUnsupportedParamsIgnoreUnimportantParams() throws Exception {
        // when
        ParameterDescriptors descriptors = createDescriptors(mockerNoParamsAnnotations(), params(0), "noSupportedParams");

        // then
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramFormatUsesSpecifiedClassToTransformPathParam() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerOneParamAnnotations(), new Date[]{null}, "formattedPathParam");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "formatted"));
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramFormatUsesSpecifiedClassToTransformQueryParam() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerOneParamAnnotations(), new Date[]{null}, "formattedQueryParam");

        // then
        verify(mockStrategyFactory).toValueMatchingStrategy(EQUAL_TO, "formatted");
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(entry("one", matchingStrategy));
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void queryParamMatchingStrategyCanBeChangedWithParamMatchedByAnnotation() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerContainingParamAnnotations(), params(1), "oneQueryParam");

        // then
        verify(mockStrategyFactory).toValueMatchingStrategy(CONTAINING, "val1");
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(entry("one", matchingStrategy));
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramMatchedByAnnotationHasNoEffectOnPathParam() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerContainingParamAnnotations(), params(1), "onePathParam");

        // then
        assertThat(descriptors.getPathParams()).containsOnly(entry("one", "val1"));
        assertThat(descriptors.getQueryParamMatchingStrategies()).isEmpty();
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void paramFormatAndParamMatchedByCanBothBeSpecifiedForTheSameParameter() throws Exception {
        // when
        ParameterDescriptors descriptors =
                createDescriptors(mockerContainingParamAnnotations(), new Date[]{null}, "formattedQueryParam");

        // then
        verify(mockStrategyFactory).toValueMatchingStrategy(CONTAINING, "formatted");
        assertThat(descriptors.getPathParams()).isEmpty();
        assertThat(descriptors.getQueryParamMatchingStrategies()).containsOnly(entry("one", matchingStrategy));
        assertThat(descriptors.getRequestBodyMatchingStrategy()).isNull();
    }

    @Test
    public void usingAbstractFormatterClassThrowsException() throws Exception {
        // when
        expectedException.expectMessage("Could not instantiate formatter AbstractFormatter");
        expectedException.expectCause(isA(InstantiationException.class));
        createDescriptors(mockerOneParamAnnotations(), new Date[]{null}, "abstractFormatter");
    }

    @Test
    public void usingNoNullConstructorFormatterClassThrowsException() throws Exception {
        // when
        expectedException.expectMessage("Could not instantiate formatter NoNullConstructorFormatter");
        expectedException.expectCause(isA(InstantiationException.class));
        createDescriptors(mockerOneParamAnnotations(), new Date[]{null}, "noNullConstructorFormatter");
    }

    @Test
    public void usingPrivateFormatterClassThrowsException() throws Exception {
        // when
        expectedException.expectMessage("Could not instantiate formatter PrivateFormatter");
        expectedException.expectCause(isA(IllegalAccessException.class));
        createDescriptors(mockerOneParamAnnotations(), new Date[]{null}, "privateFormatter");
    }

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

    private Annotation[][] mockerOneParamAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("oneNakedParam", String.class).getParameterAnnotations();
    }

    private Annotation[][] mockerTwoParamsAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("twoNakedParams", String.class, String.class)
                .getParameterAnnotations();
    }

    private Annotation[][] mockerContainingParamAnnotations() throws NoSuchMethodException {
        return TestMocker.class.getDeclaredMethod("containingParam", String.class)
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

        void unannotatedParams(String unannotated) {}

        void noSupportedParams(@Context String notPathOrQuery) {}

        void formattedPathParam(@ParamFormat(StaticFormatter.class) @PathParam("one") Date date) {}
        void formattedQueryParam(@ParamFormat(StaticFormatter.class) @QueryParam("one") Date date) {}
        void abstractFormatter(@ParamFormat(AbstractFormatter.class) @PathParam("one") Date date) {}
        void noNullConstructorFormatter(@ParamFormat(NoNullConstructorFormatter.class) @PathParam("one") Date date) {}
        void privateFormatter(@ParamFormat(PrivateFormatter.class) @PathParam("one") Date date) {}
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

    static abstract class AbstractFormatter implements ParamFormatter<Date> {

    }

    static class NoNullConstructorFormatter implements ParamFormatter<Date> {
        private final String result;

        NoNullConstructorFormatter(String result) {
            this.result = result;
        }

        @Override
        public String format(Date param) {
            return result;
        }
    }

    private static class PrivateFormatter implements ParamFormatter<Date> {
        @Override
        public String format(Date param) {
            return "This formatter should be inaccessible, because it is private";
        }
    }
}