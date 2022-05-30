package org.finos.legend.engine.language.pure.dsl.interactive.grammar.test;

public interface TestInteractiveApplicationGrammar
{
    default String defaultApplication()
    {
        return "Application(meta::demo::crud::DemoApp)\n" +
                "{\n" +
                "  doc: 'Demo Application';\n" +
                "  store: Relational\n" +
                "  {\n" +
                "    type: H2;\n" +
                "  }\n" +
                "  globalAuthorization: [None];\n" +
                "  RootType(Firm)\n" +
                "  {\n" +
                "    graphScope: ();\n" +
                "    primaryKey: id -> NONE;\n" +
                "    stringLength: name -> 256;\n" +
                "    ReadService(allFirms)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {|Firm.all()};\n" +
                "    }\n" +
                "    ReadService(firmById)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};\n" +
                "    }\n" +
                "  }\n" +
                "}\n";
    }

    /*
Application(meta::demo::crud::DemoApp)
{
  doc: 'Demo Application';
  store: Relational
  {
    type: H2;
  }
  globalAuthorization: [None];
  RootType(Firm)
  {
    graphScope: ();
    primaryKey: id -> NONE;
    stringLength: name -> 256;
    ReadService(allFirms)
    {
      authorization: [None];
      query: {|Firm.all()};
    }
    ReadService(firmById)
    {
      authorization: [None];
      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};
    }
  }
}

     */
}
