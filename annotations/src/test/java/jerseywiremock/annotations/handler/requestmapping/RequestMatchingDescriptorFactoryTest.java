package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.google.common.collect.ImmutableMap;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamType;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterDescriptor;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;

import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy.*;
import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamType.ENTITY;
import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamType.QUERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class RequestMatchingDescriptorFactoryTest {
    private static final String PATH = "http://localhost";

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    @Mock
    private ParameterAnnotationsProcessor mockParameterAnnotationsProcessor;
    @Mock
    private ParamFormatterInvoker mockParamFormatterInvoker;
    @Mock
    private ValueMatchingStrategyFactory mockValueMatchingStrategyFactory;
    @InjectMocks
    private RequestMatchingDescriptorFactory descriptorFactory;

    private LinkedList<ParameterDescriptor> parameterDescriptors = new LinkedList<>();
    private Method targetMethod;
    private Method mockerMethod;

    @Mock
    private UriBuilder mockUriBuilder;

    @Before
    public void setUp() throws Exception {
        targetMethod = TestResource.class.getDeclaredMethod("method");
        mockerMethod = TestMocker.class.getDeclaredMethod("method");
        when(mockParameterAnnotationsProcessor.createParameterDescriptors(targetMethod, mockerMethod))
                .thenReturn(parameterDescriptors);

        //noinspection unchecked
        when(mockParamFormatterInvoker.getFormattedParamValue(anyString(), any(Class.class))).thenReturn("formattedVal");

        when(mockUriBuilder.buildFromMap(ImmutableMap.<String, Object>of())).thenReturn(new URI("http://localhost"));
    }

    @Test
    public void descriptorDerivesUriPathFromUriBuilderAndPathParams() throws Exception{
        // given
        parameterDescriptors.add(new ParameterDescriptor(ParamType.PATH, "pathParam", null, null));
        when(mockUriBuilder.buildFromMap(ImmutableMap.of("pathParam", "formattedVal")))
                .thenReturn(new URI("http://localhost/forPathParam"));

        // when
        RequestMatchingDescriptor descriptor = descriptorFactory.createRequestMatchingDescriptor(
                targetMethod,
                mockerMethod,
                new Object[]{ "val" },
                mockUriBuilder);

        // then
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                "http://localhost/forPathParam",
                ImmutableMap.<String, ValueMatchingStrategy>of(),
                null
        ));
    }

    @Test
    public void descriptorDerivesQueryParamMatchingStrategiesFromQueryParams() {
        // given
        ValueMatchingStrategy equalTo = new ValueMatchingStrategy();
        ValueMatchingStrategy containing = new ValueMatchingStrategy();
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "1")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(EQUAL_TO, "2")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(CONTAINING, "3")).thenReturn(containing);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "default", null, null));
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "equalTo", null, EQUAL_TO));
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "containing", null, CONTAINING));

        // when
        RequestMatchingDescriptor descriptor = descriptorFactory.createRequestMatchingDescriptor(
                targetMethod,
                mockerMethod,
                new Object[]{ "1", "2", "3" },
                mockUriBuilder);

        // then
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                ImmutableMap.of("default", equalTo, "equalTo", equalTo, "containing", containing),
                null
        ));
    }

    @Test
    public void descriptorDerivesRequestBodyMatchingStrategyFromEntityParam() {
        // given
        ValueMatchingStrategy matching = new ValueMatchingStrategy();
        parameterDescriptors.add(new ParameterDescriptor(ENTITY, null, null, MATCHING));
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(MATCHING, "formattedVal")).thenReturn(matching);

        // when
        RequestMatchingDescriptor descriptor = descriptorFactory.createRequestMatchingDescriptor(
                targetMethod,
                mockerMethod,
                new Object[]{ "entity" },
                mockUriBuilder);

        // then
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                ImmutableMap.<String, ValueMatchingStrategy>of(),
                matching
        ));
    }

    @Test
    public void exceptionIsThrownIfNumberOfParamsDoesNotMuchNumberOfParamDescriptors() {
        // when
        expectedException.expectMessage("Invocation of method had 1 params, but 0 are desired");
        descriptorFactory.createRequestMatchingDescriptor(
                targetMethod,
                mockerMethod,
                new Object[]{ "Param value without descriptor" },
                mockUriBuilder);
    }

    @SuppressWarnings("unused")
    private static class TestResource {
        void method() {}
    }

    @SuppressWarnings("unused")
    private static class TestMocker {
        void method() {}
    }
}