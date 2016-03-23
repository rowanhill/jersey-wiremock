package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import jerseywiremock.core.RequestMappingDescriptorFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ImplementationDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class MockerFactory {
    public static <T> T wireMockerFor(Class<T> mockerType, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        MockerInvocationHandler handler = createHandler();
        ByteBuddy byteBuddy = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"));

        ImplementationDefinition<? extends BaseMocker> implDef = createImplementationDefinition(mockerType, byteBuddy);

        Class<? extends BaseMocker> mockerSubclass = createClass(handler, implDef);

        //noinspection unchecked
        return (T) mockerSubclass
                .getConstructor(WireMockServer.class, ObjectMapper.class)
                .newInstance(wireMockServer, objectMapper);
    }

    private static MockerInvocationHandler createHandler() {
        return new MockerInvocationHandler(new ParameterDescriptorsFactory(), new RequestMappingDescriptorFactory());
    }

    private static <T> ImplementationDefinition<? extends BaseMocker> createImplementationDefinition(
            Class<T> mockerType,
            ByteBuddy byteBuddy
    ) {
        ImplementationDefinition<? extends BaseMocker> implementationDefinition;
        if (mockerType.isInterface()) {
            implementationDefinition = createInterfaceImplementationDefinition(mockerType, byteBuddy);
        } else {
            implementationDefinition = createClassImplementationDefinition(mockerType, byteBuddy);
        }
        return implementationDefinition;
    }

    private static <T> ImplementationDefinition<? extends BaseMocker> createInterfaceImplementationDefinition(
            Class<T> mockerType,
            ByteBuddy byteBuddy
    ) {
        return byteBuddy
                .subclass(BaseMocker.class)
                .implement(mockerType)
                .method(isDeclaredBy(mockerType));
    }

    private static <T> ImplementationDefinition<? extends BaseMocker> createClassImplementationDefinition(
            Class<T> mockerType,
            ByteBuddy byteBuddy
    ) {
        checkExtendsBaseMocker(mockerType);

        // We know mockerType is a class that extends BaseMocker, thanks to the isAssignableFrom check above
        //noinspection unchecked
        return byteBuddy
                .subclass((Class<? extends BaseMocker>) mockerType)
                .method(isDeclaredBy(mockerType).and(isAbstract()));
    }

    private static <T> void checkExtendsBaseMocker(Class<T> mockerType) {
        if (!BaseMocker.class.isAssignableFrom(mockerType)) {
            throw new RuntimeException("For an abstract class to be implemented it must subclass BaseMocker. " +
                    mockerType.getSimpleName() + " does not.");
        }
    }

    private static Class<? extends BaseMocker> createClass(
            MockerInvocationHandler handler,
            ImplementationDefinition<? extends BaseMocker> implDef
    ) {
        return implDef
                .intercept(MethodDelegation.to(handler))
                .make()
                .load(MockerFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();
    }
}
