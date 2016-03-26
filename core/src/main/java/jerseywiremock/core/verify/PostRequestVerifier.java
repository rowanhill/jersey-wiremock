package jerseywiremock.core.verify;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

public class PostRequestVerifier<Entity> extends BaseRequestVerifier<PostRequestVerifier> {
    protected ObjectMapper objectMapper;

    public PostRequestVerifier(
            WireMockServer wireMockServer,
            ObjectMapper objectMapper,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, patternBuilder);
        this.objectMapper = objectMapper;
    }

    public PostRequestVerifier<Entity> withRequestEntity(Entity entity) throws JsonProcessingException {
        String entityString = objectMapper.writeValueAsString(entity);
        requestPatternBuilder.withRequestBody(equalTo(entityString));
        return this;
    }

    public PostRequestVerifier<Entity> withRequestBody(ValueMatchingStrategy valueMatchingStrategy) {
        requestPatternBuilder.withRequestBody(valueMatchingStrategy);
        return this;
    }
}
