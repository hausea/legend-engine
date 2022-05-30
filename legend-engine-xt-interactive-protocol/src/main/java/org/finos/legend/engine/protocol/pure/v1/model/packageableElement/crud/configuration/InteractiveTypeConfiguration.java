package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration;

import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypePropertyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfiguration;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "_type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = InteractiveTypeTypeConfiguration.class, name = "interactiveTypeTypeConfiguration"),
        @JsonSubTypes.Type(value = InteractiveTypePropertyConfiguration.class, name = "interactiveTypePropertyConfiguration")
})
public abstract class InteractiveTypeConfiguration
{
    public abstract <T> T accept(InteractiveTypeConfigurationVisitor<T> visitor);
}
