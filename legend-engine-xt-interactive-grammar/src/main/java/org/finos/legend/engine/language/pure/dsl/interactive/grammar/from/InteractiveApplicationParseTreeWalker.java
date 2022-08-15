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

package org.finos.legend.engine.language.pure.dsl.interactive.grammar.from;

import org.antlr.v4.runtime.ParserRuleContext;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.eclipse.collections.impl.utility.ListIterate;
import org.finos.legend.engine.language.pure.grammar.from.ParseTreeWalkerSourceInformation;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParser;
import org.finos.legend.engine.language.pure.grammar.from.PureGrammarParserUtility;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationParserGrammar;
import org.finos.legend.engine.language.pure.grammar.from.antlr4.InteractiveApplicationParserGrammarBaseVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.SourceInformation;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveApplication;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.InteractiveType;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.DefaultInteractiveAuthorization;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.InteractiveAuthorization;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.InteractiveTypeConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.property.InteractiveTypeStringPropertyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysPrimaryKeyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.PrimaryKeyStrategy;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.CreateInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.DeleteInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.ReadInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.UpdateInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.UpsertInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.InteractiveApplicationStore;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.RelationalInteractiveApplicationStore;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.domain.Multiplicity;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.section.ImportAwareCodeSection;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.Variable;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.Lambda;
import org.finos.legend.engine.protocol.pure.v1.model.valueSpecification.raw.graph.RootGraphFetchTree;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class InteractiveApplicationParseTreeWalker
{
    private final ParseTreeWalkerSourceInformation walkerSourceInformation;
    private final Consumer<PackageableElement> elementConsumer;
    private final ImportAwareCodeSection section;
    private final PureGrammarParser pureGrammarParser;


    public InteractiveApplicationParseTreeWalker(ParseTreeWalkerSourceInformation walkerSourceInformation, Consumer<PackageableElement> elementConsumer, ImportAwareCodeSection section, PureGrammarParser pureGrammarParser)
    {
        this.walkerSourceInformation = walkerSourceInformation;
        this.elementConsumer = elementConsumer;
        this.section = section;
        this.pureGrammarParser = pureGrammarParser;
    }

    /**********
     * interactive application
     **********/

    public void visit(InteractiveApplicationParserGrammar.DefinitionContext ctx)
    {
        this.section.imports = ListIterate.collect(ctx.imports().importStatement(), importCtx -> PureGrammarParserUtility.fromPath(importCtx.packagePath().identifier()));
        ctx.application().stream().map(this::visitApplication).peek(e -> this.section.elements.add(e.getPath())).forEach(this.elementConsumer);
    }

    private InteractiveApplication visitApplication(InteractiveApplicationParserGrammar.ApplicationContext ctx)
    {
        InteractiveApplication interactiveApplication = new InteractiveApplication();
        interactiveApplication.name = PureGrammarParserUtility.fromIdentifier(ctx.qualifiedName().identifier());
        interactiveApplication._package = ctx.qualifiedName().packagePath() == null ? "" : PureGrammarParserUtility.fromPath(ctx.qualifiedName().packagePath().identifier());
        interactiveApplication.sourceInformation = this.walkerSourceInformation.getSourceInformation(ctx);

        // documentation
        InteractiveApplicationParserGrammar.DocumentationContext documentationContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.documentation(), "doc", interactiveApplication.sourceInformation);
        interactiveApplication.documentation = PureGrammarParserUtility.fromGrammarString(documentationContext.STRING().getText(), true);

        // global authorization
        InteractiveApplicationParserGrammar.GlobalAuthorizationContext globalAuthorizationContext = PureGrammarParserUtility.validateAndExtractOptionalField(ctx.globalAuthorization(), "globalAuthorization", interactiveApplication.sourceInformation);
        interactiveApplication.globalAuthorization = this.visitGlobalInteractiveAuthorization(globalAuthorizationContext);

        // store
        InteractiveApplicationParserGrammar.StoreContext storeContext = PureGrammarParserUtility.validateAndExtractRequiredField(ctx.store(), "store", interactiveApplication.sourceInformation);
        interactiveApplication.store = this.visitStore(storeContext);

        // types
        interactiveApplication.types = ctx.rootType().stream().map(this::visitTypes).collect(Collectors.toList());

        return interactiveApplication;
    }

    private InteractiveAuthorization visitGlobalInteractiveAuthorization(InteractiveApplicationParserGrammar.GlobalAuthorizationContext globalAuthorizationContext)
    {
        if (globalAuthorizationContext != null)
        {
            if (globalAuthorizationContext.NONE() != null)
            {
                DefaultInteractiveAuthorization defaultInteractiveAuthorization = new DefaultInteractiveAuthorization();
                defaultInteractiveAuthorization.authorizationFunction = this.pureGrammarParser.parseLambda("|true");
                return defaultInteractiveAuthorization;
            }

            throw new UnsupportedOperationException("Only 'None' is supported for global authorization.");
        }
        return null;
    }

    private InteractiveApplicationStore visitStore(InteractiveApplicationParserGrammar.StoreContext storeContext)
    {
        if (storeContext.relationalStore() != null)
        {
            return this.visitRelationalStore(storeContext);
        }

        throw new UnsupportedOperationException("Unsupported store type.");
    }

    private InteractiveApplicationStore visitRelationalStore(InteractiveApplicationParserGrammar.StoreContext storeContext)
    {
        SourceInformation sourceInformation = this.walkerSourceInformation.getSourceInformation(storeContext);
        InteractiveApplicationParserGrammar.RelationalTypeContext relationalTypeContext =
                PureGrammarParserUtility.validateAndExtractRequiredField(storeContext.relationalStore().relationalType(), "type", sourceInformation);

        InteractiveApplicationStore interactiveApplicationStore = new RelationalInteractiveApplicationStore();

        // meta::pure::metamodel::type::Class<meta::pure::metamodel::type::Any>
//        interactiveApplicationStore.generateStore =
//                this.pureGrammarParser.parseLambda("{classes: meta::pure::metamodel::type::Class[0..*], interactiveApplication: meta::pure::crud::metamodel::InteractiveApplication[1]|meta::pure::relational::crud::functions::classesToStore($classes, $interactiveApplication)}");
//        interactiveApplicationStore.generateMapping =
//                this.pureGrammarParser.parseLambda("{classes: meta::pure::metamodel::type::Class[0..*], store: meta::pure::store::Store[1], interactiveApplication: meta::pure::crud::metamodel::InteractiveApplication[1]|meta::pure::relational::crud::functions::classesToMapping($classes, $store->cast(@Database), $interactiveApplication)}");

        //TODO: AJH: add support for other relational stores
        if (relationalTypeContext.relationalTypeOption().RELATIONAL_TYPE_H2() != null)
        {
            //TODO: AJH: use the xt relational specification instead
//            interactiveApplicationStore.connection =
//                    this.pureGrammarParser.parseLambda("{store: meta::pure::store::Store[1]|^meta::relational::runtime::TestDatabaseConnection(element = $store, type = meta::relational::runtime::DatabaseType.H2)}");
            interactiveApplicationStore.connection =
                    this.pureGrammarParser.parseLambda(("{store: meta::pure::store::Store[1]|^meta::pure::alloy::connections::RelationalDatabaseConnection(type = meta::relational::runtime::DatabaseType.H2, element = $store, datasourceSpecification = ^meta::pure::alloy::connections::alloy::specification::LocalH2DatasourceSpecification(testDataSetupSqls = ['DROP TABLE IF EXISTS FIRM;CREATE TABLE FIRM (ID INTEGER PRIMARY KEY, NAME VARCHAR(256));INSERT INTO FIRM (ID, NAME) VALUES (1, \\'GS\\');INSERT INTO FIRM (ID, NAME) VALUES (2, \\'Tesla\\');INSERT INTO FIRM (ID, NAME) VALUES (3, \\'QCSP\\');']), authenticationStrategy = ^meta::pure::alloy::connections::alloy::authentication::TestDatabaseAuthenticationStrategy())}"));

            return interactiveApplicationStore;
        }

        throw new UnsupportedOperationException("Unsupported relational store type.");
    }

    private InteractiveType visitTypes(InteractiveApplicationParserGrammar.RootTypeContext typeContext)
    {
        InteractiveType interactiveType = new InteractiveType();
        String baseClassName = PureGrammarParserUtility.fromIdentifier(typeContext.qualifiedName().identifier());
        ;
        String baseClassPackage = typeContext.qualifiedName().packagePath() == null ? "" : PureGrammarParserUtility.fromPath(typeContext.qualifiedName().packagePath().identifier());
        interactiveType.baseClass = baseClassPackage.isEmpty() ? baseClassName : baseClassPackage + "::" + baseClassName;

        //TODO: AJH: deep fetch goodness
        RootGraphFetchTree rootGraphFetchTree = new RootGraphFetchTree();
        rootGraphFetchTree._class = interactiveType.baseClass;
        interactiveType.graphScope = rootGraphFetchTree;

        // type configuration
        interactiveType.configuration = this.visitTypeConfiguration(typeContext);

        // services
        interactiveType.services = this.visitTypeServices(typeContext);

        return interactiveType;
    }

    private List<InteractiveService> visitTypeServices(InteractiveApplicationParserGrammar.RootTypeContext typeContext)
    {
        return ListAdapter.adapt(typeContext.service()).collectWith(InteractiveApplicationParserGrammar.ServiceContext::accept, new ParseInteractiveServiceVisitor());
    }

    private InteractiveAuthorization visitInteractiveAuthorization(InteractiveApplicationParserGrammar.AuthorizationContext authorizationContext)
    {
        if (authorizationContext != null)
        {
            if (authorizationContext.NONE() != null)
            {
                DefaultInteractiveAuthorization defaultInteractiveAuthorization = new DefaultInteractiveAuthorization();
                defaultInteractiveAuthorization.authorizationFunction = this.pureGrammarParser.parseLambda("|true");
                return defaultInteractiveAuthorization;
            }

            throw new UnsupportedOperationException("Only 'None' is supported for authorization.");
        }
        return null;
    }

    private List<InteractiveTypeConfiguration> visitTypeConfiguration(InteractiveApplicationParserGrammar.RootTypeContext typeContext)
    {
        List<InteractiveTypeConfiguration> configuration =
                new ArrayList<>(1 + typeContext.stringLength().size());

        // primary keys
        configuration.add(this.visitPrimaryKeyTypeConfiguration(typeContext));

        // string length
        configuration.addAll(this.visitStringLengthTypeConfiguration(typeContext));

        return configuration;
    }

    private InteractiveTypePrimaryKeysConfiguration visitPrimaryKeyTypeConfiguration(InteractiveApplicationParserGrammar.RootTypeContext typeContext)
    {
        InteractiveTypePrimaryKeysConfiguration primaryKeysConfiguration = new InteractiveTypePrimaryKeysConfiguration();
        primaryKeysConfiguration.primaryKeys = typeContext
                .primaryKey()
                .stream()
                .map(primaryKeyContext ->
                     {
                         InteractiveTypePrimaryKeysPrimaryKeyConfiguration primaryKeyConfiguration = new InteractiveTypePrimaryKeysPrimaryKeyConfiguration();
                         primaryKeyConfiguration.property = primaryKeyContext.identifier().getText();
                         if (primaryKeyContext.primaryKeyStrategy().PRIMARY_KEY_STRATEGY_NONE() != null)
                         {
                             primaryKeyConfiguration.strategy = PrimaryKeyStrategy.NONE;
                         }
                         else if (primaryKeyContext.primaryKeyStrategy().PRIMARY_KEY_STRATEGY_MAX() != null)
                         {
                             primaryKeyConfiguration.strategy = PrimaryKeyStrategy.MAX;
                         }
                         else if (primaryKeyContext.primaryKeyStrategy().PRIMARY_KEY_STRATEGY_SYNTHETIC() != null)
                         {
                             primaryKeyConfiguration.strategy = PrimaryKeyStrategy.SYNTHETIC;
                         }
                         return primaryKeyConfiguration;
                     }).collect(Collectors.toList());
        return primaryKeysConfiguration;
    }

    private List<InteractiveTypeStringPropertyConfiguration> visitStringLengthTypeConfiguration(InteractiveApplicationParserGrammar.RootTypeContext typeContext)
    {
        return typeContext
                .stringLength()
                .stream()
                .map(stringLengthContext ->
                     {
                         InteractiveTypeStringPropertyConfiguration stringLengthConfiguration = new InteractiveTypeStringPropertyConfiguration();
                         stringLengthConfiguration.property = stringLengthContext.identifier().getText();
                         stringLengthConfiguration.maxLength = Integer.parseInt(stringLengthContext.stringLengthValue().INTEGER().toString());
                         stringLengthConfiguration.minLength = 0;
                         return stringLengthConfiguration;
                     }).collect(Collectors.toList());
    }

    private class ParseInteractiveServiceVisitor extends InteractiveApplicationParserGrammarBaseVisitor<InteractiveService>
    {
        private <T extends InteractiveService> T parseBaseService(
                T interactiveService,
                ParserRuleContext serviceContext,
                Function0<InteractiveApplicationParserGrammar.ServiceDescriptionContext> serviceDescriptionContextFunction0,
                Function0<List<InteractiveApplicationParserGrammar.AuthorizationContext>> authorizationContextFunction0)
        {
            SourceInformation sourceInformation = InteractiveApplicationParseTreeWalker.this.walkerSourceInformation.getSourceInformation(serviceContext);

            interactiveService.name = serviceDescriptionContextFunction0.value().identifier().getText();

            interactiveService.parameters = ListAdapter.adapt(serviceDescriptionContextFunction0.value().lambdaParam())
                    .collect(lambdaParam ->
                             {
                                 Variable variable = new Variable();
                                 InteractiveApplicationParserGrammar.LambdaParamTypeContext lambdaParamTypeContext = lambdaParam.lambdaParamType();
                                 variable.multiplicity = this.buildMultiplicity(lambdaParamTypeContext.multiplicity().multiplicityArgument());
                                 variable._class = lambdaParamTypeContext.type().getText();
                                 variable.sourceInformation = walkerSourceInformation.getSourceInformation(lambdaParamTypeContext.type());
                                 variable.name = PureGrammarParserUtility.fromIdentifier(lambdaParam.identifier());
                                 return variable;
                             });

            // authorization
            InteractiveApplicationParserGrammar.AuthorizationContext authorization = PureGrammarParserUtility.validateAndExtractOptionalField(authorizationContextFunction0.value(), "authorization", sourceInformation);
            interactiveService.authorization = InteractiveApplicationParseTreeWalker.this.visitInteractiveAuthorization(authorization);

            return interactiveService;
        }

        private Lambda parseQuery(ParserRuleContext serviceContext, List<InteractiveApplicationParserGrammar.QueryContext> queryContexts)
        {
            SourceInformation sourceInformation = InteractiveApplicationParseTreeWalker.this.walkerSourceInformation.getSourceInformation(serviceContext);

            InteractiveApplicationParserGrammar.QueryContext queryContext = PureGrammarParserUtility.validateAndExtractRequiredField(queryContexts, "query", sourceInformation);
            return InteractiveApplicationParseTreeWalker.this.pureGrammarParser.parseLambda(queryContext.lambdaFunction().getText());
        }

        //TODO: AJH: straight copy from DomainParserTreeWalker
        private Multiplicity buildMultiplicity(InteractiveApplicationParserGrammar.MultiplicityArgumentContext ctx)
        {
            Multiplicity m = new Multiplicity();
            String star = "*";
            if (ctx.fromMultiplicity() == null)
            {
                m.lowerBound = Integer.parseInt(star.equals(ctx.toMultiplicity().getText()) ? "0" : ctx.toMultiplicity().getText());
            }
            else
            {
                m.lowerBound = Integer.parseInt(ctx.fromMultiplicity().getText());
            }

            if (!star.equals(ctx.toMultiplicity().getText()))
            {
                m.setUpperBound(Integer.parseInt(ctx.toMultiplicity().getText()));
            }
            else
            {
                m.setUpperBound(null);
            }
            return m;
        }

        @Override
        public InteractiveService visitReadService(InteractiveApplicationParserGrammar.ReadServiceContext ctx)
        {
            ReadInteractiveService readInteractiveService = this.parseBaseService(
                    new ReadInteractiveService(),
                    ctx,
                    ctx::serviceDescription,
                    ctx::authorization);
            readInteractiveService.query = this.parseQuery(ctx, ctx.query());
            return readInteractiveService;
        }

        @Override
        public InteractiveService visitCreateService(InteractiveApplicationParserGrammar.CreateServiceContext ctx)
        {
            return this.parseBaseService(
                    new CreateInteractiveService(),
                    ctx,
                    ctx::serviceDescription,
                    ctx::authorization);
        }

        @Override
        public InteractiveService visitUpdateService(InteractiveApplicationParserGrammar.UpdateServiceContext ctx)
        {
            UpdateInteractiveService updateInteractiveService = this.parseBaseService(
                    new UpdateInteractiveService(),
                    ctx,
                    ctx::serviceDescription,
                    ctx::authorization);
            updateInteractiveService.query = this.parseQuery(ctx, ctx.query());
            return updateInteractiveService;
        }

        @Override
        public InteractiveService visitUpsertService(InteractiveApplicationParserGrammar.UpsertServiceContext ctx)
        {
            UpsertInteractiveService upsertInteractiveService = this.parseBaseService(
                    new UpsertInteractiveService(),
                    ctx,
                    ctx::serviceDescription,
                    ctx::authorization);
            upsertInteractiveService.query = this.parseQuery(ctx, ctx.query());
            return upsertInteractiveService;
        }

        @Override
        public InteractiveService visitDeleteService(InteractiveApplicationParserGrammar.DeleteServiceContext ctx)
        {
            DeleteInteractiveService deleteInteractiveService = this.parseBaseService(
                    new DeleteInteractiveService(),
                    ctx,
                    ctx::serviceDescription,
                    ctx::authorization);
            deleteInteractiveService.query = this.parseQuery(ctx, ctx.query());
            return deleteInteractiveService;
        }
    }
}
