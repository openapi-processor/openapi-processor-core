package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.model.Foo2;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void patchFoo(@Parameter Foo body);

    @Mapping("/foo2")
    void patchFoo2(@Parameter Foo2 body);

}
