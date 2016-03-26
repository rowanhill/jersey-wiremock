package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import java.util.List;
import java.util.Map;

public class ParameterDescriptors {
    private final Map<String, String> pathParams;
    private final List<QueryParamMatchDescriptor> queryParamMatchDescriptors;

    ParameterDescriptors(
            Map<String, String> pathParams,
            List<QueryParamMatchDescriptor> queryParamMatchDescriptors
    ) {
        this.pathParams = pathParams;
        this.queryParamMatchDescriptors = queryParamMatchDescriptors;
    }

    public Map<String, String> getPathParams() {
        return pathParams;
    }

    public List<QueryParamMatchDescriptor> getQueryParamMatchDescriptors() {
        return queryParamMatchDescriptors;
    }
}
