package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ValueMatchDescriptor {
    private final String value;
    private final ParamMatchingStrategy matchingStrategy;

    public ValueMatchDescriptor(
            String value,
            ParamMatchingStrategy matchingStrategy
    ) {
        this.value = value;
        this.matchingStrategy = matchingStrategy;
    }

    public String getValue() {
        return value;
    }

    public ParamMatchingStrategy getMatchingStrategy() {
        return matchingStrategy;
    }

    public ValueMatchingStrategy toValueMatchingStrategy() {
        // TODO: Commonise with StubOrVerifyQueryParamAdder
        switch (matchingStrategy) {
            case EQUAL_TO:
                return equalTo(value);
            case CONTAINING:
                return containing(value);
            case MATCHING:
                return matching(value);
            case NOT_MATCHING:
                return notMatching(value);
            default:
                throw new RuntimeException("Unexpected matching strategy " + matchingStrategy.name());
        }
    }
}
