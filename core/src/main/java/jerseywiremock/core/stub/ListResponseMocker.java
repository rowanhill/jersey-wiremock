package jerseywiremock.core.stub;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;

import java.util.Collection;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public class ListResponseMocker<Entity> {
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
