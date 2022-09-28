package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.support.Generated;
import http.Multipart;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/multipart/single-file")
    void postMultipartSingleFile(@Parameter Multipart file, @Parameter String other);

    @Mapping("/multipart/multiple-files")
    void postMultipartMultipleFiles(@Parameter Multipart[] files, @Parameter String other);

}
