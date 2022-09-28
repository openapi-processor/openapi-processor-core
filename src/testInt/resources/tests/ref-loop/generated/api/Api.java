package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.model.Self;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/self-reference")
    Self getSelfReference();

    @Mapping("/nested-self-reference")
    Foo getNestedSelfReference();

}
