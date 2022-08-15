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

import org.finos.legend.engine.language.pure.compiler.toPureGraph.PureModel;
import org.finos.legend.engine.write.api.AlloyWrite;
import org.finos.legend.pure.generated.Root_meta_json_JSONElement;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.Mapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class;
import org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime;

public class RelationalAlloyWrite implements AlloyWrite
{
    @Override
    public Root_meta_json_JSONElement persist(PureModel pureModel, Mapping mapping, Runtime runtime, Class<?> graphQLQueryClass, Root_meta_json_JSONElement existingInstance, Root_meta_json_JSONElement newInstance)
    {
        return newInstance;
    }
}
