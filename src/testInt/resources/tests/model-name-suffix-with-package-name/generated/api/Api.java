package generated.api;

import annotation.Mapping;
import generated.model.FooResource;
import generated.support.Generated;
import io.openapiprocessor.Wrap;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foos")
    Wrap<FooResource> getFoos();

}
