package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import jerseywiremock.core.RequestMappingDescriptorFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class MockerFactory {
    public static <T> T wireMockerFor(Class<T> mockerType, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        MockerInvocationHandler handler = new MockerInvocationHandler(
                new ParameterDescriptorsFactory(),
                new RequestMappingDescriptorFactory());

        ByteBuddy byteBuddy = new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"));

        DynamicType.Builder.MethodDefinition.ImplementationDefinition<? extends BaseMocker> implementationDefinition;
        if (mockerType.isInterface()) {
            implementationDefinition = byteBuddy
                    .subclass(BaseMocker.class)
                    .implement(mockerType)
                    .method(isDeclaredBy(mockerType));
        } else {
            if (!BaseMocker.class.isAssignableFrom(mockerType)) {
                throw new RuntimeException("For an abstract class to be implemented it must subclass BaseMocker. " +
                        mockerType.getSimpleName() + " does not.");
            }

            // We know mockerType is a class that extends BaseMocker, thanks to the isAssignableFrom check above
            //noinspection unchecked
            implementationDefinition = byteBuddy
                    .subclass((Class<? extends BaseMocker>) mockerType)
                    .method(isDeclaredBy(mockerType).and(isAbstract()));
        }

        Class<? extends BaseMocker> mockerSubclass = implementationDefinition
                .intercept(MethodDelegation.to(handler))
                .make()
                .load(MockerFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        //noinspection unchecked
        return (T) mockerSubclass
                .getConstructor(WireMockServer.class, ObjectMapper.class)
                .newInstance(wireMockServer, objectMapper);
    }
}
