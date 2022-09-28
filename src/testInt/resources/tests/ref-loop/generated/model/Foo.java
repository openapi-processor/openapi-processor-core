package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("child")
    private Bar child;

    public Bar getChild() {
        return child;
    }

    public void setChild(Bar child) {
        this.child = child;
    }

}
