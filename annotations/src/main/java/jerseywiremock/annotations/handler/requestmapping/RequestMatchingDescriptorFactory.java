package jerseywiremock.annotations.handler.requestmapping;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamType;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParameterDescriptor;

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
        Map<String, String> pathParams = new HashMap<>();
        Map<String, ValueMatchingStrategy> queryParamMatchingStrategies = new HashMap<>();
        ValueMatchingStrategy requestBodyMatchingStrategy = null;

        for (int i = 0; i < parameterDescriptors.size(); i++) {
            ParameterDescriptor parameterDescriptor = parameterDescriptors.get(i);
            Object rawParamValue = parameters[i];

            if (parameterDescriptor.getParamType() == ParamType.PATH) {
                String formattedValue = paramFormatterInvoker.getFormattedParamValue(
                        rawParamValue,
                        parameterDescriptor.getFormatterClass());
                pathParams.put(parameterDescriptor.getParamName(), formattedValue);
            } else if (parameterDescriptor.getParamType() == ParamType.QUERY) {
                String stringValue;
                if (rawParamValue instanceof String) {
                    stringValue = (String) rawParamValue;
                } else {
                    stringValue = paramFormatterInvoker.getFormattedParamValue(
                            rawParamValue,
                            parameterDescriptor.getFormatterClass());
                }
                ValueMatchingStrategy valueMatchingStrategy = valueMatchingStrategyFactory
                        .toValueMatchingStrategy(parameterDescriptor.getMatchingStrategy(), stringValue);
                queryParamMatchingStrategies.put(parameterDescriptor.getParamName(), valueMatchingStrategy);
            } else { // Request entity
                String formattedValue = paramFormatterInvoker.getFormattedParamValue(
                        rawParamValue,
                        parameterDescriptor.getFormatterClass());
                requestBodyMatchingStrategy = valueMatchingStrategyFactory
                        .toValueMatchingStrategy(parameterDescriptor.getMatchingStrategy(), formattedValue);
            }
        }

        String urlPath = uriBuilder.buildFromMap(pathParams).toString();

        return new RequestMatchingDescriptor(urlPath, queryParamMatchingStrategies, requestBodyMatchingStrategy);
    }
}
