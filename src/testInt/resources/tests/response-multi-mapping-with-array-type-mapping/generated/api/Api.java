package generated.api;

import annotation.Mapping;
import generated.model.Foo;
import generated.support.Generated;
import reactor.core.publisher.Flux;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/foo")
    Flux<Foo> getFoo();

}
