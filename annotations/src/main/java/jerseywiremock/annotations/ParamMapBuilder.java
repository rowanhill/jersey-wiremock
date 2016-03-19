package jerseywiremock.annotations;

import jerseywiremock.core.ReflectionHelper;
import jerseywiremock.formatter.ParamFormatter;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ParamMapBuilder {
    public Map<String, Object> getParamMap(Object[] parameters, Class<?> resourceClass, String methodName) {
        Method method = ReflectionHelper.getMethod(resourceClass, methodName);

        List<String> paramNames = new ArrayList<String>();
        Map<String, Class<? extends ParamFormatter>> formatters = new HashMap<String, Class<? extends ParamFormatter>>();
        for (Annotation[] parameterAnnotations : method.getParameterAnnotations()) {
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

            if (paramName != null) {
                paramNames.add(paramName);
                if (formatter != null) {
                    formatters.put(paramName, formatter);
                }
            }
        }

        if (paramNames.size() != parameters.length) {
            throw new RuntimeException("Invocation of " + methodName + " had " + parameters.length + " params, but " + paramNames.size() + " are desired");
        }

        Map<String, Object> paramMap = new HashMap<String, Object>();
        for (int i = 0; i < paramNames.size(); i++) {
            String paramName = paramNames.get(i);
            Object rawParamValue = parameters[i];

            String formattedValue;
            if (formatters.containsKey(paramName)) {
                Class<? extends ParamFormatter> formatterClass = formatters.get(paramName);
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

            paramMap.put(paramName, formattedValue);
        }

        return paramMap;
    }
}
