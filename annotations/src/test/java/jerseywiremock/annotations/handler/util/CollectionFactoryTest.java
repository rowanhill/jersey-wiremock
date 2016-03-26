package jerseywiremock.annotations.handler.util;

import jerseywiremock.annotations.handler.util.CollectionFactory;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.*;

import static org.assertj.core.api.Assertions.*;

public class CollectionFactoryTest {
    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private CollectionFactory collectionFactory = new CollectionFactory();

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
        // when
        expectedException.expectMessage("Cannot create collection for type Queue");
        collectionFactory.createCollection(TestCollectionClass.class, "queue");
    }

    @Test
    public void exceptionIsThrownForMethodWithUnsupportedNonCollectionReturnType() {
        // when
        expectedException.expectMessage("does not return Collection type; it returns Map");
        collectionFactory.createCollection(TestCollectionClass.class, "map");
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