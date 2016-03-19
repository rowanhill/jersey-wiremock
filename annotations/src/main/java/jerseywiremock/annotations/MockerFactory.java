package jerseywiremock.annotations;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.annotations.handler.BaseMocker;
import jerseywiremock.annotations.handler.MockerInvocationHandler;
import jerseywiremock.core.RequestMappingDescriptorFactory;
import net.bytebuddy.ByteBuddy;
import net.bytebuddy.NamingStrategy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;

import java.lang.reflect.InvocationTargetException;

import static net.bytebuddy.matcher.ElementMatchers.isDeclaredBy;

public class MockerFactory {
    public static <T> T wireMockerFor(Class<T> mockerInterface, WireMockServer wireMockServer, ObjectMapper objectMapper)
            throws NoSuchMethodException, IllegalAccessException, InvocationTargetException, InstantiationException
    {
        MockerInvocationHandler handler = new MockerInvocationHandler(new ParamMapFactory(), new RequestMappingDescriptorFactory());

        Class<? extends BaseMocker> mockerSubclass = new ByteBuddy()
                .with(new NamingStrategy.SuffixingRandom("JerseyWireMockGenerated"))
                .subclass(BaseMocker.class)
                .implement(mockerInterface)
                .method(isDeclaredBy(mockerInterface))
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
