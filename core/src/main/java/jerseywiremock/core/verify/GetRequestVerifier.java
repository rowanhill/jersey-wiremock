package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;

public class GetRequestVerifier extends BaseRequestVerifyBuilder {
    public GetRequestVerifier(WireMockServer wireMockServer, String urlPath) {
        super(wireMockServer, new GetRequestedForStrategy(), urlPath);
    }
}
