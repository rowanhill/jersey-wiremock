package jerseywiremock.core;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;

import static com.github.tomakehurst.wiremock.client.WireMock.*;

public class StubOrVerifyQueryParamAdder {
    private MappingBuilder mappingBuilder;
    private RequestPatternBuilder requestPatternBuilder;

    public StubOrVerifyQueryParamAdder(MappingBuilder mappingBuilder) {
        this.mappingBuilder = mappingBuilder;
    }

    public StubOrVerifyQueryParamAdder(RequestPatternBuilder requestPatternBuilder) {
        this.requestPatternBuilder = requestPatternBuilder;
    }

    public void addQueryParameters(RequestMappingDescriptor mappingDescriptor) {
        for (QueryParamMatchDescriptor queryParamMatchDescriptor : mappingDescriptor.getQueryParamMatchDescriptors()) {
            ParamMatchingStrategy matchingStrategy = queryParamMatchDescriptor.getMatchingStrategy();

            String paramValue = queryParamMatchDescriptor.getValue();
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
                case EQUAL_TO_JSON:
                    valueMatchingStrategy = equalToJson(paramValue);
                    break;
                case EQUAL_TO_XML:
                    valueMatchingStrategy = equalToXml(paramValue);
                    break;
                case MATCHING_XPATH:
                    valueMatchingStrategy = matchingXPath(paramValue);
                    break;
                case MATCHING_JSON_PATH:
                    valueMatchingStrategy = matchingJsonPath(paramValue);
                    break;
                default:
                    throw new RuntimeException("Unexpected matching strategy " + matchingStrategy.name());
            }

            addQueryParam(queryParamMatchDescriptor.getParamName(), valueMatchingStrategy);
        }
    }

    private void addQueryParam(String paramName, ValueMatchingStrategy valueMatchingStrategy) {
        if (mappingBuilder != null) {
            mappingBuilder.withQueryParam(paramName, valueMatchingStrategy);
        } else {
            requestPatternBuilder.withQueryParam(paramName, valueMatchingStrategy);
        }
    }
}
