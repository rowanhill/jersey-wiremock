package jerseywiremock.joda.formatter;

import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;
import org.junit.Test;

import static org.assertj.core.api.Assertions.*;

public class DateTimeIsoFormatterTest {
    @Test
    public void dateTimeIsIsoFormatted() {
        // given
        DateTimeIsoFormatter formatter = new DateTimeIsoFormatter();
        DateTime dateTime = DateTime.now();

        // when
        String formattedString = formatter.format(dateTime);

        // then
        assertThat(formattedString).isEqualTo(dateTime.toString(ISODateTimeFormat.dateTime()));
    }

    @Test
    public void nullIsReturnedAsString() {
        // given
        DateTimeIsoFormatter formatter = new DateTimeIsoFormatter();

        // when
        String formattedString = formatter.format(null);

        // then
        assertThat(formattedString).isEqualTo("null");
    }
}