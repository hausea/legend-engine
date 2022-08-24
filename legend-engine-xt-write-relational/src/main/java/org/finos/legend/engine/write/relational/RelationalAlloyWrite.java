// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.write.relational;

import org.eclipse.collections.api.list.MutableList;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.PureModel;
import org.finos.legend.engine.plan.execution.PlanExecutor;
import org.finos.legend.engine.plan.execution.stores.relational.plugin.RelationalStoreExecutor;
import org.finos.legend.engine.write.api.AlloyWrite;
import org.finos.legend.pure.generated.Root_meta_json_JSONElement;
import org.finos.legend.pure.generated.Root_meta_json_JSONKeyValue_Impl;
import org.finos.legend.pure.generated.Root_meta_json_JSONNumber_Impl;
import org.finos.legend.pure.generated.Root_meta_json_JSONObject;
import org.finos.legend.pure.generated.Root_meta_json_JSONString_Impl;
import org.finos.legend.pure.generated.Root_meta_pure_alloy_connections_RelationalDatabaseConnection;
import org.finos.legend.pure.generated.Root_meta_pure_executionPlan_ExecutionPlan;
import org.finos.legend.pure.generated.Root_meta_pure_runtime_ExecutionContext_Impl;
import org.finos.legend.pure.generated.core_pure_executionPlan_executionPlan_generation;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.Mapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.QualifiedProperty;
import org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.support.map.PureMap;
import org.pac4j.core.profile.CommonProfile;

import java.util.concurrent.atomic.AtomicInteger;

import static org.finos.legend.pure.generated.core_relational_relational_extensions_extension.Root_meta_relational_extension_relationalExtensions__Extension_MANY_;

public class RelationalAlloyWrite implements AlloyWrite
{
    private static final AtomicInteger COUNTER = new AtomicInteger(1);
    private final PlanExecutor planExecutor;

    public RelationalAlloyWrite(PlanExecutor planExecutor)
    {
        this.planExecutor = planExecutor;
    }

    @Override
    public Root_meta_json_JSONElement persist(PureModel pureModel, Mapping mapping, Runtime runtime, MutableList<CommonProfile> profiles, QualifiedProperty graphQLQueryProperty, PureMap propertyArguments, Root_meta_json_JSONElement newInstance)
    {
        //TODO: AJH: begin transaction
        Root_meta_pure_executionPlan_ExecutionPlan readExecutionPlan = core_pure_executionPlan_executionPlan_generation.Root_meta_pure_executionPlan_executionPlan_FunctionDefinition_1__Mapping_1__Runtime_1__ExecutionContext_1__Extension_MANY__ExecutionPlan_1_(graphQLQueryProperty, mapping, runtime, new Root_meta_pure_runtime_ExecutionContext_Impl(""), Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), pureModel.getExecutionSupport());

//        propertyArguments.getMap().toMap(e -> )

//        Result result = this.planExecutor.execute(readExecutionPlan, qualifiedPropertyParameters);

//        Root_meta_pure_alloy_connections_RelationalDatabaseConnection relationalConnection = (Root_meta_pure_alloy_connections_RelationalDatabaseConnection) runtime._connections().getOnly();
//        RelationalStoreExecutor relationalStoreExecutor = (RelationalStoreExecutor) this.planExecutor.getExtraExecutors().detect(storeExecutor -> storeExecutor instanceof RelationalStoreExecutor);
//        relationalStoreExecutor.getStoreState().getRelationalExecutor().getConnectionManager().getDatabaseConnection(profiles, relationalConnection);

        return ((Root_meta_json_JSONObject) newInstance)._keyValuePairsAdd(new Root_meta_json_JSONKeyValue_Impl("")._key(new Root_meta_json_JSONString_Impl("")._value("id"))._value(new Root_meta_json_JSONNumber_Impl("")._value(COUNTER.getAndIncrement())));
    }
}
