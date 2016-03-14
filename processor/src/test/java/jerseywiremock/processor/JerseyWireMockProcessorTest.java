package jerseywiremock.processor;

import com.google.testing.compile.JavaFileObjects;
import org.junit.Test;

import javax.tools.JavaFileManager;

import java.net.URL;

import static com.google.common.truth.Truth.assert_;
import static com.google.testing.compile.JavaSourceSubjectFactory.javaSource;

public class JerseyWireMockProcessorTest {
    @Test
    public void qq() throws Exception {
        assert_().about(javaSource())
                .that(JavaFileObjects.forResource("input/ExampleIntegrationTest.java"))
                .processedWith(new JerseyWireMockProcessor())
                .compilesWithoutError()
                .and().generatesSources(JavaFileObjects.forResource("expectedoutput/FooMocker.java"));
    }
}