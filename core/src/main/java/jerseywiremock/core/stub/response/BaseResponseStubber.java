package jerseywiremock.core.stub.response;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.ResponseDefinitionBuilder;
import com.github.tomakehurst.wiremock.http.Fault;

import static com.github.tomakehurst.wiremock.client.WireMock.aResponse;

public abstract class BaseResponseStubber<SelfType extends BaseResponseStubber> {
    protected final WireMockServer wireMockServer;
    protected final ObjectMapper objectMapper;
    protected final MappingBuilder mappingBuilder;
    protected final ResponseDefinitionBuilder responseDefinitionBuilder;

    public BaseResponseStubber(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            MappingBuilder mappingBuilder,
            ResponseDefinitionBuilder responseDefinitionBuilder
    ) {
        this.wireMockServer = wireMockServer;
        this.objectMapper = objectMapper;
        this.mappingBuilder = mappingBuilder;

        if (responseDefinitionBuilder != null) {
            this.responseDefinitionBuilder = responseDefinitionBuilder;
        } else {
            this.responseDefinitionBuilder = aResponse().withHeader("Content-Type", "application/json");
        }
    }

    public SelfType withStatusCode(int statusCode) {
        responseDefinitionBuilder.withStatus(statusCode);
        //noinspection unchecked
        return (SelfType) this;
    }

    public SelfType withFault(Fault fault) {
        responseDefinitionBuilder.withFault(fault);
        //noinspection unchecked
        return (SelfType) this;
    }

    public void stub() throws JsonProcessingException {
        amendResponseDefinition(responseDefinitionBuilder);

        wireMockServer.stubFor(mappingBuilder.willReturn(responseDefinitionBuilder));
    }

    protected abstract void amendResponseDefinition(ResponseDefinitionBuilder responseDefinitionBuilder) throws JsonProcessingException;
}
