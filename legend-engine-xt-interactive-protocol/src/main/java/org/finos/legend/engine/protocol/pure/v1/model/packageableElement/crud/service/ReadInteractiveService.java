package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service;

import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;

public class ReadInteractiveService extends InteractiveService
{
    public Lambda query;
    
    @Override
    public <T> T accept(InteractiveServiceVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
