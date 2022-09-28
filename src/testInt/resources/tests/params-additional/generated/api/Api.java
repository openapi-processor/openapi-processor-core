package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import javax.servlet.http.HttpServletRequest;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void getFoo(@Parameter String foo, @Parameter HttpServletRequest request);

}
