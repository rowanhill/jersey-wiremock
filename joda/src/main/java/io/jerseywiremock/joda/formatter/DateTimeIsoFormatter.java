package io.jerseywiremock.joda.formatter;

import io.jerseywiremock.annotations.formatter.ParamFormatter;
import org.joda.time.DateTime;
import org.joda.time.format.ISODateTimeFormat;

public class DateTimeIsoFormatter implements ParamFormatter<DateTime> {
    @Override
    public String format(DateTime param) {
        return param == null ? "null" : param.toString(ISODateTimeFormat.dateTime());
    }
}
