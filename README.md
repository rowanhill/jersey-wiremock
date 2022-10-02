# jersey-wiremock
Autogenerated stubbing & verifying of JAX-RS resources with WireMock

## What is it?
Jersey is great for developing HTTP APIs, using declarative annotations to define behaviour.

WireMock is great at mocking out HTTP APIs when writing integration tests.

With jersey-wiremock, you can create classes which encapsulate the logic of mocking out your resources (e.g. building
paths, adding query parameters, serialising response entities) without having to write all the boilerplate.

## Show me an example

Let's say you have the following resource:

```java
@Path("/cakes")
@Produces(MediaType.APPLICATION_JSON)
public class CakesResource {
    @GET
    public List<Cake> getAllCakesByFlavour(@QueryParam("flavour") CakeFlavour flavour) {
        // ... retrieve all cakes with given flavour from data store and return result
    }

    @POST
    public long addNewCake(Cake cake) {
        // ... add cake to data store and return assigned id ...
    }
}
```

Using jersey-wiremock, you can define a mocker like this:

```java
@WireMockForResource(CakesResource.class)
public interface CakesMocker {
    @WireMockStub("getAllCakesByFlavour")
    GetListRequestStubber<Cake> stubGetAllCakesByFlavour(CakeFlavour flavour);
    @WireMockVerify("getAllCakesByFlavour")
    GetRequestVerifier verifyGetAllCakesByFlavour(CakeFlavour flavour);

    @WireMockStub("addNewCake")
    PostRequestVerifier<Void> stubAddCake();
}
```

Which you can then use in your tests like this:

```java
public class OtherServiceIntegrationTest {

    @Rule
    public WireMockRule wireMockRule = new WireMockRule(8080);

    private CakesMocker cakesMocker;

    @Before
    public void setUp() {
        Serializers serializers = new Serializers();
        serializers.addSerializer("application/json", new JacksonSerializer());
        cakesMocker = MockerFactory.wireMockerFor(CakesMocker.class, new WireMock(8080), serializers);
    }

    @Test
    public void otherServiceRequestsCakesByFlavourExactlyOnce() {
        // given
        CakeFlavour flavour = CakeFlavour.CHOCOLATE;
        cakesMocker.stubGetAllCakesByFlavour(flavour).andRespondWith(new Cake(1, flavour, "Test recipe")).stub();

        // when
        // ... invoke something on OtherService instance ...

        // then
        // ... maybe make some assertions ...
        cakesMocker.verifyGetAllCakesbyFlavour(flavour).times(1).verify();
    }

    @Test
    public void otherServiceHandlesErrorWhenCreatingCake() {
        // given
        cakesMocker.stubAddCake()
                .withRequestBody(matchingJsonPath("$..[?(@.status=='" + CakeFlavour.LEMON + "')]"))
                .andRespond().withStatusCode(500).stub();

        // when
        // ... invoke something on OtherService instance that creates a lemon cake ...

        // then
        // ... assert that OtherService gracefully handled a 500 ...
    }
}
```

## Tell me more - what can it do?

### Supported operations

You can both stub and verify @GET, @POST, @PUT and @DELETE requests, using functionality automatically generated from
simple interface definitions.

| Request type | Action | Interface method return type | Interface method annotation |
|--------------|--------|------------------------------|-----------------------------|
| `@GET` (singular return type) | Stub | `GetSingleRequestStubber<EntityType>` | `@WireMockStub` |
| `@GET` (collection return type) | Stub | `GetListRequestStubber<CollectionEntityType>` | `@WireMockStub` |
| `@GET` (all types) | Verify | `GetRequestVerifier` | `@WireMockVerify` |
| `@POST` | Stub | `PostRequestStubber<RequestEntity, ResponseEntity>` | `@WireMockStub` |
| `@POST` | Verify | `PostRequestVerifier<RequestEntity>` | `@WireMockVerify` |
| `@PUT` | Stub | `PutRequestStubber<RequestEntity, ResponseEntity>` | `@WireMockStub` |
| `@PUT` | Verify | `GutRequestVerifier<RequestEntity>` | `@WireMockVerify` |
| `@DELETE` | Stub | `DeleteRequestStubber<ResponseEntity>` | `@WireMockStub` |
| `@DELETE` | Verify | `DeleteRequestVerifier` | `@WireMockVerify` |

### Argument handling
#### Argument resolution
Arguments to mocker methods are matched up to the arguments of resource methods in one of two ways: either explicitly,
using `@ParamNamed` on every mocker method argument, or implicitly, with no arguments annotated with `@ParamNamed`.

When resolving arguments explicitly, the mocker method must have an argument for every `@PathParam` declared by the
resource method, but `@QueryParam` arguments are optional. Ordering or arguments can be arbitrary.

When resolving arguments implicitly, the mocker interface methods must have an argument for each `@PathParam`,
`@QueryParam` and `@HeaderParam` declared by the resource method, and they must be declared in the same order.

No other parameters (e.g. `@Context`, entity parameters) should be declared in the mocker method;

#### Argument serialisation
If an argument needs more careful serialisation than simply calling toString() on it, apply the `@ParamFormat`
annotation to specify a `ParamFormatter` class to perform the formatting. You can do this on either the _resource_
method argument (if you want to set a default serialiser that all mocker methods will use), or on the _mocker_ method
argument (if you want to override a deafult, or if you don't want or are unable to modify the resource class).

#### Query parameter matching
By default, query parameters are matched using WireMock's `equalTo()` value matching strategy. To use a different
strategy annotate the _mocker_ method parameter with `@ParamMatchedBy`. Supported strategies are `EQUAL_TO`,
`CONTAINING`, `MATCHING` and `NOT_MATCHING`.

Note that `@ParamMatchedBy` has no effect on path parameters.

### Request body matching
Request stubbers and verifiers for request types that have request entity bodies come with two methods for matching
those request bodies: `withRequestEntity(T entity)` and `withRequestBody(ContentPattern<?> strategy)`. The first is
will take an entity and serialise it with the serializer to provide the string to match against. The second allows
much more fine-grained control over the matching, by allowing specification of any WireMock `ValueMatchingStrategy`,
e.g. `contains()` or `matchesJsonPath()`.

If no request body matching is specified, any request body is acceptable.

### Response body writing
Request stubbers for request types that have singular response bodies can specify a response to reply with - to do so,
use either the `.andRespondWith(myEntity)` or `.andRespond().withEntity(myEntity)` forms. The given object will be
serialised with the Serializer and returned.

For collection type responses, usage is very similar: `.andRespondWith(entity1, entity2, ...)` or
`.andRespond().withEntities(entity1, entity2)`. All entities will be placed in an empty collection of the appropriate
type, then that collection will be serialised.

### Simulating faults
WireMock faults can be applied to responses, e.g. `.andRespond().withFault(Fault.EMPTY_RESPONSE)`.
