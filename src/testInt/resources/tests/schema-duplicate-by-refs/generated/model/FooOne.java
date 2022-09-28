package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class FooOne {

    @JsonProperty("foo-one")
    private String fooOne;

    public String getFooOne() {
        return fooOne;
    }

    public void setFooOne(String fooOne) {
        this.fooOne = fooOne;
    }

}
