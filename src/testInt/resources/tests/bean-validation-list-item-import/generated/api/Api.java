package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import java.util.List;
import javax.validation.constraints.Pattern;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/test")
    void getTest(@Parameter List<@Pattern(regexp = ".*") String> patternParam);

}
