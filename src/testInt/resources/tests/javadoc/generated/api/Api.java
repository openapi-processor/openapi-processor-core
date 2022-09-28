package generated.api;

import annotation.Mapping;
import annotation.Parameter;
import generated.model.Foo;
import generated.support.Generated;

@Generated(value = "openapi-processor-core", version = "test")
public interface Api {

    /**
     * a <em>markdown</em> description with <strong>text</strong>
     *
     * <ul>
     *   <li>one list item
     *   <li>second list item
     * </ul>
     *
     * <pre><code>code block
     * </code></pre>
     *
     * more
     *
     * @param fOO this is a <em>parameter</em> description
     * @return this is a <em>response</em> description
     */
    @Mapping("/foo")
    Foo getFoo(@Parameter Foo fOO);

}
