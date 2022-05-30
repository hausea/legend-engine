package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.ReadInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = DefaultInteractiveAuthorization.class, name = "defaultInteractiveAuthorization")
})
public abstract class InteractiveAuthorization
{
    public Lambda authorizationFunction;

    public abstract <T> T accept(InteractiveAuthorizationVisitor<T> visitor);
}
