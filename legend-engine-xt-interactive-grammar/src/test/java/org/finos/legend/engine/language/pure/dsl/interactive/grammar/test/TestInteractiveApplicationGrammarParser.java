package org.finos.legend.engine.language.pure.dsl.interactive.grammar.test;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.antlr.v4.runtime.Vocabulary;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationParserGrammar;
import org.finos.legend.engine.language.pure.grammar.test.TestGrammarParser;
import org.junit.Ignore;
import org.junit.Test;

import java.util.List;

public class TestInteractiveApplicationGrammarParser extends TestGrammarParser.TestGrammarParserTestSuite implements TestInteractiveApplicationGrammar
{
    @Override
    public Vocabulary getParserGrammarVocabulary()
    {
        return InteractiveApplicationParserGrammar.VOCABULARY;
    }

    @Override
    public String getParserGrammarIdentifierInclusionTestCode(List<String> keywords)
    {
        return "###InteractiveApplication\n" + this.defaultApplication();
    }

    @Test
    @Ignore
    public void simpleAntlrParsing()
    {
        CharStream in = CharStreams.fromString(this.defaultApplication());
        InteractiveApplicationLexerGrammar lexer = new InteractiveApplicationLexerGrammar(in);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        InteractiveApplicationParserGrammar parser = new InteractiveApplicationParserGrammar(tokens);
        System.out.println("Done: " + parser.definition());
    }
}
