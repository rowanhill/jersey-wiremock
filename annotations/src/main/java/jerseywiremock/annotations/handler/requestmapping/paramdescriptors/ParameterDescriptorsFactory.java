package jerseywiremock.annotations.handler.requestmapping.paramdescriptors;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.ParamMatchedBy;
import jerseywiremock.annotations.handler.util.ReflectionHelper;
import jerseywiremock.formatter.ParamFormatter;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ParameterDescriptorsFactory {
    private final ValueMatchingStrategyFactory valueMatchingStrategyFactory;

    public ParameterDescriptorsFactory(ValueMatchingStrategyFactory valueMatchingStrategyFactory) {
        this.valueMatchingStrategyFactory = valueMatchingStrategyFactory;
    }

    public ParameterDescriptors createParameterDescriptors(
            Object[] parameters,
            Annotation[][] mockerMethodParameterAnnotations,
            Class<?> resourceClass,
            String targetMethodName
    ) {
        Method targetMethod = ReflectionHelper.getMethod(resourceClass, targetMethodName);

        LinkedList<ParameterDescriptor> parameterDescriptors =
                getParameterDescriptors(targetMethod, mockerMethodParameterAnnotations);

        if (parameterDescriptors.size() != parameters.length) {
            throw new RuntimeException("Invocation of " + targetMethodName + " had " + parameters.length +
                    " params, but " + parameterDescriptors.size() + " are desired");
        }

        return buildParameterDescriptorsObject(parameters, parameterDescriptors);
    }

    private LinkedList<ParameterDescriptor> getParameterDescriptors(
            Method targetMethod,
            Annotation[][] mockerMethodParameterAnnotations
    ) {
        LinkedList<ParameterDescriptor> parameterDescriptors = new LinkedList<>();
        Annotation[][] targetMethodParameterAnnotations = targetMethod.getParameterAnnotations();

        int mockerMethodParamIndex = 0;
        for (Annotation[] targetSingleParamAnnotations : targetMethodParameterAnnotations) {
            if (!isQueryOrPathOrUnannotated(targetSingleParamAnnotations)) {
                continue;
            }
            // TODO: Address mockerMethodParamIndex being out of bounds - means mocker method has too few params
            Annotation[] mockerSingleParamAnnotations = mockerMethodParameterAnnotations[mockerMethodParamIndex];

            ParameterDescriptor parameterDescriptor =
                    getParameterDescriptor(targetSingleParamAnnotations, mockerSingleParamAnnotations);
            parameterDescriptors.add(parameterDescriptor);

            mockerMethodParamIndex++;
        }
        return parameterDescriptors;
    }

    private boolean isQueryOrPathOrUnannotated(Annotation[] annotations) {
        return includesQueryOrPathParams(annotations) || !includesJaxRsAnnotation(annotations);
    }

    private boolean includesQueryOrPathParams(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam || annotation instanceof PathParam) {
                return true;
            }
        }
        return false;
    }

    private boolean includesJaxRsAnnotation(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation.annotationType().getPackage().getName().startsWith("javax.ws.rs")) {
                return true;
            }
        }
        return false;
    }

    private ParameterDescriptor getParameterDescriptor(
            Annotation[] targetParamAnnotations,
            Annotation[] mockerParamAnnotations
    ) {
        String paramName = getParamName(targetParamAnnotations);
        Class<? extends ParamFormatter> formatter = getParamFormatter(targetParamAnnotations);
        ParamType paramType = getParamType(targetParamAnnotations);

        // Param matching strategies do not make sense for path params, so are ignored
        ParamMatchingStrategy matchingStrategy = null;
        if (paramType == ParamType.QUERY || paramType == ParamType.ENTITY) {
            matchingStrategy = getParamMatchingStrategy(mockerParamAnnotations);
        }

        return new ParameterDescriptor(paramType, paramName, formatter, matchingStrategy);
    }

    private String getParamName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                return ((QueryParam) annotation).value();
            } else if (annotation instanceof PathParam) {
                return ((PathParam) annotation).value();
            }
        }
        return null;
    }

    private Class<? extends ParamFormatter> getParamFormatter(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ParamFormat) {
                return ((ParamFormat) annotation).value();
            }
        }
        return null;
    }

    private ParamType getParamType(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                return ParamType.QUERY;
            } else if (annotation instanceof PathParam) {
                return ParamType.PATH;
            }
        }
        return ParamType.ENTITY;
    }

    private ParamMatchingStrategy getParamMatchingStrategy(Annotation[] annotations) {
        for (Annotation parameterAnnotation : annotations) {
            if (parameterAnnotation instanceof ParamMatchedBy) {
                return ((ParamMatchedBy) parameterAnnotation).value();
            }
        }
        return ParamMatchingStrategy.EQUAL_TO;
    }

    private ParameterDescriptors buildParameterDescriptorsObject(
            Object[] parameters,
            LinkedList<ParameterDescriptor> parameterDescriptors
    ) {
        Map<String, String> pathParams = new HashMap<>();
        Map<String, ValueMatchingStrategy> queryParamMatchingStrategies = new HashMap<>();
        ValueMatchingStrategy requestBodyMatchingStrategy = null;

        for (int i = 0; i < parameterDescriptors.size(); i++) {
            ParameterDescriptor parameterDescriptor = parameterDescriptors.get(i);
            Object rawParamValue = parameters[i];

            if (parameterDescriptor.paramType == ParamType.PATH) {
                String formattedValue = getFormattedParamValue(rawParamValue, parameterDescriptor.formatterClass);
                pathParams.put(parameterDescriptor.paramName, formattedValue);
            } else if (parameterDescriptor.paramType == ParamType.QUERY) {
                String stringValue;
                if (rawParamValue instanceof String) {
                    stringValue = (String) rawParamValue;
                } else {
                    stringValue = getFormattedParamValue(rawParamValue, parameterDescriptor.formatterClass);
                }
                ValueMatchingStrategy valueMatchingStrategy = valueMatchingStrategyFactory
                        .toValueMatchingStrategy(parameterDescriptor.matchingStrategy, stringValue);
                queryParamMatchingStrategies.put(parameterDescriptor.paramName, valueMatchingStrategy);
            } else { // Request entity
                String formattedValue = getFormattedParamValue(rawParamValue, parameterDescriptor.formatterClass);
                requestBodyMatchingStrategy = valueMatchingStrategyFactory
                        .toValueMatchingStrategy(parameterDescriptor.matchingStrategy, formattedValue);
            }
        }

        return new ParameterDescriptors(pathParams, queryParamMatchingStrategies, requestBodyMatchingStrategy);
    }

    private String getFormattedParamValue(Object rawParamValue, Class<? extends ParamFormatter> formatterClass) {
        String formattedValue;
        if (formatterClass != null) {
            ParamFormatter formatter;
            try {
                formatter = formatterClass.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException("Could not instantiate formatter " + formatterClass.getSimpleName(), e);
            }

            // ParamFormatter is generic, and the generic type is erased by run-time, so we can't check whether
            // rawParamValue is of the right type or not...
            //noinspection unchecked
            formattedValue = formatter.format(rawParamValue);
        } else {
            formattedValue = rawParamValue.toString();
        }
        return formattedValue;
    }

    private enum ParamType {
        PATH,
        QUERY,
        ENTITY
    }

    private static class ParameterDescriptor {
        private final ParamType paramType;
        private final String paramName;
        private final Class<? extends ParamFormatter> formatterClass;
        private final ParamMatchingStrategy matchingStrategy;

        public ParameterDescriptor(
                ParamType paramType,
                String paramName,
                Class<? extends ParamFormatter> formatterClass,
                ParamMatchingStrategy matchingStrategy
        ) {
            this.paramType = paramType;
            this.paramName = paramName;
            this.formatterClass = formatterClass;
            this.matchingStrategy = matchingStrategy;
        }
    }
}
