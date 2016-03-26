package jerseywiremock.annotations.handler;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import com.github.tomakehurst.wiremock.matching.ValuePattern;
import jerseywiremock.annotations.handler.requestmapping.paramdescriptors.QueryParamMatchDescriptor;
import jerseywiremock.annotations.handler.requestmapping.queryparam.StubOrVerifyQueryParamAdder;
import jerseywiremock.annotations.handler.requestmapping.queryparam.WireMockQueryParamBuilderWrapper;
import org.hamcrest.CustomTypeSafeMatcher;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.ArrayList;
import java.util.List;

import static com.github.tomakehurst.wiremock.matching.ValuePattern.containing;
import static com.github.tomakehurst.wiremock.matching.ValuePattern.equalTo;
import static com.github.tomakehurst.wiremock.matching.ValuePattern.matches;
import static jerseywiremock.annotations.handler.requestmapping.paramdescriptors.ParamMatchingStrategy.*;
import static jerseywiremock.annotations.handler.StubOrVerifyQueryParamAdderTest.ValueMatchingStrategyMatcher.hasValuePattern;
import static org.hamcrest.CoreMatchers.is;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.verify;

@RunWith(MockitoJUnitRunner.class)
public class StubOrVerifyQueryParamAdderTest {
    @Mock
    private WireMockQueryParamBuilderWrapper mockBuilderWrapper;
    @InjectMocks
    private StubOrVerifyQueryParamAdder paramAdder;

    private List<QueryParamMatchDescriptor> queryParamMatchDescriptors = new ArrayList<>();

    @Test
    public void equalToMatchingStrategyIsAddedToBuilder() {
        // given
        queryParamMatchDescriptors.add(new QueryParamMatchDescriptor("name", "val", EQUAL_TO));

        // when
        paramAdder.addQueryParameters(queryParamMatchDescriptors);

        // then
        verify(mockBuilderWrapper).withQueryParam(argThat(is("name")), argThat(hasValuePattern(equalTo("val"))));
    }

    @Test
    public void containingMatchingStrategyIsAddedToBuilder() {
        // given
        queryParamMatchDescriptors.add(new QueryParamMatchDescriptor("name", "val", CONTAINING));

        // when
        paramAdder.addQueryParameters(queryParamMatchDescriptors);

        // then
        verify(mockBuilderWrapper).withQueryParam(argThat(is("name")), argThat(hasValuePattern(containing("val"))));
    }

    @Test
    public void matchingMatchingStrategyIsAddedToBuilder() {
        // given
        queryParamMatchDescriptors.add(new QueryParamMatchDescriptor("name", "val", MATCHING));

        // when
        paramAdder.addQueryParameters(queryParamMatchDescriptors);

        // then
        verify(mockBuilderWrapper).withQueryParam(argThat(is("name")), argThat(hasValuePattern(matches("val"))));
    }

    @Test
    public void notMatchingMatchingStrategyIsAddedToBuilder() {
        // given
        queryParamMatchDescriptors.add(new QueryParamMatchDescriptor("name", "val", NOT_MATCHING));
        ValuePattern valuePattern = new ValuePattern();
        valuePattern.setDoesNotMatch("val");

        // when
        paramAdder.addQueryParameters(queryParamMatchDescriptors);

        // then
        verify(mockBuilderWrapper).withQueryParam(argThat(is("name")), argThat(hasValuePattern(valuePattern)));
    }

    static class ValueMatchingStrategyMatcher extends CustomTypeSafeMatcher<ValueMatchingStrategy> {
        private final ValuePattern valuePattern;

        public ValueMatchingStrategyMatcher(ValuePattern valuePattern) {
            super("has same value pattern as " + valuePattern);
            this.valuePattern = valuePattern;
        }

        public static ValueMatchingStrategyMatcher hasValuePattern(ValuePattern valuePattern) {
            return new ValueMatchingStrategyMatcher(valuePattern);
        }

        @Override
        protected boolean matchesSafely(ValueMatchingStrategy other) {
            return valuePattern.equals(other.asValuePattern());
        }
    }
}