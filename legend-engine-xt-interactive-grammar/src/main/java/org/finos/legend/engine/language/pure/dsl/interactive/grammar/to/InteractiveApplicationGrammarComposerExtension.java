package org.finos.legend.engine.language.pure.dsl.interactive.grammar.to;

import org.eclipse.collections.api.block.function.Function3;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.utility.LazyIterate;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.dsl.interactive.grammar.from.InteractiveApplicationParserExtension;
import org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerContext;
import org.finos.legend.engine.language.pure.grammar.to.extension.PureGrammarComposerExtension;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveApplication;

import java.util.List;

public class InteractiveApplicationGrammarComposerExtension implements PureGrammarComposerExtension
{
    @Override
    public List<Function3<List<PackageableElement>, PureGrammarComposerContext, String, String>> getExtraSectionComposers()
    {
        return Lists.fixedSize.of((elements, context, sectionName) ->
                                  {
                                      if (!InteractiveApplicationParserExtension.NAME.equals(sectionName))
                                      {
                                          return null;
                                      }
                                      return ListIterate.collect(elements, element ->
                                      {
                                          if (element instanceof InteractiveApplication)
                                          {
                                              return renderInteractiveApplication((InteractiveApplication) element, context);
                                          }
                                          return "/* Can't transform element '" + element.getPath() + "' in this section */";
                                      }).makeString("\n\n");
                                  });
    }

    @Override
    public List<Function3<List<PackageableElement>, PureGrammarComposerContext, List<String>, PureFreeSectionGrammarComposerResult>> getExtraFreeSectionComposers()
    {
        return Lists.fixedSize.of((elements, context, composedSections) ->
                                  {
                                      List<InteractiveApplication> composableElements = ListIterate.selectInstancesOf(elements, InteractiveApplication.class);
                                      return composableElements.isEmpty() ? null : new PureFreeSectionGrammarComposerResult(LazyIterate.collect(composableElements, el -> InteractiveApplicationGrammarComposerExtension.renderInteractiveApplication(el, context)).makeString("###" + InteractiveApplicationParserExtension.NAME + "\n", "\n\n", ""), composableElements);
                                  });
    }

    private static String renderInteractiveApplication(InteractiveApplication interactiveApplication, PureGrammarComposerContext context)
    {
        return HelperInteractiveApplicationGrammarComposer.renderInteractiveApplication(interactiveApplication, 1, context);
    }
}
