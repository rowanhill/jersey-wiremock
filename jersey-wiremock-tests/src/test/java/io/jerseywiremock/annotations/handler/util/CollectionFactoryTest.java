package io.jerseywiremock.annotations.handler.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Queue;
import java.util.Set;

import org.junit.jupiter.api.Test;

public class CollectionFactoryTest {
    private final CollectionFactory collectionFactory = new CollectionFactory();

    @Test
    public void listIsCreatedForMethodWithListReturnType() {
        // when
        Collection<Object> list = collectionFactory.createCollection(TestCollectionClass.class, "list");

        // then
        assertThat(list).isInstanceOf(List.class);
    }

    @Test
    public void setIsCreatedForMethodWithSettReturnType() {
        // when
        Collection<Object> set = collectionFactory.createCollection(TestCollectionClass.class, "set");

        // then
        assertThat(set).isInstanceOf(Set.class);
    }

    @Test
    public void listIsCreatedForMethodWithCollectionReturnType() {
        // when
        Collection<Object> list = collectionFactory.createCollection(TestCollectionClass.class, "collection");

        // then
        assertThat(list).isInstanceOf(List.class);
    }

    @Test
    public void exceptionIsThrownForMethodWithUnsupportedCollectionReturnType() {
        assertThrows(Exception.class, () -> collectionFactory.createCollection(TestCollectionClass.class, "queue"));
    }

    @Test
    public void exceptionIsThrownForMethodWithUnsupportedNonCollectionReturnType() {
        assertThrows(Exception.class, () -> collectionFactory.createCollection(TestCollectionClass.class, "map"));
    }

    @SuppressWarnings("unused")
    private abstract static class TestCollectionClass {
        abstract List<Object> list();
        abstract Set<Object> set();
        abstract Collection<Object> collection();
        abstract Queue<Object> queue();
        abstract Map<Object, Object> map();
    }
}