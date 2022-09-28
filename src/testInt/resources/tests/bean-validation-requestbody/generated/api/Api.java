package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import java.util.List;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    void postFoo(@Parameter List<@Size(min = 2, max = 2) String> body);

}
