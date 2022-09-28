package generated.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public class Foo {

    @JsonProperty("bar")
    private Bar bar;

    @JsonProperty(value = "barRO", access = JsonProperty.Access.READ_ONLY)
    private Bar barRo;

    @JsonProperty(value = "barWO", access = JsonProperty.Access.WRITE_ONLY)
    private Bar barWo;

    @JsonProperty(value = "barNameRO", access = JsonProperty.Access.READ_ONLY)
    private String barNameRo;

    @JsonProperty(value = "barNameWO", access = JsonProperty.Access.WRITE_ONLY)
    private String barNameWo;

    public Bar getBar() {
        return bar;
    }

    public void setBar(Bar bar) {
        this.bar = bar;
    }

    public Bar getBarRo() {
        return barRo;
    }

    public void setBarRo(Bar barRo) {
        this.barRo = barRo;
    }

    public Bar getBarWo() {
        return barWo;
    }

    public void setBarWo(Bar barWo) {
        this.barWo = barWo;
    }

    public String getBarNameRo() {
        return barNameRo;
    }

    public void setBarNameRo(String barNameRo) {
        this.barNameRo = barNameRo;
    }

    public String getBarNameWo() {
        return barNameWo;
    }

    public void setBarNameWo(String barNameWo) {
        this.barNameWo = barNameWo;
    }

}
