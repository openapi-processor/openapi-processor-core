package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public class SpecificPropertiesOne implements GenericProperties {

    @Size(max = 200)
    @JsonProperty("foo")
    private String foo;

    public String getFoo() {
        return foo;
    }

    public void setFoo(String foo) {
        this.foo = foo;
    }

}
