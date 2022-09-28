package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.model.FooL;
import generated.support.Generated;
import javax.validation.Valid;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void postFoo(@Parameter @Valid Foo body);

    @Mapping("/fooL")
    void postFooL(@Parameter @Valid FooL body);

}
