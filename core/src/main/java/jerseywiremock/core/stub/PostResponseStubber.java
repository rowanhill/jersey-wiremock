package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

public class PostResponseStubber<Entity> extends BaseResponseStubber<PostResponseStubber<Entity>> {
    private Entity entity;

    public PostResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder
    ) {
        super(wireMockServer, objectMapper, mappingBuilder);
    }

    public PostResponseStubber<Entity> withEntity(Entity entity) {
        this.entity = entity;
        return this;
    }

    @Override
    protected void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder)
            throws JsonProcessingException
    {
        String bodyString = objectMapper.writeValueAsString(entity);
        responseDefinitionBuilder.withBody(bodyString);
    }
}
