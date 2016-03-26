package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ValueMatchingStrategyFactory {
    public ValueMatchingStrategy toValueMatchingStrategy(ParamMatchingStrategy matchingStrategy, String value) {
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
