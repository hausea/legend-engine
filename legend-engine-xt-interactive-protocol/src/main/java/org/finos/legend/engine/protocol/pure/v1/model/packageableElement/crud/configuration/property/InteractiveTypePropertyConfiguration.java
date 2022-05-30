package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfigurationVisitor;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InteractiveTypeStringPropertyConfiguration.class, name = "interactiveTypeStringPropertyConfiguration")
})
public abstract class InteractiveTypePropertyConfiguration extends InteractiveTypeConfiguration
{
    public String property;

    public abstract <T> T accept(InteractiveTypePropertyConfigurationVisitor<T> visitor);

    @Override
    public <T> T accept(InteractiveTypeConfigurationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
