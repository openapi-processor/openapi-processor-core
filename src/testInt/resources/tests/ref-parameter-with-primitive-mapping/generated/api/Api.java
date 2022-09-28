package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import java.util.UUID;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/uuid")
    void getUuid(@Parameter UUID uuid, @Parameter UUID uuidex);

}
