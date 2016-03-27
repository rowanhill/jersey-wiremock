package jerseywiremock.core.verify;

import com.github.tomakehurst.wiremock.WireMockServer;
import com.github.tomakehurst.wiremock.client.RequestPatternBuilder;

public class DeleteRequestVerifier extends EmptyRequestVerifier {
    public DeleteRequestVerifier(
            WireMockServer wireMockServer,
            RequestPatternBuilder patternBuilder
    ) {
        super(wireMockServer, patternBuilder);
    }
}
