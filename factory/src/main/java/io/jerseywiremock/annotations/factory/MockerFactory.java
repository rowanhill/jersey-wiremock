package io.jerseywiremock.annotations.factory;

import static net.bytebuddy.matcher.ElementMatchers.isAbstract;

import java.lang.reflect.InvocationTargetException;

import com.github.tomakehurst.wiremock.client.WireMock;

import io.jerseywiremock.annotations.handler.BaseMocker;
import io.jerseywiremock.annotations.handler.MockerInvocationHandler;
import io.jerseywiremock.annotations.handler.requestmatching.RequestMatchingDescriptorFactory;
import io.jerseywiremock.annotations.handler.requestmatching.ValueMatchingStrategyFactory;
import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParamFormatterInvoker;
import io.jerseywiremock.annotations.handler.requestmatching.paramdescriptors.ParameterAnnotationsProcessor;
import io.jerseywiremock.annotations.handler.resourcemethod.HttpVerbDetector;
import io.jerseywiremock.annotations.handler.resourcemethod.ResourceMethodDescriptorFactory;
import io.jerseywiremock.core.stub.request.Serializers;
import io.jerseywiremock.annotations.handler.util.CollectionFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType.Builder.MethodDefinition.ImplementationDefinition;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

public class MockerFactory {
    private final MockerTypeChecker mockerTypeChecker;

    MockerFactory(MockerTypeChecker mockerTypeChecker) {
        this.mockerTypeChecker = mockerTypeChecker;
    }

    public static <T> T wireMockerFor(Class<T> mockerType, WireMock wireMock, Serializers serializers)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        return new MockerFactory(new MockerTypeChecker(new MockerMethodSelector()))
                .createWireMockerFor(mockerType, wireMock, serializers);
    }

    <T> T createWireMockerFor(Class<T> mockerType, WireMock wireMock, Serializers serializers)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        mockerTypeChecker.checkReturnTypes(mockerType);

        MockerInvocationHandler handler = createHandler();
        ByteBuddy byteBuddy = new ByteBuddy().with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"));

        ImplementationDefinition<? extends BaseMocker> implDef = createImplementationDefinition(mockerType, byteBuddy);

        Class<? extends BaseMocker> mockerSubclass = createClass(handler, implDef);

        //noinspection unchecked
        return (T) mockerSubclass
                .getConstructor(WireMock.class, Serializers.class)
                .newInstance(wireMock, serializers);
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
                .method(isAbstract());
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
                .method(isAbstract());
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
