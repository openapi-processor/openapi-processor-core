package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EndpointApi {

    @Mapping("/endpoint")
    void getEndpoint(@Parameter String foo);

    @Mapping("/endpoint-optional")
    void getEndpointOptional(@Parameter String foo);

}
