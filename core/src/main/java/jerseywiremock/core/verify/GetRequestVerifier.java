package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;
import jerseywiremock.core.RequestMappingDescriptor;

public class GetRequestVerifier extends BaseRequestVerifyBuilder<GetRequestVerifier> {
    public GetRequestVerifier(WireMockServer wireMockServer, RequestMappingDescriptor mappingDescriptor) {
        super(wireMockServer, new GetRequestedForStrategy(), mappingDescriptor);
    }

    public GetRequestVerifier(WireMockServer wireMockServer, RequestPatternBuilder patternBuilder) {
        super(wireMockServer, patternBuilder);
    }
}
