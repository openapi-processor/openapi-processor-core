package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import org.openapitools.jackson.nullable.JsonNullable;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("bar")
    private JsonNullable<String> bar;

    public JsonNullable<String> getBar() {
        return bar;
    }

    public void setBar(JsonNullable<String> bar) {
        this.bar = bar;
    }

}
