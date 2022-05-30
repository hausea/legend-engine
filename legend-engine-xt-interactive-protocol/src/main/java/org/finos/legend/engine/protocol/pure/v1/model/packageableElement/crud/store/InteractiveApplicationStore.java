package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = RelationalInteractiveApplicationStore.class, name = "relationalInteractiveApplicationStore")
})
public abstract class InteractiveApplicationStore
{
    public Lambda generateStore;
    public Lambda generateMapping;
    public Lambda connection;
}
