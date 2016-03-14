package jerseywiremock.service.core;

public class Foo {
    private final int id;
    private final String name;

    public Foo(int id, String name) {
        this.id = id;
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static class Builder {
        public Builder id(int id) {
            return this;
        }

        public Builder name(String name) {
            return this;
        }

        public Foo build() {
            return new Foo(1, null);
        }
    }
}
