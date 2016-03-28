package jerseywiremock.annotations.handler.requestmatching.paramdescriptors;

import jerseywiremock.annotations.ParamMatchingStrategy;
import jerseywiremock.annotations.formatter.ParamFormatter;

public class ParameterDescriptor {
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

    public ParamType getParamType() {
        return paramType;
    }

    public String getParamName() {
        return paramName;
    }

    public Class<? extends ParamFormatter> getFormatterClass() {
        return formatterClass;
    }

    public ParamMatchingStrategy getMatchingStrategy() {
        return matchingStrategy;
    }
}
