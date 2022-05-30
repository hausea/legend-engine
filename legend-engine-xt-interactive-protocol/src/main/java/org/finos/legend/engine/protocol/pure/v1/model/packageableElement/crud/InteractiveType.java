package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud;

import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.graph.GraphFetchTree;

import java.util.Collections;

public class InteractiveType
{
    public String baseClass;
    //TODO: AJH: what to use for RootGraphFetchTree with jackson?
    public GraphFetchTree graphScope;
    public java.util.List<InteractiveTypeConfiguration> configuration = Collections.emptyList();
    public java.util.List<InteractiveService> services = Collections.emptyList();
}
