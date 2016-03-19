package jerseywiremock.annotations;

import jerseywiremock.core.ReflectionHelper;
import jerseywiremock.formatter.ParamFormatter;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ParamMapBuilder {
    public Map<String, Object> getParamMap(Object[] parameters, Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        LinkedList<ParameterDescriptor> parameterDescriptors = getParameterDescriptors(method);

        if (parameterDescriptors.size() != parameters.length) {
            throw new RuntimeException("Invocation of " + methodName + " had " + parameters.length + " params, but " + parameterDescriptors.size() + " are desired");
        }

        return buildParamMap(parameters, parameterDescriptors);
    }

    private LinkedList<ParameterDescriptor> getParameterDescriptors(Method method) {
        LinkedList<ParameterDescriptor> parameterDescriptors = new LinkedList<ParameterDescriptor>();
        for (Annotation[] parameterAnnotations : method.getParameterAnnotations()) {
            ParameterDescriptor parameterDescriptor = getParameterDescriptor(parameterAnnotations);

            if (parameterDescriptor.paramName != null) {
                parameterDescriptors.add(parameterDescriptor);
            }
        }
        return parameterDescriptors;
    }

    private ParameterDescriptor getParameterDescriptor(Annotation[] parameterAnnotations) {
        ParameterDescriptor parameterDescriptor;

        String paramName = null;
        Class<? extends ParamFormatter> formatter = null;
        for (Annotation parameterAnnotation : parameterAnnotations) {
            if (parameterAnnotation instanceof QueryParam) {
                paramName = ((QueryParam) parameterAnnotation).value();
            } else if (parameterAnnotation instanceof PathParam) {
                paramName = ((PathParam) parameterAnnotation).value();
            } else if (parameterAnnotation instanceof ParamFormat) {
                formatter = ((ParamFormat) parameterAnnotation).value();
            }
        }

        parameterDescriptor = new ParameterDescriptor(paramName, formatter);
        return parameterDescriptor;
    }

    private Map<String, Object> buildParamMap(
            Object[] parameters,
            LinkedList<ParameterDescriptor> parameterDescriptors
    ) {
        Map<String, Object> paramMap = new HashMap<String, Object>();
        for (int i = 0; i < parameterDescriptors.size(); i++) {
            String paramName = parameterDescriptors.get(i).paramName;
            Class<? extends ParamFormatter> formatterClass = parameterDescriptors.get(i).formatterClass;
            Object rawParamValue = parameters[i];

            String formattedValue = getFormattedParamValue(rawParamValue, formatterClass);

            paramMap.put(paramName, formattedValue);
        }
        return paramMap;
    }

    private String getFormattedParamValue(Object rawParamValue, Class<? extends ParamFormatter> formatterClass) {
        String formattedValue;
        if (formatterClass != null) {
            ParamFormatter formatter;
            try {
                formatter = formatterClass.newInstance();
            } catch (InstantiationException e) {
                throw new RuntimeException(e);
            } catch (IllegalAccessException e) {
                throw new RuntimeException(e);
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
        private final String paramName;
        private final Class<? extends ParamFormatter> formatterClass;

        public ParameterDescriptor(
                String paramName,
                Class<? extends ParamFormatter> formatterClass
        ) {
            this.paramName = paramName;
            this.formatterClass = formatterClass;
        }
    }
}
