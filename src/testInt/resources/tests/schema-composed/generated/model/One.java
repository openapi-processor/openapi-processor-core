package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class One {

    @JsonProperty("one")
    private String one;

    public String getOne() {
        return one;
    }

    public void setOne(String one) {
        this.one = one;
    }

}
