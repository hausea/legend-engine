/*
 * Copyright 2022 Goldman Sachs
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.finos.legend.engine.language.pure.dsl.interactive.compiler.toPureGraph;

import org.eclipse.collections.api.block.function.Function;
import org.eclipse.collections.api.factory.Lists;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.CompilerExtension;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.extension.Processor;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.handlers.FunctionHandlerDispatchBuilderInfo;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.handlers.Handlers;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveApplication;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.mapping.Mapping;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.runtime.PackageableRuntime;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.Store;
import org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_InteractiveApplication;
import org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_InteractiveApplication_Impl;

import java.util.Collections;
import java.util.List;

public class InteractiveApplicationCompilerExtension implements CompilerExtension
{
    @Override
    public Iterable<? extends Processor<?>> getExtraProcessors()
    {
        return Collections.singletonList(Processor.newProcessor(
                InteractiveApplication.class,
                Lists.fixedSize.with(
                        Store.class,
                        Mapping.class,
                        PackageableRuntime.class,
                        org.finos.legend.engine.protocol.pure.v1.model.packageableElement.domain.Function.class),
                (interactiveApplication, context) -> new Root_meta_pure_crud_metamodel_InteractiveApplication_Impl("")
                        ._documentation(interactiveApplication.documentation)
                        ._applicationName(interactiveApplication.name)
                        ._applicationPackage(interactiveApplication._package),
                (interactiveApplication, context) ->
                {
                    Root_meta_pure_crud_metamodel_InteractiveApplication pureInteractiveApplication = (Root_meta_pure_crud_metamodel_InteractiveApplication) context.pureModel.getOrCreatePackage(interactiveApplication._package)._children().detect(c -> interactiveApplication.name.equals(c._name()));
                    pureInteractiveApplication._globalAuthorization(HelperInteractiveApplicationBuilder.buildGlobalAuthorization(interactiveApplication.globalAuthorization, context));
                    pureInteractiveApplication._store(HelperInteractiveApplicationBuilder.buildStore(interactiveApplication.store, interactiveApplication.types, context));
                    pureInteractiveApplication._types(HelperInteractiveApplicationBuilder.buildTypes(interactiveApplication.types, context));
                }
        ));
    }

    @Override
    public List<Function<Handlers, List<FunctionHandlerDispatchBuilderInfo>>> getExtraFunctionHandlerDispatchBuilderInfoCollectors()
    {
        return Collections.singletonList((handlers) ->
                                                 Lists.mutable.with());
    }
}
