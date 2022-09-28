package generated.api;

import annotation.Mapping;
import generated.model.QueryGetResponse200;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/query")
    QueryGetResponse200 getQuery();

}
