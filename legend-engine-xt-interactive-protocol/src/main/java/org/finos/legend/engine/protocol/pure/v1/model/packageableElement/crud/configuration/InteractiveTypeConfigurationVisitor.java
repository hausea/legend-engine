package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypePropertyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfiguration;

public interface InteractiveTypeConfigurationVisitor<T>
{
    T visit(InteractiveTypeTypeConfiguration val);

    T visit(InteractiveTypePropertyConfiguration val);
}
