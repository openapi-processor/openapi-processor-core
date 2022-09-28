package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface MixedApi {

    @Mapping("/foo-mixed")
    Foo getFooMixedApplicationJson();

    @Mapping("/foo-mixed")
    String getFooMixedTextPlain();

}
