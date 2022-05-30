package org.finos.legend.engine.language.pure.dsl.interactive.grammar.from;

import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.CharStreams;
import org.antlr.v4.runtime.CommonTokenStream;
import org.eclipse.collections.impl.factory.Lists;
import org.finos.legend.engine.language.pure.grammar.from.*;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationLexerGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.connection.ConnectionParser;
import org.finos.legend.engine.language.pure.grammar.from.domain.DomainParser;
import org.finos.legend.engine.language.pure.grammar.from.extension.PureGrammarParserExtension;
import org.finos.legend.engine.language.pure.grammar.from.extension.SectionParser;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.ImportAwareCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.Section;

import java.util.function.Consumer;

public class InteractiveApplicationParserExtension implements PureGrammarParserExtension
{
    public static final String NAME = "InteractiveApplication";

    @Override
    public Iterable<? extends SectionParser> getExtraSectionParsers()
    {
        return Lists.fixedSize.of(SectionParser.newParser(NAME, InteractiveApplicationParserExtension::parseSection));
    }

    private static Section parseSection(SectionSourceCode sectionSourceCode, Consumer<PackageableElement> elementConsumer, PureGrammarParserContext context)
    {
        SourceCodeParserInfo parserInfo = getInteractiveApplicationParserInfo(sectionSourceCode);
        ImportAwareCodeSection section = new ImportAwareCodeSection();
        section.parserName = sectionSourceCode.sectionType;
        section.sourceInformation = parserInfo.sourceInformation;

        PureGrammarParser pureGrammarParser = PureGrammarParser.newInstance(context.getPureGrammarParserExtensions());
        InteractiveApplicationParseTreeWalker walker = new InteractiveApplicationParseTreeWalker(parserInfo.walkerSourceInformation, elementConsumer, section, pureGrammarParser);
        walker.visit((InteractiveApplicationParserGrammar.DefinitionContext) parserInfo.rootContext);

        return section;
    }

    private static SourceCodeParserInfo getInteractiveApplicationParserInfo(SectionSourceCode sectionSourceCode)
    {
        CharStream input = CharStreams.fromString(sectionSourceCode.code);
        ParserErrorListener errorListener = new InteractiveApplicationParserErrorListener(sectionSourceCode.walkerSourceInformation);
        InteractiveApplicationLexerGrammar lexer = new InteractiveApplicationLexerGrammar(input);
        lexer.removeErrorListeners();
        lexer.addErrorListener(errorListener);
        InteractiveApplicationParserGrammar parser = new InteractiveApplicationParserGrammar(new CommonTokenStream(lexer));
        parser.removeErrorListeners();
        parser.addErrorListener(errorListener);
        return new SourceCodeParserInfo(sectionSourceCode.code, input, sectionSourceCode.sourceInformation, sectionSourceCode.walkerSourceInformation, lexer, parser, parser.definition());
    }
}
