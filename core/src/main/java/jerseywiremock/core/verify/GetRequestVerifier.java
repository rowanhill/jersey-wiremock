package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import jerseywiremock.core.RequestMappingDescriptor;

public class GetRequestVerifier extends BaseRequestVerifyBuilder {
    public GetRequestVerifier(WireMockServer wireMockServer, RequestMappingDescriptor mappingDescriptor) {
        super(wireMockServer, new GetRequestedForStrategy(), mappingDescriptor);
    }
}
