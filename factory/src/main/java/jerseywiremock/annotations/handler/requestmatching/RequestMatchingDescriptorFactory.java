package jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterDescriptor;
import org.glassfish.jersey.uri.UriComponent;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RequestMatchingDescriptorFactory {
    public enum QueryParamEncodingStrategy {
        ENCODED, UNENCODED
    }

    private final ParameterAnnotationsProcessor parameterAnnotationsProcessor;
    private final ParamFormatterInvoker paramFormatterInvoker;
    private final ValueMatchingStrategyFactory valueMatchingStrategyFactory;

    public RequestMatchingDescriptorFactory(
            ParameterAnnotationsProcessor parameterAnnotationsProcessor,
            ParamFormatterInvoker paramFormatterInvoker, ValueMatchingStrategyFactory valueMatchingStrategyFactory
    ) {
        this.parameterAnnotationsProcessor = parameterAnnotationsProcessor;
        this.paramFormatterInvoker = paramFormatterInvoker;
        this.valueMatchingStrategyFactory = valueMatchingStrategyFactory;
    }

    public RequestMatchingDescriptor createRequestMatchingDescriptor(
            Method targetMethod,
            Method mockerMethod,
            Object[] parameters,
            UriBuilder uriBuilder,
            QueryParamEncodingStrategy encodingStrategy
    ) {
        LinkedList<ParameterDescriptor> parameterDescriptors = parameterAnnotationsProcessor
                .createParameterDescriptors(targetMethod, mockerMethod);

        if (parameterDescriptors.size() != parameters.length) {
            throw new RuntimeException("Invocation of " + targetMethod.getName() + " had " + parameters.length +
                    " params, but " + parameterDescriptors.size() + " are desired");
        }

        return buildParameterDescriptorsObject(parameters, parameterDescriptors, uriBuilder, encodingStrategy);
    }

    private RequestMatchingDescriptor buildParameterDescriptorsObject(
            Object[] parameters,
            LinkedList<ParameterDescriptor> parameterDescriptors,
            UriBuilder uriBuilder,
            QueryParamEncodingStrategy encodingStrategy
    ) {
        Builder builder = new Builder(encodingStrategy);

        for (int i = 0; i < parameterDescriptors.size(); i++) {
            ParameterDescriptor parameterDescriptor = parameterDescriptors.get(i);
            Object rawParamValue = parameters[i];

            builder.addParameter(parameterDescriptor, rawParamValue);
        }

        return builder.build(uriBuilder);
    }

    private class Builder {
        private final PathParamBuilder pathParamBuilder;
        private final QueryParamBuilder queryParamBuilder;

        public Builder(QueryParamEncodingStrategy encodingStrategy) {
            this.pathParamBuilder = new PathParamBuilder();
            this.queryParamBuilder = new QueryParamBuilder(encodingStrategy);
        }

        public void addParameter(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            if (parameterDescriptor.getParamType() == ParamType.PATH) {
                pathParamBuilder.addPathParam(parameterDescriptor, rawParamValue);
            } else if (parameterDescriptor.getParamType() == ParamType.QUERY) {
                queryParamBuilder.addQueryParam(parameterDescriptor, rawParamValue);
            }
        }

        public RequestMatchingDescriptor build(UriBuilder uriBuilder) {
            return new RequestMatchingDescriptor(
                    pathParamBuilder.build(uriBuilder),
                    queryParamBuilder.build()
            );
        }
    }

    private class PathParamBuilder {
        private Map<String, String> pathParams = new HashMap<>();

        public void addPathParam(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            String formattedValue = paramFormatterInvoker.getFormattedParamValue(
                    rawParamValue,
                    parameterDescriptor.getFormatterClass());
            pathParams.put(parameterDescriptor.getParamName(), formattedValue);
        }

        public String build(UriBuilder uriBuilder) {
            return uriBuilder.buildFromMap(pathParams).toString();
        }
    }

    private class QueryParamBuilder {
        private final QueryParamEncodingStrategy encodingStrategy;
        private ListMultimap<String, ValueMatchingStrategy> queryParamMatchingStrategies = ArrayListMultimap.create();

        public QueryParamBuilder(QueryParamEncodingStrategy encodingStrategy) {
            this.encodingStrategy = encodingStrategy;
        }

        public void addQueryParam(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            if (rawParamValue instanceof Iterable) {
                addIterableQueryParam(parameterDescriptor, (Iterable) rawParamValue);
            } else {
                addSingleQueryParam(parameterDescriptor, rawParamValue);
            }
        }

        private void addIterableQueryParam(ParameterDescriptor parameterDescriptor, Iterable rawParamValue) {
            for (Object childRawParamValue : rawParamValue) {
                addSingleQueryParam(parameterDescriptor, childRawParamValue);
            }
        }

        private void addSingleQueryParam(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            String stringValue = stringifyValue(parameterDescriptor, rawParamValue);
            ValueMatchingStrategy valueMatchingStrategy = valueMatchingStrategyFactory
                    .createValueMatchingStrategy(parameterDescriptor.getMatchingStrategy(), stringValue);
            queryParamMatchingStrategies.put(parameterDescriptor.getParamName(), valueMatchingStrategy);
        }

        private String stringifyValue(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            if (rawParamValue instanceof String) {
                return urlEncodeIfNeeded((String) rawParamValue);
            } else {
                String formattedValue = paramFormatterInvoker.getFormattedParamValue(
                        rawParamValue,
                        parameterDescriptor.getFormatterClass());
                return urlEncodeIfNeeded(formattedValue);
            }
        }

        private String urlEncodeIfNeeded(String value) {
            if (encodingStrategy.equals(QueryParamEncodingStrategy.ENCODED)) {
                // Use Jersey's UriComponent for encoding, rather than Java's URLEncoder. The two can have some
                // differences, and WireMock stubbing is sensitive to encoding (in 1.x at least - might be changed in
                // 2.x). Using Jersey's encoding means that jersey-wiremock stubbing will work with jersey-client.
                return UriComponent.contextualEncode(value, UriComponent.Type.QUERY_PARAM, true);
            } else {
                return value;
            }
        }

        public ListMultimap<String, ValueMatchingStrategy> build() {
            return queryParamMatchingStrategies;
        }
    }
}
