package jerseywiremock.annotations.handler;

import jerseywiremock.core.RequestMappingDescriptor;

class ResourceMethodDescriptor {
    private final Class<?> resourceClass;
    private final String methodName;
    private final HttpVerb verb;
    private final RequestMappingDescriptor requestMappingDescriptor;

    ResourceMethodDescriptor(
            Class<?> resourceClass,
            String methodName,
            HttpVerb verb,
            RequestMappingDescriptor requestMappingDescriptor
    ) {
        this.resourceClass = resourceClass;
        this.methodName = methodName;
        this.verb = verb;
        this.requestMappingDescriptor = requestMappingDescriptor;
    }

    /**
     * The class of the Jersey resource being described
     */
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    /**
     * The name of the method being described on the Jersey resource
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * The HTTP verb the described Jersey resource method services
     */
    public HttpVerb getVerb() {
        return verb;
    }

    /**
     * Details of how to match requests for the method being described
     */
    public RequestMappingDescriptor getRequestMappingDescriptor() {
        return requestMappingDescriptor;
    }
}
