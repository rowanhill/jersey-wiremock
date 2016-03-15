package jerseywiremock.annotations.handler;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.core.ReflectionHelper;
import jerseywiremock.core.UrlPathBuilder;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

// TODO: When using ByteBuddy, the methods in this class will need to be static
public class MockerInvocationHandler {
    // TODO: When using ByteBuddy, the resourceClass & methodName will be read with reflection from an injected @Super's annotations
    private final Class<?> resourceClass;
    private final String methodName;
    // TODO: When using ByteBuddy, the wireMockServer & objectMapper will be accessed from a @This
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public MockerInvocationHandler(
            Class<?> resourceClass,
            String methodName,
            WireMockServer wireMockServer,
            ObjectMapper objectMapper
    ) {
        this.resourceClass = resourceClass;
        this.methodName = methodName;
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    public <T> GetRequestMocker<T> handleStubGet(Object[] parameters) {
        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestMocker<T>(wireMockServer, objectMapper, urlPath);
    }

    public GetRequestVerifier handleVerifyGet(Object[] parameters) {
        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestVerifier(wireMockServer, urlPath);
    }

    public <T> ListRequestMocker<T> handleStubList(Object[] parameters) {
        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        Collection<T> collection = CollectionFactory.createCollection(resourceClass, methodName);
        return new ListRequestMocker<T>(wireMockServer, objectMapper, urlPath, collection);
    }

    public GetRequestVerifier handleVerifyList(Object[] parameters) {
        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestVerifier(wireMockServer, urlPath);
    }

    private Map<String, Object> getParamMap(Object[] parameters) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        List<String> paramNames = new ArrayList<String>();
        for (Annotation[] parameterAnnotations : method.getParameterAnnotations()) {
            for (Annotation parameterAnnotation : parameterAnnotations) {
                String paramName = null;
                if (parameterAnnotation instanceof QueryParam) {
                    paramName = ((QueryParam) parameterAnnotation).value();
                } else if (parameterAnnotation instanceof PathParam) {
                    paramName = ((PathParam) parameterAnnotation).value();
                }

                if (paramName != null) {
                    paramNames.add(paramName);
                }
            }
        }

        if (paramNames.size() != parameters.length) {
            throw new RuntimeException("Invocation had " + parameters.length + " params, but " + paramNames.size() + " were given");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        for (int i = 0; i < paramNames.size(); i++) {
            paramMap.put(paramNames.get(i), parameters[i]);
        }

        return paramMap;
    }
}
