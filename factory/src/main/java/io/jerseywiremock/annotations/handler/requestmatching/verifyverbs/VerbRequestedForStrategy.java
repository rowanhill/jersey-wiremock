package io.jerseywiremock.annotations.handler.requestmatching.verifyverbs;

import com.github.tomakehurst.wiremock.matching.RequestPatternBuilder;
import com.github.tomakehurst.wiremock.matching.UrlPathPattern;

public interface VerbRequestedForStrategy {
    RequestPatternBuilder verbRequestedFor(UrlPathPattern urlPathPattern);
}
