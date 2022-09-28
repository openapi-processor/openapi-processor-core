package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Bar;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EnumApi {

    @Mapping("/endpoint")
    void getEndpoint(@Parameter Foo foo, @Parameter Bar bar);

}
