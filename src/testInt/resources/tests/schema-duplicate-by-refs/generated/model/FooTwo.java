package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class FooTwo {

    @JsonProperty("foo-two")
    private String fooTwo;

    public String getFooTwo() {
        return fooTwo;
    }

    public void setFooTwo(String fooTwo) {
        this.fooTwo = fooTwo;
    }

}
