package jerseywiremock.core;

import java.util.List;

public class RequestMappingDescriptor {
    private final String urlPath;
    private final List<QueryParamMatchDescriptor> queryParamMatchDescriptors;

    public RequestMappingDescriptor(String urlPath, List<QueryParamMatchDescriptor> queryParamMatchDescriptors) {
        this.urlPath = urlPath;
        this.queryParamMatchDescriptors = queryParamMatchDescriptors;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public List<QueryParamMatchDescriptor> getQueryParamMatchDescriptors() {
        return queryParamMatchDescriptors;
    }
}
