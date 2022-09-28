package generated.api;

import annotation.Mapping;
import generated.model.FooNestedOneOf;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo-nested-one-of")
    FooNestedOneOf getFooNestedOneOf();

}
