package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import java.util.List;
import java.util.Map;

// TODO: Commonise with RequestMappingDescriptor, or name better - only difference is pathParms are built into URL string
public class ParameterDescriptors {
    private final Map<String, String> pathParams;
    private final List<QueryParamMatchDescriptor> queryParamMatchDescriptors;
    private final ValueMatchDescriptor requestBodyMatchDescriptor;

    ParameterDescriptors(
            Map<String, String> pathParams,
            List<QueryParamMatchDescriptor> queryParamMatchDescriptors,
            ValueMatchDescriptor requestBodyMatchDescriptor
    ) {
        this.pathParams = pathParams;
        this.queryParamMatchDescriptors = queryParamMatchDescriptors;
        this.requestBodyMatchDescriptor = requestBodyMatchDescriptor;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public List<QueryParamMatchDescriptor> getQueryParamMatchDescriptors() {
        return queryParamMatchDescriptors;
    }

    public ValueMatchDescriptor getRequestBodyMatchDescriptor() {
        return requestBodyMatchDescriptor;
    }
}
