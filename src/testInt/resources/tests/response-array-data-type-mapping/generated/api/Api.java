package generated.api;

import annotation.Mapping;
import generated.support.Generated;
import java.util.Collection;
import java.util.List;
import java.util.Set;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/array-global")
    Collection<String> getArrayGlobal();

    @Mapping("/array-global-response")
    List<String> getArrayGlobalResponse();

    @Mapping("/array-endpoint-response")
    Set<String> getArrayEndpointResponse();

}
