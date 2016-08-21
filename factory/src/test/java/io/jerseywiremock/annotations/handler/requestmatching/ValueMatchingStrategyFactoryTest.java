package io.jerseywiremock.annotations.handler.requestmatching;

import com.github.tomakehurst.wiremock.client.ValueMatchingStrategy;
import org.junit.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static io.jerseywiremock.annotations.ParamMatchingStrategy.*;
import static org.assertj.core.api.Assertions.assertThat;

public class ValueMatchingStrategyFactoryTest {
    private ValueMatchingStrategyFactory factory = new ValueMatchingStrategyFactory();

    @Test
    public void strategyCanBeCreatedForEqualTo() {
        // when
        ValueMatchingStrategy valueMatchingStrategy = factory.createValueMatchingStrategy(EQUAL_TO, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(equalTo("val"));
    }

    @Test
    public void strategyCanBeCreatedForMatching() {
        // when
        ValueMatchingStrategy valueMatchingStrategy = factory.createValueMatchingStrategy(MATCHING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(matching("val"));
    }

    @Test
    public void strategyCanBeCreatedForContaining() {
        // when
        ValueMatchingStrategy valueMatchingStrategy = factory.createValueMatchingStrategy(CONTAINING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(containing("val"));
    }

    @Test
    public void strategyCanBeCreatedForNotMatching() {
        // when
        ValueMatchingStrategy valueMatchingStrategy = factory.createValueMatchingStrategy(NOT_MATCHING, "val");

        // then
        assertThat(valueMatchingStrategy).isEqualToComparingFieldByField(notMatching("val"));
    }
}