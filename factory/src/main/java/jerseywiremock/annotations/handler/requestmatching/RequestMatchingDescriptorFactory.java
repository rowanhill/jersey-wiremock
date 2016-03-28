package jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamType;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterDescriptor;

import javax.ws.rs.core.UriBuilder;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

public class RequestMatchingDescriptorFactory {
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
            UriBuilder uriBuilder
    ) {
        LinkedList<ParameterDescriptor> parameterDescriptors = parameterAnnotationsProcessor
                .createParameterDescriptors(targetMethod, mockerMethod);

        if (parameterDescriptors.size() != parameters.length) {
            throw new RuntimeException("Invocation of " + targetMethod.getName() + " had " + parameters.length +
                    " params, but " + parameterDescriptors.size() + " are desired");
        }

        return buildParameterDescriptorsObject(parameters, parameterDescriptors, uriBuilder);
    }

    private RequestMatchingDescriptor buildParameterDescriptorsObject(
            Object[] parameters,
            LinkedList<ParameterDescriptor> parameterDescriptors,
            UriBuilder uriBuilder
    ) {
        Builder builder = new Builder();

        for (int i = 0; i < parameterDescriptors.size(); i++) {
            ParameterDescriptor parameterDescriptor = parameterDescriptors.get(i);
            Object rawParamValue = parameters[i];

            builder.addParameter(parameterDescriptor, rawParamValue);
        }

        return builder.build(uriBuilder);
    }

    private class Builder {
        private final PathParamBuilder pathParamBuilder = new PathParamBuilder();
        private final QueryParamBuilder queryParamBuilder = new QueryParamBuilder();
        private final EntityParamBuilder entryParamBuilder = new EntityParamBuilder();

        public void addParameter(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            if (parameterDescriptor.getParamType() == ParamType.PATH) {
                pathParamBuilder.addPathParam(parameterDescriptor, rawParamValue);
            } else if (parameterDescriptor.getParamType() == ParamType.QUERY) {
                queryParamBuilder.addQueryParam(parameterDescriptor, rawParamValue);
            } else {
                entryParamBuilder.addEntityParam(parameterDescriptor, rawParamValue);
            }
        }

        public RequestMatchingDescriptor build(UriBuilder uriBuilder) {
            return new RequestMatchingDescriptor(
                    pathParamBuilder.build(uriBuilder),
                    queryParamBuilder.build(),
                    entryParamBuilder.build()
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
        private ListMultimap<String, ValueMatchingStrategy> queryParamMatchingStrategies = ArrayListMultimap.create();

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
                return (String) rawParamValue;
            } else {
                return paramFormatterInvoker.getFormattedParamValue(
                        rawParamValue,
                        parameterDescriptor.getFormatterClass());
            }
        }

        public ListMultimap<String, ValueMatchingStrategy> build() {
            return queryParamMatchingStrategies;
        }
    }

    private class EntityParamBuilder {
        private ValueMatchingStrategy requestBodyMatchingStrategy = null;

        public void addEntityParam(ParameterDescriptor parameterDescriptor, Object rawParamValue) {
            String formattedValue = paramFormatterInvoker.getFormattedParamValue(
                    rawParamValue,
                    parameterDescriptor.getFormatterClass());
            requestBodyMatchingStrategy = valueMatchingStrategyFactory
                    .createValueMatchingStrategy(parameterDescriptor.getMatchingStrategy(), formattedValue);
        }

        public ValueMatchingStrategy build() {
            return requestBodyMatchingStrategy;
        }
    }
}
