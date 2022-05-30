package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfigurationVisitor;

import java.util.Collections;

public class InteractiveTypePrimaryKeysConfiguration extends InteractiveTypeTypeConfiguration
{
    public java.util.List<InteractiveTypePrimaryKeysPrimaryKeyConfiguration> primaryKeys = Collections.emptyList();

    @Override
    public <T> T accept(InteractiveTypeTypeConfigurationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
