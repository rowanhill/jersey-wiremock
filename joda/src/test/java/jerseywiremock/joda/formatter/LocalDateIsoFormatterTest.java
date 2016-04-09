package jerseywiremock.joda.formatter;

import org.joda.time.LocalDate;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class LocalDateIsoFormatterTest {
    @Test
    public void dateTimeIsIsoFormatted() {
        // given
        LocalDateIsoFormatter formatter = new LocalDateIsoFormatter();
        LocalDate date = LocalDate.now();

        // when
        String formattedString = formatter.format(date);

        // then
        assertThat(formattedString).isEqualTo(date.toString(ISODateTimeFormat.date()));
    }

    @Test
    public void nullIsReturnedAsString() {
        // given
        LocalDateIsoFormatter formatter = new LocalDateIsoFormatter();

        // when
        String formattedString = formatter.format(null);

        // then
        assertThat(formattedString).isEqualTo("null");
    }
}