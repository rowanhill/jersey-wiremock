package jerseywiremock.annotations.handler.requestmapping.queryparam;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.QueryParamMatchDescriptor;

import java.util.List;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubOrVerifyQueryParamAdder {
    private final WireMockQueryParamBuilderWrapper builderWrapper;

    public StubOrVerifyQueryParamAdder(WireMockQueryParamBuilderWrapper builderWrapper) {
        this.builderWrapper = builderWrapper;
    }

    public void addQueryParameters(List<QueryParamMatchDescriptor> queryParamMatchDescriptors) {
        for (QueryParamMatchDescriptor queryParamMatchDescriptor : queryParamMatchDescriptors) {
            ValueMatchingStrategy valueMatchingStrategy = getValueMatchingStrategy(queryParamMatchDescriptor);
            builderWrapper.withQueryParam(queryParamMatchDescriptor.getParamName(), valueMatchingStrategy);
        }
    }

    private ValueMatchingStrategy getValueMatchingStrategy(QueryParamMatchDescriptor queryParamMatchDescriptor) {
        String paramValue = queryParamMatchDescriptor.getValue();
        ParamMatchingStrategy matchingStrategy = queryParamMatchDescriptor.getMatchingStrategy();
        ValueMatchingStrategy valueMatchingStrategy;
        switch (matchingStrategy) {
            case EQUAL_TO:
                valueMatchingStrategy = equalTo(paramValue);
                break;
            case CONTAINING:
                valueMatchingStrategy = containing(paramValue);
                break;
            case MATCHING:
                valueMatchingStrategy = matching(paramValue);
                break;
            case NOT_MATCHING:
                valueMatchingStrategy = notMatching(paramValue);
                break;
            default:
                throw new RuntimeException("Unexpected matching strategy " + matchingStrategy.name());
        }
        return valueMatchingStrategy;
    }
}
