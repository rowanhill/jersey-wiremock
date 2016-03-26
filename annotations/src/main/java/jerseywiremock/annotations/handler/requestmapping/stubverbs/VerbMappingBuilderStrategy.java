package jerseywiremock.annotations.handler.requestmapping.stubverbs;

import com.github.tomakehurst.wiremock.client.MappingBuilder;
import com.github.tomakehurst.wiremock.client.UrlMatchingStrategy;

public interface VerbMappingBuilderStrategy {
    MappingBuilder verb(UrlMatchingStrategy urlMatchingStrategy);
}
