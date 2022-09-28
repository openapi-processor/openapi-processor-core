package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Props;
import generated.support.Generated;
import java.util.Map;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/endpoint-object")
    void getEndpointObject(@Parameter Props props);

    @Mapping("/endpoint-map")
    void getEndpointMap(@Parameter Map<String, String> props);

}
