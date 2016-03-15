package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;
import static net.bytebuddy.matcher.ElementMatchers.nameStartsWithIgnoreCase;
import static net.bytebuddy.matcher.ElementMatchers.named;

public class MockerFactory {
    public static <T> T wireMockerFor(Class<T> mockerInterface, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        DynamicType.Builder<BaseMocker> builder = new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"))
                .subclass(BaseMocker.class)
                .implement(mockerInterface);

        MockerInvocationHandler handler = new MockerInvocationHandler(null, null, wireMockServer, objectMapper);

        //TODO handler method based on named resource method's annotations, rather than start of method name
        builder = builder
                .method(isDeclaredBy(mockerInterface).and(nameStartsWithIgnoreCase("stubGet")))
                .intercept(MethodDelegation.to(handler).filter(named("handleStubGet")));

//        builder = builder
//                .method(isDeclaredBy(mockerInterface).and(nameStartsWithIgnoreCase("stubList")))
//                .intercept(MethodDelegation.to(handler).filter(named("handleStubList")));

//        builder = builder
//                .method(isDeclaredBy(mockerInterface).and(nameStartsWithIgnoreCase("verifyGet")))
//                .intercept(MethodDelegation.to(handler).filter(named("handleVerifyGet")));
//
//        builder = builder
//                .method(isDeclaredBy(mockerInterface).and(nameStartsWithIgnoreCase("verifyList")))
//                .intercept(MethodDelegation.to(handler).filter(named("handleVerifyList")));

        Class<? extends BaseMocker> mockerSubclass = builder
                .make()
                .load(MockerFactory.class.getClassLoader(), ClassLoadingStrategy.Default.WRAPPER)
                .getLoaded();

        //noinspection unchecked
        return (T) mockerSubclass
                .getConstructor(WireMockServer.class, ObjectMapper.class)
                .newInstance(wireMockServer, objectMapper);
    }
}
