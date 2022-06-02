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
