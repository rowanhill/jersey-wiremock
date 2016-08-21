package io.jerseywiremock.joda.formatter;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class DateTimeIsoNoMillisFormatterTest {
    @Test
    public void dateTimeIsIsoFormatted() {
        // given
        DateTimeIsoNoMillisFormatter formatter = new DateTimeIsoNoMillisFormatter();
        DateTime dateTime = DateTime.now();

        // when
        String formattedString = formatter.format(dateTime);

        // then
        assertThat(formattedString).isEqualTo(dateTime.toString(ISODateTimeFormat.dateTimeNoMillis()));
    }

    @Test
    public void nullIsReturnedAsString() {
        // given
        DateTimeIsoNoMillisFormatter formatter = new DateTimeIsoNoMillisFormatter();

        // when
        String formattedString = formatter.format(null);

        // then
        assertThat(formattedString).isEqualTo("null");
    }
}