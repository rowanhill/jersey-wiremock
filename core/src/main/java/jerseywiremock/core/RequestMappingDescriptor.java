package jerseywiremock.core;

import java.util.Map;

public class RequestMappingDescriptor {
    private final String urlPath;
    private final Map<String, String> queryParams;

    public RequestMappingDescriptor(String urlPath, Map<String, String> queryParams) {
        this.urlPath = urlPath;
        this.queryParams = queryParams;
    }

    public String getUrlPath() {
        return urlPath;
    }

    public Map<String, String> getQueryParams() {
        return queryParams;
    }
}
