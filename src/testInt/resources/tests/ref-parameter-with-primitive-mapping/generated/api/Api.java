/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import java.util.UUID;

public interface Api {

    @Mapping("/uuid")
    void getUuid(@Parameter UUID uuid, @Parameter UUID uuidex);

}
