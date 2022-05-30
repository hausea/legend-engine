package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property;

public class InteractiveTypeStringPropertyConfiguration extends InteractiveTypePropertyConfiguration
{
    public Integer minLength;
    public Integer maxLength;

    @Override
    public <T> T accept(InteractiveTypePropertyConfigurationVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}
