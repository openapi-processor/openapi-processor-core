package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Foo getFoo();

    @Mapping("/foo/{id}")
    Foo getFooId(@Parameter Integer id);

}
