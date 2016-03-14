package jerseywiremock.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import jerseywiremock.service.core.Foo;
import jerseywiremock.service.resources.FooResource;

import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

/*
@WireMockForResource(FooResource.class)
public interface FooMocker {
    @WireMockStub("getById")
    GetRequestMocker<Foo> stubGetFoo(int id);

    @WireMockVerify("getById")
    GetRequestVerifier verifyGetFoo(int id);

    @WireMockStub("getAllByName")
    ListRequestMocker<Foo> stubListFoos(String name);

    @WireMockVerify("getAllByName")
    GetRequestVerifier verifyListFoos(String name);
}
*/

// TODO: Define as an annotated interface (as above), and construct with ByteBuddy
public class FooMocker {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public FooMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    public GetRequestMocker<Foo> stubGetFoo(int id) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getById", wireMockServer, objectMapper);
        return handler.handleStubGet(new Object[]{id});
    }

    public GetRequestVerifier verifyGetFoo(int id) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getById", wireMockServer, objectMapper);
        return handler.handleVerifyGet(new Object[]{id});
    }

    public ListRequestMocker<Foo> stubListFoos(String name) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getAllByName", wireMockServer, objectMapper);
        return handler.handleStubList(new Object[]{name});
    }

    public GetRequestVerifier verifyListFoos(String name) {
        MockerInvocationHandler handler = new MockerInvocationHandler(FooResource.class, "getAllByName", wireMockServer, objectMapper);
        return handler.handleVerifyList(new Object[]{name});
    }

    /*
      PRECOMPILED FILES
     */

    // TODO: When using ByteBuddy, the methods in this class will need to be static
    public static class MockerInvocationHandler {
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
            String urlPath = UrlPathBuilder.buildUrlPath(FooResource.class, methodName, paramMap);
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

    public static class ReflectionHelper {
        public static Method getMethod(Class<?> clazz, String methodName) {
            for (Method method : clazz.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
                    return method;
                }
            }
            throw new RuntimeException("No method named " + methodName + " on " + clazz.getSimpleName());
        }
    }

    public static class CollectionFactory {
        public static <T> Collection<T> createCollection(Class<?> resourceClass, String methodName) {
            Method method = ReflectionHelper.getMethod(resourceClass, methodName);

            Class<?> returnType = method.getReturnType();
            if (returnType.isAssignableFrom(Collection.class)) {
                return createCollection(returnType);
            } else {
                throw new RuntimeException(method.getDeclaringClass().getSimpleName() + "#" + methodName +
                        " does not return Collection type; it returns " + returnType.getSimpleName());
            }
        }

        private static <T> Collection<T> createCollection(Class<?> returnType) {
            if (returnType.isAssignableFrom(List.class)) {
                return new ArrayList<T>();
            } else if (returnType.isAssignableFrom(Set.class)) {
                return new HashSet<T>();
            } else if (returnType.equals(Collection.class)) {
                return new ArrayList<T>();
            } else {
                throw new RuntimeException("Cannot create collection for type " + returnType.getSimpleName());
            }
        }
    }

    public static class GetRequestMocker<Entity> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final String urlPath;

        public GetRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, String urlPath) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.urlPath = urlPath;
        }

        public GetResponseMocker<Entity> andRespondWith(Entity entity) {
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
            return new GetResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, entity);
        }
    }

    public static class GetResponseMocker<Entity> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final MappingBuilder mappingBuilder;
        private final Entity entity;
        private final ResponseDefinitionBuilder responseDefinitionBuilder;

        public GetResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Entity entity
        ) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.mappingBuilder = mappingBuilder;
            this.entity = entity;

            responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json");
        }

        public void stub() throws JsonProcessingException {
            String bodyString = objectMapper.writeValueAsString(entity);

            responseDefinitionBuilder.withBody(bodyString);

            wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
        }
    }

    public static abstract class BaseRequestVerifyBuilder {
        private final WireMockServer wireMockServer;
        private final RequestPatternBuilder requestPatternBuilder;

        private Integer numOfTimes;

        public BaseRequestVerifyBuilder(
                WireMockServer wireMockServer,
                VerbRequestedForStrategy verbRequestedForStrategy,
                String urlPath
        ) {
            this.wireMockServer = wireMockServer;

            this.requestPatternBuilder = verbRequestedForStrategy.verbRequestedFor(urlPathEqualTo(urlPath));
        }

        public BaseRequestVerifyBuilder times(int numTimes) {
            this.numOfTimes = numTimes;
            return this;
        }

        public void verify() {
            if (numOfTimes != null) {
                wireMockServer.verify(numOfTimes, requestPatternBuilder);
            } else {
                wireMockServer.verify(requestPatternBuilder);
            }
        }
    }

    public interface VerbRequestedForStrategy {
        RequestPatternBuilder verbRequestedFor(UrlMatchingStrategy urlMatchingStrategy);
    }

    public static class GetRequestedForStrategy implements VerbRequestedForStrategy {
        public RequestPatternBuilder verbRequestedFor(UrlMatchingStrategy urlMatchingStrategy) {
            return getRequestedFor(urlMatchingStrategy);
        }
    }

    public static class GetRequestVerifier extends BaseRequestVerifyBuilder {
        public GetRequestVerifier(WireMockServer wireMockServer, String urlPath) {
            super(wireMockServer, new GetRequestedForStrategy(), urlPath);
        }
    }

    public static class ListRequestMocker<Entity> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final String urlPath;
        private final Collection<Entity> collection;

        public ListRequestMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                String urlPath,
                Collection<Entity> collection
        ) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.urlPath = urlPath;
            this.collection = collection;
        }

        public ListResponseMocker<Entity> andRespondWith(Entity... items) {
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
            Collections.addAll(collection, items);
            return new ListResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder, collection);
        }
    }

    public static class ListResponseMocker<Entity> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final MappingBuilder mappingBuilder;
        private final Collection<Entity> entities;
        private final ResponseDefinitionBuilder responseDefinitionBuilder;

        public ListResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Collection<Entity> entities
        ) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.mappingBuilder = mappingBuilder;
            this.entities = entities;

            responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json");
        }

        public void stub() throws JsonProcessingException {
            String bodyString = objectMapper.writeValueAsString(entities);

            responseDefinitionBuilder.withBody(bodyString);

            wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
        }
    }

    public static class UrlPathBuilder {
        public static String buildUrlPath(Class<?> resourceClass, String methodName, Map<String, Object> paramValues) {
            UriBuilder uriBuilder = UriBuilder.fromResource(resourceClass);

            Method method = ReflectionHelper.getMethod(resourceClass, methodName);

            for (Annotation methodAnnotation : method.getDeclaredAnnotations()) {
                if (methodAnnotation.annotationType().equals(Path.class)) {
                    uriBuilder.path(method);
                }
            }
            for (Annotation[] paramAnnotations : method.getParameterAnnotations()) {
                for (Annotation paramAnnotation : paramAnnotations) {
                    if (paramAnnotation instanceof QueryParam) {
                        QueryParam queryParam = (QueryParam) paramAnnotation;
                        String queryParamName = queryParam.value();
                        uriBuilder.queryParam(queryParamName, paramValues.get(queryParamName));
                    }
                }
            }

            return uriBuilder.buildFromMap(paramValues).toString();
        }
    }
}
