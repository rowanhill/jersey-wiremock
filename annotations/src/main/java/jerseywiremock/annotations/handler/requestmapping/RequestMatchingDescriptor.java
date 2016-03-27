package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.stubverbs.VerbMappingBuilderStrategy;
import jerseywiremock.annotations.handler.requestmapping.verifyverbs.VerbRequestedForStrategy;

import java.util.Map;

import static com.github.tomakehurst.wiremock.client.WireMock.urlPathEqualTo;

public class RequestMatchingDescriptor {
    private final String urlPath;
    private final Map<String, ValueMatchingStrategy> queryParamMatchingStrategies;
    private final ValueMatchingStrategy requestBodyMatchingStrategy;

    RequestMatchingDescriptor(
            String urlPath,
            Map<String, ValueMatchingStrategy> queryParamMatchingStrategies,
            ValueMatchingStrategy requestBodyMatchingStrategy
    ) {
        this.urlPath = urlPath;
        this.queryParamMatchingStrategies = queryParamMatchingStrategies;
        this.requestBodyMatchingStrategy = requestBodyMatchingStrategy;
    }

    public MappingBuilder toMappingBuilder(VerbMappingBuilderStrategy verbMappingBuilderStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        MappingBuilder mappingBuilder = verbMappingBuilderStrategy.verb(urlMatchingStrategy);
        for (Map.Entry<String, ValueMatchingStrategy> entry : queryParamMatchingStrategies.entrySet()) {
            mappingBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        if (requestBodyMatchingStrategy != null) {
            mappingBuilder.withRequestBody(requestBodyMatchingStrategy);
        }
        return mappingBuilder;
    }

    public RequestPatternBuilder toRequestPatternBuilder(VerbRequestedForStrategy verbRequestedForStrategy) {
        UrlMatchingStrategy urlMatchingStrategy = urlPathEqualTo(urlPath);
        RequestPatternBuilder patternBuilder = verbRequestedForStrategy.verbRequestedFor(urlMatchingStrategy);
        for (Map.Entry<String, ValueMatchingStrategy> entry : queryParamMatchingStrategies.entrySet()) {
            patternBuilder.withQueryParam(entry.getKey(), entry.getValue());
        }
        if (requestBodyMatchingStrategy != null) {
            patternBuilder.withRequestBody(requestBodyMatchingStrategy);
        }
        return patternBuilder;
    }
}
