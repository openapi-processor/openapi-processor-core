package generated.api;

import annotation.Mapping;
import generated.model.Book;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/book")
    Book getBook();

}
