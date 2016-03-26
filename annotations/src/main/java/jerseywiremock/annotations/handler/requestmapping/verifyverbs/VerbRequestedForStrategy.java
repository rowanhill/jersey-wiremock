package jerseywiremock.annotations.handler.requestmapping.verifyverbs;

import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

public interface VerbRequestedForStrategy {
    RequestPatternBuilder verbRequestedFor(UrlMatchingStrategy urlMatchingStrategy);
}
