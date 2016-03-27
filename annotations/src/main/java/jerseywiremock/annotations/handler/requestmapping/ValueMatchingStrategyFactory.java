package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class ValueMatchingStrategyFactory {
    public ValueMatchingStrategy createValueMatchingStrategy(ParamMatchingStrategy matchingStrategy, String value) {
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
