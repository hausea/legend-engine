package org.finos.legend.engine.language.pure.dsl.interactive.compiler.toPureGraph;

import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.impl.factory.Lists;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.CompileContext;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.ProcessingContext;
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
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.configuration.type.primarykey.InteractiveTypePrimaryKeysPrimaryKeyConfiguration;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.CreateInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.InteractiveServiceVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.service.ReadInteractiveService;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.InteractiveApplicationStore;
import org.finos.legend.pure.generated.*;
import org.finos.legend.pure.m3.coreinstance.meta.pure.graphFetch.GraphFetchTree;
import org.finos.legend.pure.m3.coreinstance.meta.pure.graphFetch.RootGraphFetchTree;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.Property;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Enum;

import java.util.List;

import static org.finos.legend.engine.language.pure.compiler.toPureGraph.HelperValueSpecificationBuilder.buildGraphFetchTree;
import static org.finos.legend.engine.language.pure.compiler.toPureGraph.HelperValueSpecificationBuilder.buildLambda;

public class HelperInteractiveApplicationBuilder
{
    private static final String INTERACTIVE_APPLICATION_PACKAGE_PREFIX = "meta::pure::crud::metamodel";

    private HelperInteractiveApplicationBuilder()
    {
    }

    public static Root_meta_pure_crud_metamodel_InteractiveAuthorization buildGlobalAuthorization(InteractiveAuthorization globalAuthorization, CompileContext context)
    {
        Root_meta_pure_crud_metamodel_InteractiveAuthorization pureInteractiveAuthorization = new Root_meta_pure_crud_metamodel_InteractiveAuthorization_Impl("");
        pureInteractiveAuthorization._authorizationFunction(buildLambda(globalAuthorization.authorizationFunction, context));
        return pureInteractiveAuthorization;
    }

    public static Root_meta_pure_crud_metamodel_InteractiveApplicationStore buildStore(InteractiveApplicationStore store, CompileContext context)
    {
        Root_meta_pure_crud_metamodel_InteractiveApplicationStore pureStore = new Root_meta_pure_crud_metamodel_InteractiveApplicationStore_Impl("");
//        pureStore._generateStore(buildLambda(store.generateStore, context));
//        pureStore._generateMapping(buildLambda(store.generateMapping, context));
        pureStore._connection(buildLambda(store.connection, context));
        return pureStore;
    }

    public static RichIterable<? extends Root_meta_pure_crud_metamodel_InteractiveType> buildTypes(List<InteractiveType> types, CompileContext context)
    {
        return ListAdapter.adapt(types)
                .collect(type ->
                         {
                             Root_meta_pure_crud_metamodel_InteractiveType pureInteractiveType = new Root_meta_pure_crud_metamodel_InteractiveType_Impl("");
                             Class<?> baseClass = context.pureModel.getClass(type.baseClass);
                             pureInteractiveType._baseClass(baseClass);

                             // root graph fetch tree
                             GraphFetchTree graphFetchTree = buildGraphFetchTree(type.graphScope, context, baseClass, Lists.mutable.empty(), new ProcessingContext(""));
                             pureInteractiveType._graphScope((RootGraphFetchTree<? extends Object>) graphFetchTree);

                             // configuration
                             MutableList<Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration> pureConfigurations =
                                     ListAdapter.adapt(type.configuration).collect(tc -> HelperInteractiveApplicationBuilder.buildTypeConfiguration(tc, baseClass, context));
                             pureInteractiveType._configuration(pureConfigurations);

                             // services
                             MutableList<Root_meta_pure_crud_metamodel_InteractiveService> pureServices =
                                     ListAdapter.adapt(type.services).collectWith(HelperInteractiveApplicationBuilder::buildService, context);
                             pureInteractiveType._services(pureServices);

                             return pureInteractiveType;
                         });
    }

    public static Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration buildTypeConfiguration(InteractiveTypeConfiguration typeConfiguration, Class<?> baseClass, CompileContext context)
    {
        return typeConfiguration.accept(new InteractiveTypeConfigurationBuilder(baseClass, context));
    }

    private static Root_meta_pure_crud_metamodel_InteractiveService buildService(InteractiveService interactiveService, CompileContext context)
    {
        return interactiveService.accept(new InteractiveServiceBuilder(context));
    }

    private static class InteractiveTypeConfigurationBuilder implements InteractiveTypeConfigurationVisitor<Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration>
    {
        private final Class<?> baseClass;
        private final CompileContext context;

        public InteractiveTypeConfigurationBuilder(Class<?> baseClass, CompileContext context)
        {
            this.baseClass = baseClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration visit(InteractiveTypeTypeConfiguration val)
        {
            return val.accept(new InteractiveTypeTypeConfigurationBuilder(this.baseClass, this.context));
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration visit(InteractiveTypePropertyConfiguration val)
        {
            return val.accept(new InteractiveTypePropertyConfigurationBuilder(this.baseClass, this.context));
        }
    }

    private static class InteractiveTypeTypeConfigurationBuilder implements InteractiveTypeTypeConfigurationVisitor<Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration>
    {
        private final Class<?> baseClass;
        private final CompileContext context;

        public InteractiveTypeTypeConfigurationBuilder(Class<?> baseClass, CompileContext context)
        {
            this.baseClass = baseClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration visit(InteractiveTypePrimaryKeysConfiguration val)
        {
            Root_meta_pure_crud_metamodel_InteractiveTypePrimaryKeysConfiguration purePrimaryKeys =
                    new Root_meta_pure_crud_metamodel_InteractiveTypePrimaryKeysConfiguration_Impl("");
            purePrimaryKeys._primaryKeys(ListAdapter.adapt(val.primaryKeys).collect(this::buildPrimaryKey));
            return purePrimaryKeys;
        }

        private Root_meta_pure_crud_metamodel_InteractiveTypePrimaryKeysPrimaryKeyConfiguration buildPrimaryKey(InteractiveTypePrimaryKeysPrimaryKeyConfiguration primaryKeyConfiguration)
        {
            Root_meta_pure_crud_metamodel_InteractiveTypePrimaryKeysPrimaryKeyConfiguration purePrimaryKey =
                    new Root_meta_pure_crud_metamodel_InteractiveTypePrimaryKeysPrimaryKeyConfiguration_Impl("");

            Property<?, ?> pkProperty = baseClass._properties().select(p -> p._name().equals(primaryKeyConfiguration.property)).getOnly();
            purePrimaryKey._property(pkProperty);

            Enum pureStrategy = this.context.resolveEnumValue("meta::pure::crud::metamodel::PrimaryKeyStrategy", primaryKeyConfiguration.strategy.name());
            purePrimaryKey._strategy(pureStrategy);

            return purePrimaryKey;
        }
    }

    private static class InteractiveTypePropertyConfigurationBuilder implements InteractiveTypePropertyConfigurationVisitor<Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration>
    {
        private final Class<?> baseClass;
        private final CompileContext context;

        public InteractiveTypePropertyConfigurationBuilder(Class<?> baseClass, CompileContext context)
        {
            this.baseClass = baseClass;
            this.context = context;
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveTypeConfiguration visit(InteractiveTypeStringPropertyConfiguration val)
        {
            Root_meta_pure_crud_metamodel_InteractiveTypeStringPropertyConfiguration pureStringProperty =
                    new Root_meta_pure_crud_metamodel_InteractiveTypeStringPropertyConfiguration_Impl("");

            Property<?, ?> stringProperty = baseClass._properties().select(p -> p._name().equals(val.property)).getOnly();
            pureStringProperty._property(stringProperty);

            pureStringProperty._minLength(val.minLength.longValue());
            pureStringProperty._maxLength(val.maxLength.longValue());

            return pureStringProperty;
        }
    }

    private static class InteractiveServiceBuilder implements InteractiveServiceVisitor<Root_meta_pure_crud_metamodel_InteractiveService>
    {
        private final CompileContext context;

        public InteractiveServiceBuilder(CompileContext context)
        {
            this.context = context;
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveService visit(CreateInteractiveService val)
        {
            throw new UnsupportedOperationException("not yet supported.");
        }

        @Override
        public Root_meta_pure_crud_metamodel_InteractiveService visit(ReadInteractiveService val)
        {
            Root_meta_pure_crud_metamodel_ReadInteractiveService pureReadService =
                    new Root_meta_pure_crud_metamodel_ReadInteractiveService_Impl("");

            pureReadService._name(val.name);

            Root_meta_pure_crud_metamodel_InteractiveAuthorization pureInteractiveAuthorization = new Root_meta_pure_crud_metamodel_InteractiveAuthorization_Impl("");
            pureInteractiveAuthorization._authorizationFunction(buildLambda(val.authorization.authorizationFunction, this.context));
            pureReadService._authorization(pureInteractiveAuthorization);

            pureReadService._query(buildLambda(val.query, this.context));

            return pureReadService;
        }
    }
}
