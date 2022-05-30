package org.finos.legend.engine.language.pure.dsl.interactive.compiler.test;

import org.finos.legend.engine.language.pure.compiler.test.TestCompilationFromGrammar;
import org.finos.legend.engine.language.pure.dsl.interactive.grammar.test.TestInteractiveApplicationGrammar;

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
}
