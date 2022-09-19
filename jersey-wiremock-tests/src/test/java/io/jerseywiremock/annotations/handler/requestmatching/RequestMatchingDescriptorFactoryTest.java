package io.jerseywiremock.annotations.handler.requestmatching;

import static io.jerseywiremock.annotations.ParamMatchingStrategy.CONTAINING;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.EQUAL_TO;
import static io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.HEADER;
import static io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType.QUERY;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.lang.reflect.Method;
import java.net.URI;
import java.util.LinkedList;

import javax.ws.rs.core.UriBuilder;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import com.github.tomakehurst.wiremock.matching.ContainsPattern;
import com.github.tomakehurst.wiremock.matching.EqualToPattern;
import com.github.tomakehurst.wiremock.matching.StringValuePattern;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableListMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.ListMultimap;

import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType;
import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterDescriptor;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
public class RequestMatchingDescriptorFactoryTest {
    private static final String PATH = "http://localhost";

    @Mock
    private ParameterAnnotationsProcessor mockParameterAnnotationsProcessor;
    @Mock
    private ParamFormatterInvoker mockParamFormatterInvoker;
    @Mock
    private ValueMatchingStrategyFactory mockValueMatchingStrategyFactory;
    @InjectMocks
    private RequestMatchingDescriptorFactory descriptorFactory;

    private final LinkedList<ParameterDescriptor> parameterDescriptors = new LinkedList<>();
    private Method targetMethod;
    private Method mockerMethod;

    @Mock
    private UriBuilder mockUriBuilder;

    @BeforeEach
    public void setUp() throws Exception {
        targetMethod = TestResource.class.getDeclaredMethod("method");
        mockerMethod = TestMocker.class.getDeclaredMethod("method");
        when(mockParameterAnnotationsProcessor.createParameterDescriptors(targetMethod, mockerMethod))
                .thenReturn(parameterDescriptors);

        when(mockParamFormatterInvoker.getFormattedParamValue(any(), any())).thenReturn("formattedVal");

        when(mockUriBuilder.buildFromMap(ImmutableMap.of())).thenReturn(new URI("http://localhost"));
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
                ImmutableListMultimap.of(),
                ImmutableListMultimap.of()
        ));
    }

    @Test
    public void descriptorDerivesQueryParamMatchingStrategiesFromQueryParams() {
        // given
        StringValuePattern equalTo = new EqualToPattern("123");
        StringValuePattern containing = new ContainsPattern("123");
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "1")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(EQUAL_TO, "2")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(CONTAINING, "3")).thenReturn(containing);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "default", null, null));
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "equalTo", null, EQUAL_TO));
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "containing", null, CONTAINING));

        // when
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "1", "2", "3" });

        // then
        ListMultimap<String, StringValuePattern> multimap = ArrayListMultimap.create();
        multimap.put("default", equalTo);
        multimap.put("equalTo", equalTo);
        multimap.put("containing", containing);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
                ImmutableListMultimap.of()
        ));
    }

    @Test
    public void descriptorDerivesQueryParamMatchingStrategiesFromHeaderParams() {
        // given
        StringValuePattern equalTo = new EqualToPattern("123");
        StringValuePattern containing = new ContainsPattern("123");
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "1")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(EQUAL_TO, "2")).thenReturn(equalTo);
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(CONTAINING, "3")).thenReturn(containing);
        parameterDescriptors.add(new ParameterDescriptor(HEADER, "default", null, null));
        parameterDescriptors.add(new ParameterDescriptor(HEADER, "equalTo", null, EQUAL_TO));
        parameterDescriptors.add(new ParameterDescriptor(HEADER, "containing", null, CONTAINING));

        // when
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "1", "2", "3" });

        // then
        ListMultimap<String, StringValuePattern> multimap = ArrayListMultimap.create();
        multimap.put("default", equalTo);
        multimap.put("equalTo", equalTo);
        multimap.put("containing", containing);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                ImmutableListMultimap.of(),
                multimap
        ));
    }

    @Test
    public void descriptorHasMatchingStrategyForEachItemInIterableUsedAsQueryParam() {
        // given
        StringValuePattern equalTo = new EqualToPattern("123");
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
        ListMultimap<String, StringValuePattern> multimap = ArrayListMultimap.create();
        multimap.put("list", equalTo);
        multimap.put("list", equalTo);
        multimap.put("set", equalTo);
        multimap.put("set", equalTo);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
                ImmutableListMultimap.of()
        ));
    }

    @Test
    public void descriptorUrlEncodesQueryParameterValues() {
        // given
        StringValuePattern equalTo = new EqualToPattern("123");
        when(mockValueMatchingStrategyFactory.createValueMatchingStrategy(null, "a%3Ab")).thenReturn(equalTo);
        parameterDescriptors.add(new ParameterDescriptor(QUERY, "query", null, null));

        // when
        RequestMatchingDescriptor descriptor = createDescriptor(new Object[]{ "a:b" });

        // then
        ListMultimap<String, StringValuePattern> multimap = ArrayListMultimap.create();
        multimap.put("query", equalTo);
        assertThat(descriptor).isEqualToComparingFieldByField(new RequestMatchingDescriptor(
                PATH,
                multimap,
                ImmutableListMultimap.of()
        ));
    }

    @Test
    public void exceptionIsThrownIfNumberOfParamsDoesNotMuchNumberOfParamDescriptors() {
        assertThrows(Exception.class, () -> createDescriptor(new Object[] {"Param value without descriptor"}));
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