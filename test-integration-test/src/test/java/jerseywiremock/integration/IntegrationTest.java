package jerseywiremock.integration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.tomakehurst.wiremock.junit.WireMockRule;
import com.github.tomakehurst.wiremock.stubbing.ListStubMappingsResult;
import jerseywiremock.service.core.Foo;
import org.junit.Rule;
import org.junit.Test;

public class IntegrationTest {
    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    public FooMocker fooMocker = new FooMocker(wireMockRule, new ObjectMapper());

    @Test
    public void fooMockerCanBeUsed() throws Exception {
        fooMocker.stubGetFoo(123)
                .andRespondWith(Foo.builder().id(123).name("Test foo").build())
                .stub();

        fooMocker.stubListFoos("Test foo")
                .andRespondWith(
                        Foo.builder().id(123).name("Test foo").build(),
                        Foo.builder().id(345).name("Test foo").build())
                .stub();

        ListStubMappingsResult stubMappings = wireMockRule.listAllStubMappings();
        stubMappings.getMappings();
    }
}
