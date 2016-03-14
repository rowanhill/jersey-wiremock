package jerseywiremock.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;
import com.google.common.collect.ImmutableMap;
import jerseywiremock.service.core.Foo;
import jerseywiremock.service.resources.FooResource;

import javax.ws.rs.Path;
import javax.ws.rs.QueryParam;
import javax.ws.rs.core.UriBuilder;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FooMocker {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public FooMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    public GetRequestMocker<Foo> stubGetFoo(int id) {
        String urlPath = UrlPathBuilder.buildUrlPath(FooResource.class, "getById", ImmutableMap.<String, Object>of("id", id));
        return new GetRequestMocker<Foo>(wireMockServer, objectMapper, urlPath);
    }

    public GetRequestVerifier verifyGetFoo(int id) {
        String urlPath = UrlPathBuilder.buildUrlPath(FooResource.class, "getById", ImmutableMap.<String, Object>of("id", id));
        return new GetRequestVerifier(wireMockServer, urlPath);
    }

    public ListRequestMocker<Foo, List<Foo>> stubListFoos(String name) {
        String urlPath = UrlPathBuilder.buildUrlPath(FooResource.class, "getAllByName", ImmutableMap.<String, Object>of("name", name));
        List<Foo> collection = new ArrayList<Foo>();
        return new ListRequestMocker<Foo, List<Foo>>(wireMockServer, objectMapper, urlPath, collection);
    }

    public GetRequestVerifier verifyListFoos(String name) {
        String urlPath = UrlPathBuilder.buildUrlPath(FooResource.class, "getAllByName", ImmutableMap.<String, Object>of("name", name));
        return new GetRequestVerifier(wireMockServer, urlPath);
    }

    /*
      PRECOMPILED FILES
     */

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

    public static class ListRequestMocker<Entity, CollectionType extends Collection<Entity>> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final String urlPath;
        private final CollectionType collection;

        public ListRequestMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                String urlPath,
                CollectionType collection
        ) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.urlPath = urlPath;
            this.collection = collection;
        }

        public ListResponseMocker<Entity, CollectionType> andRespondWith(Entity... items) {
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
            Collections.addAll(collection, items);
            return new ListResponseMocker<Entity, CollectionType>(wireMockServer, objectMapper, mappingBuilder, collection);
        }
    }

    public static class ListResponseMocker<Entity, CollectionType extends Collection<Entity>> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final MappingBuilder mappingBuilder;
        private final CollectionType entities;
        private final ResponseDefinitionBuilder responseDefinitionBuilder;

        public ListResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                CollectionType entities
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

            for (Method method : resourceClass.getDeclaredMethods()) {
                if (method.getName().equals(methodName)) {
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
                }
            }

            return uriBuilder.buildFromMap(paramValues).toString();
        }
    }
}
