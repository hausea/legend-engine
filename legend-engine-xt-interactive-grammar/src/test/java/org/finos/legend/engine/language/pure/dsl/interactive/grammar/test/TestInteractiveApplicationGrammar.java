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
                "    ReadService(id: Integer[1] -> firmById)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};\n" +
                "    }\n" +
                "    CreateService(firm: Firm[1] -> createFirm)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {|[]->cast(@Firm)};\n" +
                "    }\n" +
                "    UpdateService(id: Integer[1], firm: Firm[1] -> updateFirm)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};\n" +
                "    }\n" +
                "    UpsertService(id: Integer[1], firm: Firm[1] -> upsertFirm)\n" +
                "    {\n" +
                "      authorization: [None];\n" +
                "      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};\n" +
                "    }\n" +
                "    DeleteService(id: Integer[1] -> deleteFirm)\n" +
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
    ReadService(id: Integer[1] -> firmById)
    {
      authorization: [None];
      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};
    }
    CreateService(firm: Firm[1] -> createFirm)
    {
      authorization: [None];
      query: {|[]};
    }
    UpdateService(id: Integer[1], firm: Firm[1] -> updateFirm)
    {
      authorization: [None];
      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};
    }
    UpsertService(id: Integer[1], firm: Firm[1] -> upsertFirm)
    {
      authorization: [None];
      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};
    }
    DeleteService(id: Integer[1] -> deleteFirm)
    {
      authorization: [None];
      query: {id: Integer[1]|Firm.all()->filter(f|$f.id == $id)};
    }
  }
}

     */
}
