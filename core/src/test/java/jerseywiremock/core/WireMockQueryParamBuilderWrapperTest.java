package jerseywiremock.core;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

public class WireMockQueryParamBuilderWrapperTest {

    private ValueMatchingStrategy matchingStrategy = new ValueMatchingStrategy();
    private String key = "foo";

    @Test
    public void withQueryParamsCallsAreDelegatedToGivenMappingBuilder() {
        // given
        MappingBuilder mockBuilder = mock(MappingBuilder.class);

        // when
        new WireMockQueryParamBuilderWrapper(mockBuilder).withQueryParam(key, matchingStrategy);

        // then
        verify(mockBuilder).withQueryParam(key, matchingStrategy);
    }

    @Test
    public void withQueryParamCallsAreDelegatedToGivenRequestPatternBuilder() {
        // given
        RequestPatternBuilder mockBuilder = mock(RequestPatternBuilder.class);

        // when
        new WireMockQueryParamBuilderWrapper(mockBuilder).withQueryParam(key, matchingStrategy);

        // then
        verify(mockBuilder).withQueryParam(key, matchingStrategy);
    }
}