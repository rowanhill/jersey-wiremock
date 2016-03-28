package jerseywiremock.joda.formatter;

import jerseywiremock.annotations.formatter.ParamFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeIsoNoMillisFormatter implements ParamFormatter<DateTime> {
    @Override
    public String format(DateTime param) {
        return param == null ? "null" : param.toString(ISODateTimeFormat.dateTimeNoMillis());
    }
}
