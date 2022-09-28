package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Bar;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Deprecated
    @Mapping("/foo")
    Bar getFoo(@Deprecated @Parameter Bar bar);

}
