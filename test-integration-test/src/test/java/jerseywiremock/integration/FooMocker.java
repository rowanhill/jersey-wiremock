package jerseywiremock.integration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.google.common.collect.ImmutableMap;
import jerseywiremock.service.core.Foo;
import jerseywiremock.service.resources.FooResource;

import javax.ws.rs.core.UriBuilder;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class FooMocker {
    private final WireMockServer wireMockServer;
    private final ObjectMapper objectMapper;

    public FooMocker(WireMockServer wireMockServer, ObjectMapper objectMapper) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
    }

    public GetFooRequestMocker stubGetFoo(int id) {
        return new GetFooRequestMocker(wireMockServer, objectMapper, id);
    }

    public ListFoosRequestMocker stubListFoos(String name) {
        return new ListFoosRequestMocker(wireMockServer, objectMapper, name);
    }

    /*
      PRECOMPILED FILES
     */

    public static abstract class GetRequestMocker<Entity, ResponseMocker extends GetResponseMocker<Entity>> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final String urlPath;

        public GetRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, String urlPath) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.urlPath = urlPath;
        }

        public ResponseMocker andRespondWith(Entity entity) {
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
            return createResponseMocker(wireMockServer, objectMapper, mappingBuilder, entity);
        }

        protected abstract ResponseMocker createResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Entity entity
        );
    }

    public static abstract class GetResponseMocker<Entity> {
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

    public static abstract class ListRequestMocker<
            Entity,
            CollectionType extends Collection<Entity>,
            ResponseMocker extends ListResponseMocker<Entity, CollectionType>
            > {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final String urlPath;

        public ListRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, String urlPath) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.urlPath = urlPath;
        }

        public ResponseMocker andRespondWith(Entity... items) {
            MappingBuilder mappingBuilder = get(urlPathEqualTo(urlPath));
            return createResponseMocker(wireMockServer, objectMapper, mappingBuilder, items);
        }

        protected abstract ResponseMocker createResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Entity... items
        );
    }

    public static abstract class ListResponseMocker<Entity, CollectionType extends Collection<Entity>> {
        private final WireMockServer wireMockServer;
        private final ObjectMapper objectMapper;
        private final MappingBuilder mappingBuilder;
        private final CollectionType entities;
        private final ResponseDefinitionBuilder responseDefinitionBuilder;

        public ListResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Entity... entities
        ) {
            this.wireMockServer = wireMockServer;
            this.objectMapper = objectMapper;
            this.mappingBuilder = mappingBuilder;

            this.entities = createEntitiesCollection();
            Collections.addAll(this.entities, entities);

            responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json");
        }

        public void stub() throws JsonProcessingException {
            String bodyString = objectMapper.writeValueAsString(entities);

            responseDefinitionBuilder.withBody(bodyString);

            wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
        }

        public abstract CollectionType createEntitiesCollection();
    }


    /*
      GENERATED FILES
     */

    public static class GetFooRequestMocker extends GetRequestMocker<Foo, GetFooResponseMocker> {
        public GetFooRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, int id) {
            super(wireMockServer,
                    objectMapper,
                    UriBuilder.fromResource(FooResource.class)
                            .path(FooResource.class, "getById")
                            .buildFromMap(ImmutableMap.of("id", id))
                            .toString());
        }

        @Override
        protected GetFooResponseMocker createResponseMocker(
                WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder, Foo foo
        ) {
            return new GetFooResponseMocker(wireMockServer, objectMapper, mappingBuilder, foo);
        }
    }

    public static class GetFooResponseMocker extends GetResponseMocker<Foo> {
        public GetFooResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Foo foo
        ) {
            super(wireMockServer, objectMapper, mappingBuilder, foo);
        }
    }


    public static class ListFoosRequestMocker extends ListRequestMocker<Foo, List<Foo>, ListFoosResponseMocker> {
        public ListFoosRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, String name) {
            super(wireMockServer,
                    objectMapper,
                    UriBuilder.fromResource(FooResource.class)
                            .queryParam("name", name)
                            .toString());
        }

        @Override
        protected ListFoosResponseMocker createResponseMocker(
                WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder, Foo... items
        ) {
            return new ListFoosResponseMocker(wireMockServer, objectMapper, mappingBuilder, items);
        }
    }

    public static class ListFoosResponseMocker extends ListResponseMocker<Foo, List<Foo>> {
        public ListFoosResponseMocker(
                WireMockServer wireMockServer,
                ObjectMapper objectMapper,
                MappingBuilder mappingBuilder,
                Foo... items
        ) {
            super(wireMockServer, objectMapper, mappingBuilder, items);
        }

        @Override
        public List<Foo> createEntitiesCollection() {
            return new ArrayList<Foo>();
        }
    }
}
