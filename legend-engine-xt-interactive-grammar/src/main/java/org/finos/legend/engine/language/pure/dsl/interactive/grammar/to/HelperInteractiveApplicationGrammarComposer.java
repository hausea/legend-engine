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

package org.finos.legend.engine.language.pure.dsl.interactive.grammar.to;

import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.finos.legend.engine.language.pure.grammar.to.DEPRECATED_PureGrammarComposerCore;
import org.finos.legend.engine.language.pure.grammar.to.HelperDomainGrammarComposer;
import org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerContext;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveApplication;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.InteractiveAuthorization;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfigurationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypePropertyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypePropertyConfigurationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypeStringPropertyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.InteractiveTypeTypeConfigurationVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.CreateInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.DeleteInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveServiceVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.ReadInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.UpdateInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.UpsertInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.InteractiveApplicationStore;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.RelationalInteractiveApplicationStore;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.Variable;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;

import java.util.List;

import static org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerUtility.convertPath;
import static org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerUtility.convertString;
import static org.finos.legend.engine.language.pure.grammar.to.PureGrammarComposerUtility.getTabString;

public class HelperInteractiveApplicationGrammarComposer
{
    private HelperInteractiveApplicationGrammarComposer()
    {
    }

    public static String renderInteractiveApplication(InteractiveApplication interactiveApplication, int indentLevel, PureGrammarComposerContext context)
    {
        return "Application(" + convertPath(interactiveApplication.getPath()) + ")\n" +
                "{\n" +
                renderDocumentation(interactiveApplication.documentation, indentLevel) +
                renderStore(interactiveApplication.store, indentLevel) +
                renderGlobalAuthorization(interactiveApplication.globalAuthorization, indentLevel) +
                renderTypes(interactiveApplication.types, indentLevel) +
                "}";
    }

    private static String renderDocumentation(String documentation, int indentLevel)
    {
        return getTabString(indentLevel) + "doc: " + convertString(documentation, true) + ";\n";
    }

    private static String renderStore(InteractiveApplicationStore store, int indentLevel)
    {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append(getTabString(indentLevel)).append("store: ");
        if (store instanceof RelationalInteractiveApplicationStore)
        {
            stringBuilder.append("Relational\n");
            stringBuilder.append(getTabString(indentLevel)).append("{\n");
            stringBuilder.append(getTabString(indentLevel + 1)).append("type: H2;\n");
            stringBuilder.append(getTabString(indentLevel)).append("}\n");
        }
        return stringBuilder.toString();
    }

    private static String renderGlobalAuthorization(InteractiveAuthorization globalAuthorization, int indentLevel)
    {
        return getTabString(indentLevel) + "globalAuthorization: [None];\n";
    }

    private static String renderTypes(List<InteractiveType> types, int indentLevel)
    {
        return ListAdapter
                .adapt(types)
                .collect(type ->
                         {
                             StringBuilder stringBuilder = new StringBuilder();
                             stringBuilder.append(getTabString(indentLevel)).append("RootType(").append(type.baseClass).append(")\n");
                             stringBuilder.append(getTabString(indentLevel)).append("{\n");
                             stringBuilder.append(getTabString(indentLevel + 1)).append("graphScope: ();\n");
                             ListAdapter.adapt(type.configuration).collect(HelperInteractiveApplicationGrammarComposer::renderTypeConfiguration).each(s -> stringBuilder.append(getTabString(indentLevel + 1)).append(s));
                             ListAdapter.adapt(type.services).collectWith(HelperInteractiveApplicationGrammarComposer::renderService, indentLevel + 1).each(stringBuilder::append);
                             stringBuilder.append(getTabString(indentLevel)).append("}\n");
                             return stringBuilder.toString();
                         })
                .makeString("\n");
    }

    private static String renderService(InteractiveService interactiveService, int indentLevel)
    {
        return interactiveService.accept(new InteractiveServiceComposer(indentLevel));
    }

    private static String renderTypeConfiguration(InteractiveTypeConfiguration interactiveTypeConfiguration)
    {
        return interactiveTypeConfiguration.accept(new InteractiveTypeConfigurationComposer());
    }

    private static class InteractiveTypeConfigurationComposer implements InteractiveTypeConfigurationVisitor<String>
    {
        @Override
        public String visit(InteractiveTypeTypeConfiguration val)
        {
            return val.accept(new InteractiveTypeTypeConfigurationComposer());
        }

        @Override
        public String visit(InteractiveTypePropertyConfiguration val)
        {
            return val.accept(new InteractiveTypePropertyConfigurationComposer());
        }
    }

    private static class InteractiveTypePropertyConfigurationComposer implements InteractiveTypePropertyConfigurationVisitor<String>
    {
        @Override
        public String visit(InteractiveTypeStringPropertyConfiguration val)
        {
            return "stringLength: " + val.property + " -> " + val.maxLength + ";\n";
        }
    }

    private static class InteractiveTypeTypeConfigurationComposer implements InteractiveTypeTypeConfigurationVisitor<String>
    {
        @Override
        public String visit(InteractiveTypePrimaryKeysConfiguration val)
        {
            return ListAdapter.adapt(val.primaryKeys).collect(pk -> "primaryKey: " + pk.property + " -> " + pk.strategy.name() + ';').makeString("", "\n", "\n");
        }
    }

    private static class InteractiveServiceComposer implements InteractiveServiceVisitor<String>
    {
        private final int indentLevel;

        public InteractiveServiceComposer(int indentLevel)
        {
            this.indentLevel = indentLevel;
        }

        private StringBuilder appendServiceDefinition(StringBuilder stringBuilder, InteractiveService interactiveService)
        {
            stringBuilder.append(getTabString(this.indentLevel)).append(interactiveService.accept(new InteractiveServiceNameComposer()));
            stringBuilder.append("(");
            this.appendServiceParameters(stringBuilder, interactiveService.parameters);
            stringBuilder.append(interactiveService.name).append(")\n");
            stringBuilder.append(getTabString(this.indentLevel)).append("{\n");
            return stringBuilder;
        }

        private StringBuilder appendServiceParameters(StringBuilder stringBuilder, List<Variable> parameters)
        {
            if (parameters != null && !parameters.isEmpty())
            {
                String variableString =
                        ListAdapter.adapt(parameters)
                                .collect(variable -> variable.name + ": " + variable._class + "[" + HelperDomainGrammarComposer.renderMultiplicity(variable.multiplicity) + "]")
                                .makeString(", ");
                stringBuilder.append(variableString).append(" -> ");
            }
            return stringBuilder;
        }

        private StringBuilder appendAuthorization(StringBuilder stringBuilder)
        {
            return stringBuilder.append(getTabString(this.indentLevel + 1)).append("authorization: [None];\n");
        }

        private StringBuilder appendQuery(StringBuilder stringBuilder, Lambda lambda)
        {
            stringBuilder.append(getTabString(this.indentLevel + 1)).append("query: ");
            stringBuilder.append("{").append(lambda.accept(DEPRECATED_PureGrammarComposerCore.Builder.newInstance().build())).append("};\n");
            return stringBuilder;
        }

        private StringBuilder appendServiceEnd(StringBuilder stringBuilder)
        {
            return stringBuilder.append(getTabString(this.indentLevel)).append("}\n");
        }

        @Override
        public String visit(ReadInteractiveService val)
        {
            StringBuilder stringBuilder = new StringBuilder();
            this.appendServiceDefinition(stringBuilder, val);
            this.appendAuthorization(stringBuilder);
            this.appendQuery(stringBuilder, val.query);
            this.appendServiceEnd(stringBuilder);
            return stringBuilder.toString();
        }

        @Override
        public String visit(CreateInteractiveService val)
        {
            StringBuilder stringBuilder = new StringBuilder();
            this.appendServiceDefinition(stringBuilder, val);
            this.appendAuthorization(stringBuilder);
            this.appendServiceEnd(stringBuilder);
            return stringBuilder.toString();
        }

        @Override
        public String visit(UpdateInteractiveService val)
        {
            StringBuilder stringBuilder = new StringBuilder();
            this.appendServiceDefinition(stringBuilder, val);
            this.appendAuthorization(stringBuilder);
            this.appendQuery(stringBuilder, val.query);
            this.appendServiceEnd(stringBuilder);
            return stringBuilder.toString();
        }

        @Override
        public String visit(UpsertInteractiveService val)
        {
            StringBuilder stringBuilder = new StringBuilder();
            this.appendServiceDefinition(stringBuilder, val);
            this.appendAuthorization(stringBuilder);
            this.appendQuery(stringBuilder, val.query);
            this.appendServiceEnd(stringBuilder);
            return stringBuilder.toString();
        }

        @Override
        public String visit(DeleteInteractiveService val)
        {
            StringBuilder stringBuilder = new StringBuilder();
            this.appendServiceDefinition(stringBuilder, val);
            this.appendAuthorization(stringBuilder);
            this.appendQuery(stringBuilder, val.query);
            this.appendServiceEnd(stringBuilder);
            return stringBuilder.toString();
        }
    }

    private static class InteractiveServiceNameComposer implements InteractiveServiceVisitor<String>
    {
        @Override
        public String visit(ReadInteractiveService val)
        {
            return "ReadService";
        }

        @Override
        public String visit(CreateInteractiveService val)
        {
            return "CreateService";
        }

        @Override
        public String visit(UpdateInteractiveService val)
        {
            return "UpdateService";
        }

        @Override
        public String visit(UpsertInteractiveService val)
        {
            return "UpsertService";
        }

        @Override
        public String visit(DeleteInteractiveService val)
        {
            return "DeleteService";
        }
    }
}