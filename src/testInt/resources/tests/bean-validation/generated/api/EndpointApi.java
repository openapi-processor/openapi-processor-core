package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Obj1;
import generated.support.Generated;
import javax.validation.Valid;
import javax.validation.constraints.DecimalMax;
import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

@Generated(value = "openapi-processor-core", version = "test")
public interface EndpointApi {

    @Mapping("/endpoint/required")
    void getEndpointRequired(
            @Parameter String requiredFalse, @Parameter @NotNull String requiredTrue);

    @Mapping("/endpoint/length")
    void getEndpointLength(
            @Parameter @Size(min = 2) String minLength,
            @Parameter @Size(max = 4) String maxLength,
            @Parameter @Size(min = 2, max = 4) String minMaxLength);

    @Mapping("/endpoint/minmax")
    void getEndpointMinmax(
            @Parameter @DecimalMin(value = "10") Integer min,
            @Parameter @DecimalMin(value = "10", inclusive = false) Integer minEx,
            @Parameter @DecimalMax(value = "20") Integer max,
            @Parameter @DecimalMax(value = "20", inclusive = false) Integer maxEx,
            @Parameter @DecimalMin(value = "10") @DecimalMax(value = "20") Integer minMax,
            @Parameter
                    @DecimalMin(value = "10", inclusive = false)
                    @DecimalMax(value = "20", inclusive = false)
                    Integer minMaxEx);

    @Mapping("/endpoint/items")
    void getEndpointItems(
            @Parameter @Size(min = 2) String[] min,
            @Parameter @Size(max = 4) String[] max,
            @Parameter @Size(min = 2, max = 4) String[] minMax);

    @Mapping("/endpoint/obj")
    void postEndpointObj(@Parameter @Valid Obj1 body);

    @Mapping("/endpoint/pattern")
    void getEndpointPattern(@Parameter @Pattern(regexp = ".*\\.\\\\") String anything);

    @Mapping("/endpoint/email")
    void getEndpointEmail(@Parameter @Email String anything);

}
