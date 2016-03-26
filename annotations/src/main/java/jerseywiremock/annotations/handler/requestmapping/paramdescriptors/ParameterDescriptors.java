package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import java.util.Map;

// TODO: Commonise with RequestMappingDescriptor, or name better - only difference is pathParms are built into URL string
public class ParameterDescriptors {
    private final Map<String, String> pathParams;
    private final Map<String, ValueMatchingStrategy> queryParamMatchingStrategies;
    private final ValueMatchingStrategy requestBodyMatchingStrategy;

    ParameterDescriptors(
            Map<String, String> pathParams,
            Map<String, ValueMatchingStrategy> queryParamMatchingStrategies,
            ValueMatchingStrategy requestBodyMatchingStrategy
    ) {
        this.pathParams = pathParams;
        this.queryParamMatchingStrategies = queryParamMatchingStrategies;
        this.requestBodyMatchingStrategy = requestBodyMatchingStrategy;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public Map<String, ValueMatchingStrategy> getQueryParamMatchingStrategies() {
        return queryParamMatchingStrategies;
    }

    public ValueMatchingStrategy getRequestBodyMatchingStrategy() {
        return requestBodyMatchingStrategy;
    }
}
