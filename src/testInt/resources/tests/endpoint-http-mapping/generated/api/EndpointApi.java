package generated.api;

import annotation.Mapping;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface EndpointApi {

    @Mapping("/endpoint")
    void getEndpoint();

    @Mapping("/endpoint")
    void putEndpoint();

    @Mapping("/endpoint")
    void postEndpoint();

    @Mapping("/endpoint")
    void patchEndpoint();

}
