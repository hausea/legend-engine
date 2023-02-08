// Copyright 2023 Goldman Sachs
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

package org.finos.legend.pure.code.write.interpreted;

import org.eclipse.collections.api.block.function.Function2;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.tuple.Pair;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.tuple.Tuples;
import org.finos.legend.pure.code.write.interpreted.natives.Save;
import org.finos.legend.pure.m4.ModelRepository;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.JsonExtensionInterpreted;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.Escape;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.FromJson;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.FromJsonDeprecated;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.JsonStringsEqual;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.ParseJSON;
import org.finos.legend.pure.runtime.java.extension.external.json.interpreted.natives.ToJson;
import org.finos.legend.pure.runtime.java.interpreted.FunctionExecutionInterpreted;
import org.finos.legend.pure.runtime.java.interpreted.extension.BaseInterpretedExtension;
import org.finos.legend.pure.runtime.java.interpreted.extension.InterpretedExtension;
import org.finos.legend.pure.runtime.java.interpreted.natives.core.NativeFunction;

public class WriteExtensionInterpreted extends BaseInterpretedExtension
{
    public WriteExtensionInterpreted()
    {
        super(Lists.mutable.with(
                Tuples.pair("save_T_n__T_n_", Save::new)
        ));
    }

    public static InterpretedExtension extension()
    {
        return new JsonExtensionInterpreted();
    }
}
