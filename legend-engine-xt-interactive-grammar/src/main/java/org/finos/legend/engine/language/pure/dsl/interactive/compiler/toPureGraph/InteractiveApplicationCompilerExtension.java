package org.finos.legend.engine.language.pure.dsl.interactive.compiler.toPureGraph;

import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.CompilerExtension;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.Processor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.connection.PackageableConnection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveApplication;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.mapping.Mapping;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.service.Service;
import org.finos.legend.engine.protocol.pure.v1.packageableElement.external.shared.Binding;
import org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_InteractiveApplication;
import org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_InteractiveApplication_Impl;

import java.util.Collections;

public class InteractiveApplicationCompilerExtension implements CompilerExtension
{
    @Override
    public Iterable<? extends Processor<?>> getExtraProcessors()
    {
        return Collections.singletonList(Processor.newProcessor(
                InteractiveApplication.class,
                Lists.fixedSize.with(Service.class, Mapping.class, Binding.class, PackageableConnection.class),
                (interactiveApplication, context) -> new Root_meta_pure_crud_metamodel_InteractiveApplication_Impl("")
                        ._documentation(interactiveApplication.documentation)
                        ._applicationName(interactiveApplication.name)
                        ._applicationPackage(interactiveApplication._package),
                (interactiveApplication, context) ->
                {
                    Root_meta_pure_crud_metamodel_InteractiveApplication pureInteractiveApplication = (Root_meta_pure_crud_metamodel_InteractiveApplication) context.pureModel.getOrCreatePackage(interactiveApplication._package)._children().detect(c -> interactiveApplication.name.equals(c._name()));
                    pureInteractiveApplication._globalAuthorization(HelperInteractiveApplicationBuilder.buildGlobalAuthorization(interactiveApplication.globalAuthorization, context));
                    pureInteractiveApplication._store(HelperInteractiveApplicationBuilder.buildStore(interactiveApplication.store, context));
                    pureInteractiveApplication._types(HelperInteractiveApplicationBuilder.buildTypes(interactiveApplication.types, context));
                }
        ));
    }
}
