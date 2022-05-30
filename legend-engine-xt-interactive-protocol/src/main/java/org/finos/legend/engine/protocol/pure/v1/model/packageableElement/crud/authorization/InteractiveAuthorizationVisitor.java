package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization;

public interface InteractiveAuthorizationVisitor<T>
{
    T visit(DefaultInteractiveAuthorization val);
}