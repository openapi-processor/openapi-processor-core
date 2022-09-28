package generated.api;

import annotation.Mapping;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/string")
    String getString();

    @Mapping("/integer")
    Integer getInteger();

    @Mapping("/long")
    Long getLong();

    @Mapping("/float")
    Float getFloat();

    @Mapping("/double")
    Double getDouble();

    @Mapping("/boolean")
    Boolean getBoolean();

    @Mapping("/array-string")
    String[] getArrayString();

}
