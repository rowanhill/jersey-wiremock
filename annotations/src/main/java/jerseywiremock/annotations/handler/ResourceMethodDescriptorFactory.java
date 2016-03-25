package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.WireMockForResource;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.annotations.factory.ParameterDescriptorsFactory;
import jerseywiremock.core.ParameterDescriptors;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.RequestMappingDescriptorFactory;

import java.lang.annotation.Annotation;
import java.lang.reflect.Method;

public class ResourceMethodDescriptorFactory {
    private final ParameterDescriptorsFactory parameterDescriptorsFactory;
    private final RequestMappingDescriptorFactory requestMappingDescriptorFactory;

    public ResourceMethodDescriptorFactory(
            ParameterDescriptorsFactory parameterDescriptorsFactory,
            RequestMappingDescriptorFactory requestMappingDescriptorFactory
    ) {
        this.parameterDescriptorsFactory = parameterDescriptorsFactory;
        this.requestMappingDescriptorFactory = requestMappingDescriptorFactory;
    }

    ResourceMethodDescriptor constructMethodDescriptor(
            Object[] parameters,
            Method method,
            Class<? extends Annotation> wireMockAnnotationType
    ) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String targetMethodName = getTargetMethodName(method, wireMockAnnotationType);
        Annotation[][] mockerMethodParameterAnnotations = method.getParameterAnnotations();
        ParameterDescriptors parameterDescriptors = parameterDescriptorsFactory.createParameterDescriptors(
                parameters,
                mockerMethodParameterAnnotations,
                resourceClass,
                targetMethodName);
        RequestMappingDescriptor mappingDescriptor = requestMappingDescriptorFactory
                .createMappingDescriptor(resourceClass, targetMethodName, parameterDescriptors);
        return new ResourceMethodDescriptor(resourceClass, targetMethodName, mappingDescriptor);
    }

    private String getTargetMethodName(Method method, Class<? extends Annotation> wireMockAnnotationType) {
        String methodName;
        if (wireMockAnnotationType == WireMockStub.class) {
            methodName = method.getAnnotation(WireMockStub.class).value();
        } else if (wireMockAnnotationType == WireMockVerify.class) {
            methodName = method.getAnnotation(WireMockVerify.class).value();
        } else {
            throw new RuntimeException("Unexpected annotation: " + wireMockAnnotationType.getSimpleName());
        }
        return methodName;
    }
}
