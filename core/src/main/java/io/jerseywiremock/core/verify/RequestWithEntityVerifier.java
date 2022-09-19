package io.jerseywiremock.core.verify;

import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.github.tomakehurst.wiremock.matching.ContentPattern;
import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;

import io.jerseywiremock.core.stub.request.Serializer;

public abstract class RequestWithEntityVerifier<Entity, Self extends RequestWithEntityVerifier<Entity, Self>>
        extends BaseRequestVerifier<RequestWithEntityVerifier>
{
    protected Serializer serializer;

    public RequestWithEntityVerifier(
            WireMock wireMock,
            Serializer serializer,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMock, patternBuilder);
        this.serializer = serializer;
    }

    public Self withRequestEntity(Entity entity) {
        String entityString = serializer.serialize(entity);
        requestPatternBuilder.withRequestBody(equalTo(entityString));
        //noinspection unchecked
        return (Self) this;
    }

    public Self withRequestBody(ContentPattern<?> valueMatchingStrategy) {
        requestPatternBuilder.withRequestBody(valueMatchingStrategy);
        //noinspection unchecked
        return (Self) this;
    }
}
