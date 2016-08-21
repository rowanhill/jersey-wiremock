package io.jerseywiremock.annotations.formatter;

public interface ParamFormatter<T> {
    String format(T param);
}
