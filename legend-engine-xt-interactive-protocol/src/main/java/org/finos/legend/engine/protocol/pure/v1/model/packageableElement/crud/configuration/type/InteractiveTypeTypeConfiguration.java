package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfigurationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InteractiveTypePrimaryKeysConfiguration.class, name = "interactiveTypePrimaryKeysConfiguration")
})
public abstract class InteractiveTypeTypeConfiguration extends InteractiveTypeConfiguration
{
    public abstract <T> T accept(InteractiveTypeTypeConfigurationVisitor<T> visitor);

    @Override
    public <T> T accept(InteractiveTypeConfigurationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
