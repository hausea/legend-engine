package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.InteractiveAuthorization;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ReadInteractiveService.class, name = "readInteractiveService"),
        @JsonSubTypes.Type(value = CreateInteractiveService.class, name = "createInteractiveService")
})
public abstract class InteractiveService
{
    public String name;
    public InteractiveAuthorization authorization;

    public abstract <T> T accept(InteractiveServiceVisitor<T> visitor);
}
