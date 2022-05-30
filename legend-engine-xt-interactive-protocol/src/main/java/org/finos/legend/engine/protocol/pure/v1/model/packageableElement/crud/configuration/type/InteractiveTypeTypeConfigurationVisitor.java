package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysConfiguration;

public interface InteractiveTypeTypeConfigurationVisitor<T>
{
    T visit(InteractiveTypePrimaryKeysConfiguration val);
}
