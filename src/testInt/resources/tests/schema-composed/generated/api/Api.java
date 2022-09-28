package generated.api;

import annotation.Mapping;
import generated.model.FooAllOf;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo-all-of")
    FooAllOf getFooAllOf();

    @Mapping("/foo-any-of")
    Object getFooAnyOf();

    @Mapping("/foo-one-of")
    Object getFooOneOf();

}
