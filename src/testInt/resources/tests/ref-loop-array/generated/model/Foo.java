package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("foos")
    private Foos foos;

    public Foos getFoos() {
        return foos;
    }

    public void setFoos(Foos foos) {
        this.foos = foos;
    }

}
