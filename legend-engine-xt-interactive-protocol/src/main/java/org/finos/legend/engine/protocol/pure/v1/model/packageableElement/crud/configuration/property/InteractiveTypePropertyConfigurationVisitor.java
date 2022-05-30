package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property;

public interface InteractiveTypePropertyConfigurationVisitor<T>
{
    T visit(InteractiveTypeStringPropertyConfiguration val);
}
