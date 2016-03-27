package jerseywiremock.annotations.factory;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory;
import jerseywiremock.annotations.handler.requestmatching.ValueMatchingStrategyFactory;
import jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;
import jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import jerseywiremock.annotations.handler.util.CollectionFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ImplementationDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;
import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class MockerFactory {
    private final MockerTypeChecker mockerTypeChecker;

    MockerFactory(MockerTypeChecker mockerTypeChecker) {
        this.mockerTypeChecker = mockerTypeChecker;
    }

    public static <T> T wireMockerFor(Class<T> mockerType, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        return new MockerFactory(new MockerTypeChecker(new MockerMethodSelector()))
                .createWireMockerFor(mockerType, wireMockServer, objectMapper);
    }

    <T> T createWireMockerFor(Class<T> mockerType, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        mockerTypeChecker.checkReturnTypes(mockerType);

        MockerInvocationHandler handler = createHandler();
        ByteBuddy byteBuddy = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"));

        ImplementationDefinition<? extends BaseMocker> implDef = createImplementationDefinition(mockerType, byteBuddy);

        Class<? extends BaseMocker> mockerSubclass = createClass(handler, implDef);

        //noinspection unchecked
        return (T) mockerSubclass
                .getConstructor(WireMockServer.class, ObjectMapper.class)
                .newInstance(wireMockServer, objectMapper);
    }

    private MockerInvocationHandler createHandler() {
        ParameterAnnotationsProcessor parameterAnnotationsProcessor = new ParameterAnnotationsProcessor();
        ParamFormatterInvoker paramFormatterInvoker = new ParamFormatterInvoker();
        ValueMatchingStrategyFactory valueMatchingStrategyFactory = new ValueMatchingStrategyFactory();
        RequestMatchingDescriptorFactory requestMatchingDescriptorFactory = new RequestMatchingDescriptorFactory(
                parameterAnnotationsProcessor,
                paramFormatterInvoker,
                valueMatchingStrategyFactory);
        HttpVerbDetector verbDetector = new HttpVerbDetector();
        ResourceMethodDescriptorFactory resourceMethodDescriptorFactory =
                new ResourceMethodDescriptorFactory(verbDetector);
        CollectionFactory collectionFactory = new CollectionFactory();

        return new MockerInvocationHandler(
                resourceMethodDescriptorFactory,
                requestMatchingDescriptorFactory,
                collectionFactory);
    }

    private <T> ImplementationDefinition<? extends BaseMocker> createImplementationDefinition(
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

    private <T> ImplementationDefinition<? extends BaseMocker> createInterfaceImplementationDefinition(
            Class<T> mockerType,
            ByteBuddy byteBuddy
    ) {
        return byteBuddy
                .subclass(BaseMocker.class)
                .implement(mockerType)
                .method(isDeclaredBy(mockerType));
    }

    private <T> ImplementationDefinition<? extends BaseMocker> createClassImplementationDefinition(
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

    private <T> void checkExtendsBaseMocker(Class<T> mockerType) {
        if (!BaseMocker.class.isAssignableFrom(mockerType)) {
            throw new RuntimeException("For an abstract class to be implemented it must subclass BaseMocker. " +
                    mockerType.getSimpleName() + " does not.");
        }
    }

    private Class<? extends BaseMocker> createClass(
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
