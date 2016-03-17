package jerseywiremock.annotations.handler;

import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.WireMockForResource;
import jerseywiremock.annotations.WireMockStub;
import jerseywiremock.annotations.WireMockVerify;
import jerseywiremock.core.ReflectionHelper;
import jerseywiremock.core.UrlPathBuilder;
import jerseywiremock.core.stub.GetRequestMocker;
import jerseywiremock.core.stub.ListRequestMocker;
import jerseywiremock.core.verify.GetRequestVerifier;
import jerseywiremock.formatter.ParamFormatter;
import net.bytebuddy.implementation.bind.annotation.AllArguments;
import net.bytebuddy.implementation.bind.annotation.Origin;
import net.bytebuddy.implementation.bind.annotation.This;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class MockerInvocationHandler {
    public <T> GetRequestMocker<T> handleStubGet(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockStub.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, urlPath);
    }

    public <T> ListRequestMocker<T> handleStubList(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockStub.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        Collection<T> collection = CollectionFactory.createCollection(resourceClass, methodName);
        return new ListRequestMocker<T>(mocker.wireMockServer, mocker.objectMapper, urlPath, collection);
    }

    public GetRequestVerifier handleVerifyGetVerb(@AllArguments Object[] parameters, @This BaseMocker mocker, @Origin Method method) {
        Class<?> resourceClass = method.getDeclaringClass().getAnnotation(WireMockForResource.class).value();
        String methodName = method.getAnnotation(WireMockVerify.class).value();

        // TODO: Check method is @GET annotated
        Map<String, Object> paramMap = getParamMap(parameters, resourceClass, methodName);
        String urlPath = UrlPathBuilder.buildUrlPath(resourceClass, methodName, paramMap);
        return new GetRequestVerifier(mocker.wireMockServer, urlPath);
    }

    // TODO: Extract into separate class
    private Map<String, Object> getParamMap(Object[] parameters, Class<?> resourceClass, String methodName) {
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
