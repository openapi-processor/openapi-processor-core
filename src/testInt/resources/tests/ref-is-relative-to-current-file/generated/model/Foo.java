package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("bar")
    private Bar bar;

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

}
