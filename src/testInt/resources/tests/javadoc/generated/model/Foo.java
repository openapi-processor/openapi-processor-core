package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

/** this is the <em>Foo</em> schema description */
@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    /** <em>property</em> description */
    @JsonProperty("foo-bar")
    private String fooBar;

    public String getFooBar() {
        return fooBar;
    }

    public void setFooBar(String fooBar) {
        this.fooBar = fooBar;
    }

}
