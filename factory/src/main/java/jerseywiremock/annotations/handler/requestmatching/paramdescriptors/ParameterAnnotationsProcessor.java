package jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import com.google.common.collect.Sets;
import jerseywiremock.annotations.ParamFormat;
import jerseywiremock.annotations.ParamMatchedBy;
import jerseywiremock.annotations.ParamMatchingStrategy;
import jerseywiremock.annotations.ParamNamed;
import jerseywiremock.annotations.formatter.ParamFormatter;

import javax.ws.rs.PathParam;
import javax.ws.rs.QueryParam;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

public class ParameterAnnotationsProcessor {
    /**
     * @param targetMethod Annotations from the parameters of the target (resource) method
     * @param mockerMethod Annotations from the parameters of the mocker method
     * @return A list of ParameterDescriptor, in the order of the parameters of the mocker method
     */
    public LinkedList<ParameterDescriptor> createParameterDescriptors(Method targetMethod, Method mockerMethod) {
        LinkedList<ParameterDescriptor> parameterDescriptors = new LinkedList<>();
        Annotation[][] targetMethodParameterAnnotations = targetMethod.getParameterAnnotations();
        Annotation[][] mockerMethodParameterAnnotations = mockerMethod.getParameterAnnotations();

        TargetParamAnnotations relevantTargetParamAnnotations =
                getRelevantTargetParamAnnotations(targetMethodParameterAnnotations);

        Set<String> mockerParamNames = getMockerParamNames(mockerMethodParameterAnnotations);
        assertAllOrNoParamsAreNamed(mockerParamNames, mockerMethodParameterAnnotations);
        assertMockerParamsCanBeMappedToTargetParams(
                mockerMethod,
                mockerParamNames,
                mockerMethodParameterAnnotations,
                relevantTargetParamAnnotations,
                mockerMethodParameterAnnotations.length);

        int paramIndex = 0;
        for (Annotation[] mockerSingleParamAnnotations : mockerMethodParameterAnnotations) {
            String mockerName = getMockerParamName(mockerSingleParamAnnotations);
            TargetParamDescriptor targetParamDescriptor;
            if (mockerName != null) {
                targetParamDescriptor = relevantTargetParamAnnotations.get(mockerName);
            } else {
                targetParamDescriptor = relevantTargetParamAnnotations.get(paramIndex);
            }

            ParameterDescriptor parameterDescriptor =
                    getParameterDescriptor(targetParamDescriptor, mockerSingleParamAnnotations);
            parameterDescriptors.add(parameterDescriptor);

            paramIndex++;
        }

        return parameterDescriptors;
    }

    private Set<String> getMockerParamNames(Annotation[][] mockerMethodParameterAnnotations) {
        Set<String> names = new HashSet<>();
        Set<String> duplicates = new HashSet<>();
        for (Annotation[] mockerSingleParamAnnotations : mockerMethodParameterAnnotations) {
            String name = getMockerParamName(mockerSingleParamAnnotations);
            if (name != null) {
                if (names.contains(name)) {
                    duplicates.add(name);
                }
                names.add(name);
            }
        }
        if (!duplicates.isEmpty()) {
            throw new RuntimeException("Named parameters must be unique, but the following are duplicated: "
                    + duplicates);
        }
        return names;
    }

    private void assertAllOrNoParamsAreNamed(
            Set<String> mockerParamNames,
            Annotation[][] mockerMethodParameterAnnotations
    ) {
        if (mockerParamNames.size() != 0 && mockerParamNames.size() != mockerMethodParameterAnnotations.length) {
            throw new RuntimeException(
                    "Only some parameters were annotated with @ParamNamed; either all must be, or none");
        }
    }

    private void assertMockerParamsCanBeMappedToTargetParams(
            Method mockerMethod,
            Set<String> mockerParamNames,
            Annotation[][] mockerMethodParameterAnnotations,
            TargetParamAnnotations targetParamAnnotations,
            int mockerParamsCount
    ) {
        if (mockerParamNames.size() == 0 && mockerMethodParameterAnnotations.length > 0) {
            int relevantTargetParamsCount = targetParamAnnotations.annotationsByOrder.size();
            if (relevantTargetParamsCount != mockerParamsCount) {
                throw new RuntimeException("Expected " + mockerMethod.getName() + " to have " +
                        relevantTargetParamsCount + " param(s), but has " + mockerParamsCount);
            }
        } else {
            Sets.SetView<String> missingPathParamNames =
                    Sets.difference(targetParamAnnotations.pathParamNames, mockerParamNames);
            if (missingPathParamNames.size() > 0) {
                throw new RuntimeException("Expected " + mockerMethod.getName() + " to specify all path parameters, " +
                        "but the following are missing: " + missingPathParamNames);
            }
        }
    }

    private TargetParamAnnotations getRelevantTargetParamAnnotations(Annotation[][] targetMethodParameterAnnotations) {
        List<TargetParamDescriptor> annotationsByOrder = new ArrayList<>();
        Map<String, TargetParamDescriptor> annotationsByName = new HashMap<>();

        for (Annotation[] targetSingleParamAnnotations : targetMethodParameterAnnotations) {
            if (!includesQueryOrPathParams(targetSingleParamAnnotations)) {
                continue;
            }

            ParamType paramType = getParamType(targetSingleParamAnnotations);
            String paramName = getTargetParamName(targetSingleParamAnnotations);
            Class<? extends ParamFormatter> formatter = getParamFormatter(targetSingleParamAnnotations);

            TargetParamDescriptor descriptor = new TargetParamDescriptor(paramType, paramName, formatter);

            annotationsByOrder.add(descriptor);
            annotationsByName.put(getTargetParamName(targetSingleParamAnnotations), descriptor);
        }

        return new TargetParamAnnotations(annotationsByOrder, annotationsByName);
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
            TargetParamDescriptor targetParamDescriptor,
            Annotation[] mockerParamAnnotations
    ) {
        // Param matching strategies do not make sense for path params, so are ignored
        ParamMatchingStrategy matchingStrategy = null;
        if (targetParamDescriptor.type == ParamType.QUERY) {
            matchingStrategy = getParamMatchingStrategy(mockerParamAnnotations);
        }

        return new ParameterDescriptor(
                targetParamDescriptor.type,
                targetParamDescriptor.name,
                targetParamDescriptor.formatter,
                matchingStrategy);
    }

    private String getTargetParamName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof QueryParam) {
                return ((QueryParam) annotation).value();
            } else if (annotation instanceof PathParam) {
                return ((PathParam) annotation).value();
            }
        }
        throw new RuntimeException("Trying to create ParameterDescriptor for neither @QueryParam nor @PathParam");
    }

    private String getMockerParamName(Annotation[] annotations) {
        for (Annotation annotation : annotations) {
            if (annotation instanceof ParamNamed) {
                return ((ParamNamed) annotation).value();
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
        throw new RuntimeException("Trying to create ParameterDescriptor for neither @QueryParam nor @PathParam");
    }

    private ParamMatchingStrategy getParamMatchingStrategy(Annotation[] annotations) {
        for (Annotation parameterAnnotation : annotations) {
            if (parameterAnnotation instanceof ParamMatchedBy) {
                return ((ParamMatchedBy) parameterAnnotation).value();
            }
        }
        return ParamMatchingStrategy.EQUAL_TO;
    }

    private static class TargetParamAnnotations {
        private final List<TargetParamDescriptor> annotationsByOrder;
        private final Map<String, TargetParamDescriptor> annotationsByName;
        private final Set<String> pathParamNames;

        public TargetParamAnnotations(
                List<TargetParamDescriptor> annotationsByOrder,
                Map<String, TargetParamDescriptor> annotationsByName
        ) {
            this.annotationsByOrder = annotationsByOrder;
            this.annotationsByName = annotationsByName;

            pathParamNames = new HashSet<>();
            for (TargetParamDescriptor descriptor : annotationsByOrder) {
                if (descriptor.type == ParamType.PATH) {
                    pathParamNames.add(descriptor.name);
                }
            }
        }

        public TargetParamDescriptor get(int index) {
            return annotationsByOrder.get(index);
        }

        public TargetParamDescriptor get(String name) {
            return annotationsByName.get(name);
        }
    }

    private static class TargetParamDescriptor {
        private final ParamType type;
        private final String name;
        private final Class<? extends ParamFormatter> formatter;

        public TargetParamDescriptor(
                ParamType type,
                String name,
                Class<? extends ParamFormatter> formatter
        ) {
            this.type = type;
            this.name = name;
            this.formatter = formatter;
        }
    }
}
