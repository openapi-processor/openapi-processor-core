package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class FooResource {

    @JsonProperty("prop")
    private String prop;

    @JsonProperty("nested")
    private BarResource nested;

    @JsonProperty("inline")
    private FooInlineResource inline;

    public String getProp() {
        return prop;
    }

    public void setProp(String prop) {
        this.prop = prop;
    }

    public BarResource getNested() {
        return nested;
    }

    public void setNested(BarResource nested) {
        this.nested = nested;
    }

    public FooInlineResource getInline() {
        return inline;
    }

    public void setInline(FooInlineResource inline) {
        this.inline = inline;
    }

}
