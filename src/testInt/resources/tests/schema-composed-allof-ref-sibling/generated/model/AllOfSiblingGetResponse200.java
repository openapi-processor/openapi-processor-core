package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class AllOfSiblingGetResponse200 {

    @JsonProperty(value = "foo", access = JsonProperty.Access.READ_ONLY)
    private Foo foo;

    public Foo getFoo() {
        return foo;
    }

    public void setFoo(Foo foo) {
        this.foo = foo;
    }

}
