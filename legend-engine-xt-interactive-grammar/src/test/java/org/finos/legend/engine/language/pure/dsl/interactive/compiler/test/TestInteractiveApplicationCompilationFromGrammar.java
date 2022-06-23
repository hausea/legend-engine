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

package org.finos.legend.engine.language.pure.dsl.interactive.compiler.test;

import org.eclipse.collections.api.tuple.Pair;
import org.finos.legend.engine.language.pure.compiler.test.TestCompilationFromGrammar;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.PureModel;
import org.finos.legend.engine.language.pure.dsl.interactive.grammar.test.TestInteractiveApplicationGrammar;
import org.finos.legend.engine.protocol.pure.v1.model.context.PureModelContextData;
import org.junit.Test;

public class TestInteractiveApplicationCompilationFromGrammar extends TestCompilationFromGrammar.TestCompilationFromGrammarTestSuite implements TestInteractiveApplicationGrammar
{
    @Override
    protected String getDuplicatedElementTestCode()
    {
        return "Class meta::demo::crud::DemoApp {}\n" +
                "###InteractiveApplication\n" +
                this.defaultApplication();
    }

    @Override
    protected String getDuplicatedElementTestExpectedErrorMessage()
    {
        return "COMPILATION error at [3:1-27:1]: Duplicated element 'meta::demo::crud::DemoApp'";
    }

    @Test
    public void pleaseCompile()
    {
        Pair<PureModelContextData, PureModel> result = test(
                "Class meta::demo::crud::Firm\n" +
                        "{\n" +
                        "  name: String[1];\n" +
                        "  id: Integer[1];\n" +
                        "}\n" +
                        "###InteractiveApplication\n" +
                        "import meta::demo::crud::*;\n" +
                        this.defaultApplication());
        PureModel model = result.getTwo();
    }
}
