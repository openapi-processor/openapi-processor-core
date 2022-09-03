package generated.support.Generated;

import java.lang.annotation.*;
import static java.lang.annotation.ElementType.*;
import static java.lang.annotation.RetentionPolicy.*;

@Documented
@Retention(CLASS)
@Target({TYPE, METHOD})
@Generated(value = "openapi-processor-core", version = "test")
public @interface Generated {
    /**
     * The name of the source code generator, i.e. openapi-processor-*.
     *
     * @return name of the generator
     */
    String value();

    /**
     * @return version of the generator
     */
    String version();

    /**
     * The date & time of generation (ISO 8601).
     *
     * @return date of generation
     */
    String date() default null;

    /**
     * The url of the generator.
     *
     * @return url of generator
     */
    String url() default null;

}
