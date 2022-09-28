package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foos {

    @JsonProperty("items")
    private Foo[] items;

    public Foo[] getItems() {
        return items;
    }

    public void setItems(Foo[] items) {
        this.items = items;
    }

}
