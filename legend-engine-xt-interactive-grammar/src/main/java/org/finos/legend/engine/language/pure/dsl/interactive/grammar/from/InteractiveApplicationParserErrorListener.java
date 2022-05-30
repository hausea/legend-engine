package org.finos.legend.engine.language.pure.dsl.interactive.grammar.from;

import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.grammar.from.ParseTreeWalkerSourceInformation;
import org.finos.legend.engine.language.pure.grammar.from.ParserErrorListener;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationLexerGrammar;

import java.util.List;
import java.util.Objects;

public class InteractiveApplicationParserErrorListener extends ParserErrorListener
{
    public InteractiveApplicationParserErrorListener(ParseTreeWalkerSourceInformation walkerSourceInformation)
    {
        super(walkerSourceInformation);
    }

    @Override
    protected List<String> dereferenceTokens(List<Integer> expectedTokens)
    {
        return ListIterate.collect(expectedTokens, InteractiveApplicationLexerGrammar.VOCABULARY::getLiteralName).select(Objects::nonNull);
    }
}
