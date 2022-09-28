package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Bar {

    @JsonProperty("bar")
    private String bar;

    @JsonProperty("foos")
    private Foo[] foos;

    public String getBar() {
        return bar;
    }

    public void setBar(String bar) {
        this.bar = bar;
    }

    public Foo[] getFoos() {
        return foos;
    }

    public void setFoos(Foo[] foos) {
        this.foos = foos;
    }

}
