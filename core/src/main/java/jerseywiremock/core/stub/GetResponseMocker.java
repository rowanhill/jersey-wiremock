package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class GetResponseMocker<Entity> {
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
