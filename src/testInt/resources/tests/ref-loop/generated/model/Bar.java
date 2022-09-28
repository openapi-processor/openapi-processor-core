package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Bar {

    @JsonProperty("parent")
    private Foo parent;

    public Foo getParent() {
        return parent;
    }

    public void setParent(Foo parent) {
        this.parent = parent;
    }

}
