package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Deprecated
@Generated(value = "openapi-processor-core", version = "test")
public class Bar {

    @Deprecated
    @JsonProperty("foobar")
    private String foobar;

    @Deprecated
    public String getFoobar() {
        return foobar;
    }

    @Deprecated
    public void setFoobar(String foobar) {
        this.foobar = foobar;
    }

}
