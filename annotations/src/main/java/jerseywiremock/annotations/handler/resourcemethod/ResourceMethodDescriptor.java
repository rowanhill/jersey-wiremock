package jerseywiremock.annotations.handler.resourcemethod;

public class ResourceMethodDescriptor {
    private final Class<?> resourceClass;
    private final String methodName;
    private final HttpVerb verb;

    ResourceMethodDescriptor(
            Class<?> resourceClass,
            String methodName,
            HttpVerb verb
    ) {
        this.resourceClass = resourceClass;
        this.methodName = methodName;
        this.verb = verb;
    }

    /**
     * The class of the Jersey resource being described
     */
    public Class<?> getResourceClass() {
        return resourceClass;
    }

    /**
     * The name of the method being described on the Jersey resource
     */
    public String getMethodName() {
        return methodName;
    }

    /**
     * Throws a RuntimeException if the resource method was not annotated with the expected HTTP verb
     */
    public void assertVerb(HttpVerb verb) {
        if (this.verb != verb) {
            throw new RuntimeException("Expected " + methodName + " to be annotated with @"
                    + verb.getAnnotation().getSimpleName());
        }
    }
}
