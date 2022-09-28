package generated.api;

import annotation.Mapping;
import generated.model.FooOne;
import generated.model.FooTwo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo-one")
    FooOne getFoo();

    @Mapping("/foo-two")
    FooTwo getFoo();

}
