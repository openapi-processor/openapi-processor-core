package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Two {

    @JsonProperty("two")
    private String two;

    public String getTwo() {
        return two;
    }

    public void setTwo(String two) {
        this.two = two;
    }

}
