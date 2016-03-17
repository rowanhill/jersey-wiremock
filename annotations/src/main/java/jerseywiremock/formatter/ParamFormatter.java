package jerseywiremock.formatter;

public interface ParamFormatter<T> {
    String format(T param);
}
