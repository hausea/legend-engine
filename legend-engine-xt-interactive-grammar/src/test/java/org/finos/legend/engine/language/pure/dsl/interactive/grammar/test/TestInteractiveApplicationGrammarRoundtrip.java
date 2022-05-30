package org.finos.legend.engine.language.pure.dsl.interactive.grammar.test;

import org.finos.legend.engine.language.pure.grammar.test.TestGrammarRoundtrip;
import org.junit.Test;

public class TestInteractiveApplicationGrammarRoundtrip extends TestGrammarRoundtrip.TestGrammarRoundtripTestSuite implements TestInteractiveApplicationGrammar
{
    @Test
    public void roundTrip()
    {
        test("###InteractiveApplication\n" + this.defaultApplication());
    }
}
