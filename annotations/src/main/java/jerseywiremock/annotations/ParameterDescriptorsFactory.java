package jerseywiremock.annotations;

import jerseywiremock.core.ParamMatchingStrategy;
import jerseywiremock.core.ParameterDescriptors;
import jerseywiremock.core.QueryParamMatchDescriptor;
import jerseywiremock.core.ReflectionHelper;
import jerseywiremock.formatter.ParamFormatter;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ParameterDescriptorsFactory {
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
            if (!includesQueryOrPathParams(targetSingleParamAnnotations)) {
                continue;
            }
            Annotation[] mockerSingleParamAnnotations = mockerMethodParameterAnnotations[mockerMethodParamIndex];


            ParameterDescriptor parameterDescriptor =
                    getParameterDescriptor(targetSingleParamAnnotations, mockerSingleParamAnnotations);
            parameterDescriptors.add(parameterDescriptor);

            mockerMethodParamIndex++;
        }
        return parameterDescriptors;
    }

    private boolean includesQueryOrPathParams(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam || annotation instanceof PathParam) {
                return true;
            }
        }
        return false;
    }

    private ParameterDescriptor getParameterDescriptor(
            Annotation[] targetParamAnnotations,
            Annotation[] mockerParamAnnotations
    ) {
        String paramName = null;
        Class<? extends ParamFormatter> formatter = null;
        Class<? extends Annotation> paramType = null;
        for (Annotation parameterAnnotation : targetParamAnnotations) {
            if (parameterAnnotation instanceof QueryParam) {
                paramName = ((QueryParam) parameterAnnotation).value();
                paramType = QueryParam.class;
            } else if (parameterAnnotation instanceof PathParam) {
                paramName = ((PathParam) parameterAnnotation).value();
                paramType = PathParam.class;
            } else if (parameterAnnotation instanceof ParamFormat) {
                formatter = ((ParamFormat) parameterAnnotation).value();
            }
        }

        // Resource method params with @QueryParam can have their equivalent mocker method param annotated with
        // @ParamMatchedBy to specify a WireMock value matching strategy. If @ParamMatchedBy is absent, this defaults
        // to equality.
        ParamMatchingStrategy matchingStrategy = ParamMatchingStrategy.EQUAL_TO;
        if (paramType == QueryParam.class) {
            for (Annotation parameterAnnotation : mockerParamAnnotations) {
                if (parameterAnnotation instanceof ParamMatchedBy) {
                    matchingStrategy = ((ParamMatchedBy) parameterAnnotation).value();
                }
            }
        }

        return new ParameterDescriptor(paramType, paramName, formatter, matchingStrategy);
    }

    private ParameterDescriptors buildParameterDescriptorsObject(
            Object[] parameters,
            LinkedList<ParameterDescriptor> parameterDescriptors
    ) {
        Map<String, String> pathParms = new HashMap<>();
        List<QueryParamMatchDescriptor> queryParamMatchDescriptors = new LinkedList<>();

        for (int i = 0; i < parameterDescriptors.size(); i++) {
            ParameterDescriptor parameterDescriptor = parameterDescriptors.get(i);
            Object rawParamValue = parameters[i];

            if (parameterDescriptor.paramType == PathParam.class) {
                String formattedValue = getFormattedParamValue(rawParamValue, parameterDescriptor.formatterClass);
                pathParms.put(parameterDescriptor.paramName, formattedValue);
            } else { // QueryParam
                String stringValue;
                if (rawParamValue instanceof String) {
                    stringValue = (String) rawParamValue;
                } else {
                    stringValue = getFormattedParamValue(rawParamValue, parameterDescriptor.formatterClass);
                }
                QueryParamMatchDescriptor queryParamMatchDescriptor = new QueryParamMatchDescriptor(
                        parameterDescriptor.paramName,
                        stringValue,
                        parameterDescriptor.matchingStrategy);
                queryParamMatchDescriptors.add(queryParamMatchDescriptor);
            }
        }

        return new ParameterDescriptors(pathParms, queryParamMatchDescriptors);
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

    private static class ParameterDescriptor {
        private final Class<? extends Annotation> paramType;
        private final String paramName;
        private final Class<? extends ParamFormatter> formatterClass;
        private final ParamMatchingStrategy matchingStrategy;

        public ParameterDescriptor(
                Class<? extends Annotation> paramType,
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
