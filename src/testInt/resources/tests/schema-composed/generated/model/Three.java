package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Three {

    @JsonProperty("three")
    private String three;

    public String getThree() {
        return three;
    }

    public void setThree(String three) {
        this.three = three;
    }

}
