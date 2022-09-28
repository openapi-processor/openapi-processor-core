package generated.api;

import annotation.Mapping;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void getFoo();

    @Mapping("/bar")
    String getBar();

    @Mapping("/bar-multi")
    String getBarMultiTextPlain();

    @Mapping("/bar-multi")
    String getBarMultiApplicationJson();

}
