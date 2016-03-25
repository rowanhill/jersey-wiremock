package jerseywiremock.annotations.handler;

import jerseywiremock.core.RequestMappingDescriptor;

class MockerMethodDescriptor {
    private final Class<?> resourceClass;
    private final String methodName;
    private final RequestMappingDescriptor requestMappingDescriptor;

    MockerMethodDescriptor(
            Class<?> resourceClass,
            String methodName,
            RequestMappingDescriptor requestMappingDescriptor
    ) {
        this.resourceClass = resourceClass;
        this.methodName = methodName;
        this.requestMappingDescriptor = requestMappingDescriptor;
    }

    public Class<?> getResourceClass() {
        return resourceClass;
    }

    public String getMethodName() {
        return methodName;
    }

    public RequestMappingDescriptor getRequestMappingDescriptor() {
        return requestMappingDescriptor;
    }
}
