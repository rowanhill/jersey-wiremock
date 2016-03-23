package jerseywiremock.core;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

/**
 * Wraps either MappingBuilder or RequestPatternBuilder, exposing a common withQueryParam method
 */
public class WireMockQueryParamBuilderWrapper {
    private final QueryParamBuilder queryParamBuilder;

    public WireMockQueryParamBuilderWrapper(MappingBuilder mappingBuilder) {
        this.queryParamBuilder = new MappingBuilderWrapper(mappingBuilder);
    }

    public WireMockQueryParamBuilderWrapper(RequestPatternBuilder requestPatternBuilder) {
        this.queryParamBuilder = new RequestPatternBuilderWrapper(requestPatternBuilder);
    }

    public void withQueryParam(String key, ValueMatchingStrategy valueMatchingStrategy) {
        queryParamBuilder.withQueryParam(key, valueMatchingStrategy);
    }

    private interface QueryParamBuilder {
        void withQueryParam(String key, ValueMatchingStrategy valueMatchingStrategy);
    }

    private static class MappingBuilderWrapper implements QueryParamBuilder {
        private final MappingBuilder mappingBuilder;

        private MappingBuilderWrapper(MappingBuilder mappingBuilder) {
            this.mappingBuilder = mappingBuilder;
        }

        @Override
        public void withQueryParam(String key, ValueMatchingStrategy valueMatchingStrategy) {
            mappingBuilder.withQueryParam(key, valueMatchingStrategy);
        }
    }

    private static class RequestPatternBuilderWrapper implements QueryParamBuilder {
        private final RequestPatternBuilder requestPatternBuilder;

        private RequestPatternBuilderWrapper(RequestPatternBuilder requestPatternBuilder) {
            this.requestPatternBuilder = requestPatternBuilder;
        }

        @Override
        public void withQueryParam(String key, ValueMatchingStrategy valueMatchingStrategy) {
            requestPatternBuilder.withQueryParam(key, valueMatchingStrategy);
        }
    }
}
