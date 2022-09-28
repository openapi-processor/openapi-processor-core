package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;
import java.util.Collection;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public class FooL {

    @JsonProperty("bars")
    private Collection<@Valid Bar> bars;

    public Collection<Bar> getBars() {
        return bars;
    }

    public void setBars(Collection<Bar> bars) {
        this.bars = bars;
    }

}
