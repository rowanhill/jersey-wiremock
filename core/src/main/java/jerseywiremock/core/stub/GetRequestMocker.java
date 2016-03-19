package jerseywiremock.core.stub;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import jerseywiremock.core.RequestMappingDescriptor;
import jerseywiremock.core.stub.verbs.GetMappingBuilderStrategy;

public class GetRequestMocker<Entity> extends BaseRequestMocker {
    public GetRequestMocker(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestMappingDescriptor mappingDescriptor
    ) {
        super(wireMockServer, objectMapper, mappingDescriptor, new GetMappingBuilderStrategy());
    }

    public GetRequestMocker(WireMockServer wireMockServer, ObjectMapper objectMapper, MappingBuilder mappingBuilder) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public GetResponseMocker<Entity> andRespond() {
        return new GetResponseMocker<Entity>(wireMockServer, objectMapper, mappingBuilder);
    }

    public GetResponseMocker<Entity> andRespondWith(Entity entity) {
        return andRespond().withEntity(entity);
    }
}
