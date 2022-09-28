package generated.api;

import annotation.Mapping;
import generated.support.Generated;
import http.ResponseWrapper;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    String getFoo();

    @Mapping("/bar")
    ResponseWrapper<String> getBar();

}
