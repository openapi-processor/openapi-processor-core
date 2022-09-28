package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @Valid
    @JsonProperty("myProperties")
    private GenericProperties myProperties;

    public GenericProperties getMyProperties() {
        return myProperties;
    }

    public void setMyProperties(GenericProperties myProperties) {
        this.myProperties = myProperties;
    }

}
