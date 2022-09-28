package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @Valid
    @JsonProperty("bars")
    private Bar[] bars;

    public Bar[] getBars() {
        return bars;
    }

    public void setBars(Bar[] bars) {
        this.bars = bars;
    }

}
