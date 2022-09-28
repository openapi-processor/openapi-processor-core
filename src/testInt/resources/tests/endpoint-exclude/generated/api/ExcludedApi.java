package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface ExcludedApi {

    @Mapping("/endpoint-exclude/{foo}")
    void getEndpointExcludeFoo(@Parameter String foo);

}
