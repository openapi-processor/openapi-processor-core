package generated.api;

import annotation.Mapping;
import generated.model.Book;
import generated.model.BookInlineGetResponse200;
import generated.model.BookNested;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    @Mapping("/book-inline")
    BookInlineGetResponse200 getBookInline();

    @Mapping("/book")
    Book getBook();

    @Mapping("/book-nested")
    BookNested getBookNested();

}
