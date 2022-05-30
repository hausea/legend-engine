package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization;

public class DefaultInteractiveAuthorization extends InteractiveAuthorization
{
    @Override
    public <T> T accept(InteractiveAuthorizationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
