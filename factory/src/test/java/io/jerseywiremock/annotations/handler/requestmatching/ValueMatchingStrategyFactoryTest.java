package io.jerseywiremock.annotations.handler.requestmatching;

import static com.github.tomakehurst.wiremock.client.WireMock.containing;
import static com.github.tomakehurst.wiremock.client.WireMock.equalTo;
import static com.github.tomakehurst.wiremock.client.WireMock.matching;
import static com.github.tomakehurst.wiremock.client.WireMock.notMatching;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.CONTAINING;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.EQUAL_TO;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.MATCHING;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.NOT_MATCHING;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;

import com.github.tomakehurst.wiremock.matching.StringValuePattern;

public class ValueMatchingStrategyFactoryTest {
    private ValueMatchingStrategyFactory factory = new ValueMatchingStrategyFactory();

    @Test
    public void strategyCanBeCreatedForEqualTo() {
        // when
        StringValuePattern valueMatchingStrategy = factory.createValueMatchingStrategy(EQUAL_TO, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(equalTo("val"));
    }

    @Test
    public void strategyCanBeCreatedForMatching() {
        // when
        StringValuePattern valueMatchingStrategy = factory.createValueMatchingStrategy(MATCHING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualTo(matching("val"));
    }

    @Test
    public void strategyCanBeCreatedForContaining() {
        // when
        StringValuePattern valueMatchingStrategy = factory.createValueMatchingStrategy(CONTAINING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(containing("val"));
    }

    @Test
    public void strategyCanBeCreatedForNotMatching() {
        // when
        StringValuePattern valueMatchingStrategy = factory.createValueMatchingStrategy(NOT_MATCHING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualTo(notMatching("val"));
    }
}