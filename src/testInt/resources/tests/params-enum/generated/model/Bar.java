package generated.model;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public enum Bar {
    BAR("bar"),
    BAR_2("bar-2"),
    BAR_BAR("bar-bar");

    private final String value;

    Bar(String value) {
        this.value = value;
    }

    @JsonValue
    public String getValue() {
        return this.value;
    }

    @JsonCreator
    public static Bar fromValue(String value) {
        for (Bar val : Bar.values()) {
            if (val.value.equals(value)) {
                return val;
            }
        }
        throw new IllegalArgumentException(value);
    }

}
