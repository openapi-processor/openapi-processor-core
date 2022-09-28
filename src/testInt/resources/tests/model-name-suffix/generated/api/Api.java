package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.BodyResource;
import generated.model.FooResource;
import generated.model.ParamResource;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    FooResource getFoo(@Parameter ParamResource param, @Parameter BodyResource body);

}
