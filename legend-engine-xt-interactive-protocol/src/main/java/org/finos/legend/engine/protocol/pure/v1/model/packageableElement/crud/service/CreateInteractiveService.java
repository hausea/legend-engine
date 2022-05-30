package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service;

public class CreateInteractiveService extends InteractiveService
{
    //TODO: AJH: what to use for RootGraphFetchTree with jackson?
    public String writeScope;

    @Override
    public <T> T accept(InteractiveServiceVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
