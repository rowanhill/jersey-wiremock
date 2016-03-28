package jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.google.common.collect.*;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterDescriptor;
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

import static jerseywiremock.annotations.ParamMatchingStrategy.*;
import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.ENTITY;
import static jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.QUERY;
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
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "val" });

        // then
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                "http://localhost/forPathParam",
                ImmutableListMultimap.<String, ValueMatchingStrategy>of(),
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
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "1", "2", "3" });

        // then
        ListMultimap<String, ValueMatchingStrategy> multimap = ArrayListMultimap.create();
        multimap.put("default", equalTo);
        multimap.put("equalTo", equalTo);
        multimap.put("containing", containing);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
                null
        ));
    }

    @Test
    public void descriptorHasMatchingStrategyForEachItemInIterableUsedAsQueryParam() {
        // given
        ValueMatchingStrategy equalTo = new ValueMatchingStrategy();
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "l1")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "l2")).thenReturn(equalTo);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "list", null, null));
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "s1")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "s2")).thenReturn(equalTo);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "set", null, null));

        // when
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{
                ImmutableList.of("l1", "l2"),
                ImmutableSet.of("s1", "s2")
        });

        // then
        ListMultimap<String, ValueMatchingStrategy> multimap = ArrayListMultimap.create();
        multimap.put("list", equalTo);
        multimap.put("list", equalTo);
        multimap.put("set", equalTo);
        multimap.put("set", equalTo);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
                null
        ));
    }

    @Test
    public void descriptorUrlEncodesQueryParameterValues() {
        // given
        ValueMatchingStrategy equalTo = new ValueMatchingStrategy();
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "a%3Ab")).thenReturn(equalTo);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "query", null, null));

        // when
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "a:b" });

        // then
        ListMultimap<String, ValueMatchingStrategy> multimap = ArrayListMultimap.create();
        multimap.put("query", equalTo);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
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
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "entity" });

        // then
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                ImmutableListMultimap.<String, ValueMatchingStrategy>of(),
                matching
        ));
    }

    @Test
    public void exceptionIsThrownIfNumberOfParamsDoesNotMuchNumberOfParamDescriptors() {
        // when
        expectedException.expectMessage("Invocation of method had 1 params, but 0 are desired");
        createDescriptor(new Object[]{ "Param value without descriptor" });
    }

    private RequestMatchingDescriptor createDescriptor(Object[] params) {
        return descriptorFactory.createRequestMatchingDescriptor(
                targetMethod,
                mockerMethod,
                params,
                mockUriBuilder,
                RequestMatchingDescriptorFactory.QueryParamEncodingStrategy.ENCODED);
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