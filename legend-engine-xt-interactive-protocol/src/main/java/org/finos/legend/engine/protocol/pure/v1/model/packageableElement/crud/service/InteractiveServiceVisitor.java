package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service;

public interface InteractiveServiceVisitor<T>
{
    T visit(CreateInteractiveService val);

    T visit(ReadInteractiveService val);
}
