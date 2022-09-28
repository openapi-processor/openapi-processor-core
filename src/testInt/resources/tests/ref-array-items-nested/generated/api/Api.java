package generated.api;

import annotation.Mapping;
import generated.model.Bar;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/array")
    Bar[] getArray();

}
