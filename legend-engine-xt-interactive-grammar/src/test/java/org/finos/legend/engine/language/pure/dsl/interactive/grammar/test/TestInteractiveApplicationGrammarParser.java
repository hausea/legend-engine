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
