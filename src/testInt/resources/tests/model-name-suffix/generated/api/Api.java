/*
 * This class is auto generated by https://github.com/hauner/openapi-processor-core.
 * TEST ONLY.
 */

package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.BodyResource;
import generated.model.FooResource;
import generated.model.ParamResource;

public interface Api {

    @Mapping("/foo")
    FooResource getFoo(@Parameter ParamResource param, @Parameter BodyResource body);

}