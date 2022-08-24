// Copyright 2022 Goldman Sachs
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package org.finos.legend.engine.query.graphQL.api.execute;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.opentracing.Scope;
import io.opentracing.util.GlobalTracer;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.eclipse.collections.api.RichIterable;
import org.eclipse.collections.api.block.function.Function0;
import org.eclipse.collections.api.list.ListIterable;
import org.eclipse.collections.api.list.MutableList;
import org.eclipse.collections.api.map.MapIterable;
import org.eclipse.collections.impl.list.mutable.ListAdapter;
import org.eclipse.collections.impl.tuple.Tuples;
import org.eclipse.collections.impl.utility.Iterate;
import org.finos.legend.engine.language.graphQL.grammar.from.GraphQLGrammarParser;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.HelperRuntimeBuilder;
import org.finos.legend.engine.language.pure.compiler.toPureGraph.PureModel;
import org.finos.legend.engine.language.pure.modelManager.ModelManager;
import org.finos.legend.engine.language.pure.modelManager.sdlc.configuration.MetaDataServerConfiguration;
import org.finos.legend.engine.plan.execution.PlanExecutor;
import org.finos.legend.engine.plan.execution.result.json.JsonStreamingResult;
import org.finos.legend.engine.plan.generation.PlanGenerator;
import org.finos.legend.engine.plan.generation.transformers.PlanTransformer;
import org.finos.legend.engine.plan.platform.PlanPlatform;
import org.finos.legend.engine.protocol.graphQL.metamodel.DefinitionVisitor;
import org.finos.legend.engine.protocol.graphQL.metamodel.Document;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.ExecutableDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.Field;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.FragmentDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.OperationDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.OperationType;
import org.finos.legend.engine.protocol.graphQL.metamodel.executable.Selection;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.DirectiveDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.EnumTypeDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.InterfaceTypeDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.ObjectTypeDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.ScalarTypeDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.SchemaDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.Type;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.TypeSystemDefinition;
import org.finos.legend.engine.protocol.graphQL.metamodel.typeSystem.UnionTypeDefinition;
import org.finos.legend.engine.protocol.pure.PureClientVersions;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.ExecutionPlan;
import org.finos.legend.engine.protocol.pure.v1.model.executionPlan.SingleExecutionPlan;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.runtime.Runtime;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.store.relational.connection.RelationalDatabaseConnection;
import org.finos.legend.engine.query.graphQL.api.GraphQL;
import org.finos.legend.engine.query.graphQL.api.execute.model.PlansResult;
import org.finos.legend.engine.query.graphQL.api.execute.model.Query;
import org.finos.legend.engine.query.graphQL.api.execute.model.error.GraphQLErrorMain;
import org.finos.legend.engine.shared.core.ObjectMapperFactory;
import org.finos.legend.engine.shared.core.kerberos.ProfileManagerHelper;
import org.finos.legend.engine.shared.core.operational.errorManagement.ExceptionTool;
import org.finos.legend.engine.shared.core.operational.logs.LoggingEventType;
import org.finos.legend.engine.write.api.AlloyWrite;
import org.finos.legend.engine.write.relational.RelationalAlloyWrite;
import org.finos.legend.pure.generated.Root_meta_json_JSONArray;
import org.finos.legend.pure.generated.Root_meta_json_JSONBoolean;
import org.finos.legend.pure.generated.Root_meta_json_JSONElement;
import org.finos.legend.pure.generated.Root_meta_json_JSONKeyValue;
import org.finos.legend.pure.generated.Root_meta_json_JSONNull;
import org.finos.legend.pure.generated.Root_meta_json_JSONNumber;
import org.finos.legend.pure.generated.Root_meta_json_JSONObject;
import org.finos.legend.pure.generated.Root_meta_json_JSONString;
import org.finos.legend.pure.generated.Root_meta_pure_alloy_connections_RelationalDatabaseConnection;
import org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_InteractiveApplication;
import org.finos.legend.pure.generated.Root_meta_pure_executionPlan_ExecutionPlan;
import org.finos.legend.pure.generated.Root_meta_pure_metamodel_type_Class_Impl;
import org.finos.legend.pure.generated.core_external_query_graphql_introspection_transformation;
import org.finos.legend.pure.generated.core_external_query_graphql_transformation;
import org.finos.legend.pure.generated.core_pure_executionPlan_executionPlan_print;
import org.finos.legend.pure.m3.coreinstance.meta.pure.functions.collection.Pair;
import org.finos.legend.pure.m3.coreinstance.meta.pure.mapping.Mapping;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.AbstractProperty;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.Property;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.function.property.QualifiedProperty;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.FunctionType;
import org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.valuespecification.VariableExpression;
import org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Connection;
import org.finos.legend.pure.runtime.java.compiled.generation.processors.support.map.PureMap;
import org.pac4j.core.profile.CommonProfile;
import org.pac4j.core.profile.ProfileManager;
import org.pac4j.jax.rs.annotations.Pac4JProfileManager;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.util.Collection;
import java.util.List;

import static org.finos.legend.engine.shared.core.operational.http.InflateInterceptor.APPLICATION_ZLIB;
import static org.finos.legend.pure.generated.core_relational_relational_extensions_extension.Root_meta_relational_extension_relationalExtensions__Extension_MANY_;

@Api(tags = "GraphQL - Execution")
@Path("graphQL/v1/execution")
@Produces(MediaType.APPLICATION_JSON)
public class GraphQLExecute extends GraphQL
{
    private final PlanExecutor planExecutor;
    private final MutableList<PlanTransformer> transformers;

    public GraphQLExecute(ModelManager modelManager, PlanExecutor planExecutor, MetaDataServerConfiguration metadataserver, MutableList<PlanTransformer> transformers)
    {
        super(modelManager, metadataserver);
        this.planExecutor = planExecutor;
        this.transformers = transformers;
    }

    @POST
    @ApiOperation(value = "Generate plans from a GraphQL query in the context of a Mapping and a Runtime.")
    @Path("generatePlans/prod/{groupId}/{artifact}/{version}/query/{queryClassPath}/mapping/{mappingPath}")
    @Consumes({MediaType.APPLICATION_JSON, APPLICATION_ZLIB})
    public Response generatePlansProd(@Context HttpServletRequest request, @PathParam("branch") String branch, @PathParam("projectId") String projectId, Query query, @ApiParam(hidden = true) @Pac4JProfileManager ProfileManager<CommonProfile> pm)
    {
        throw new RuntimeException("Not implemented yet");
    }

    @POST
    @ApiOperation(value = "Generate plans from a GraphQL query in the context of a Mapping and a Runtime.")
    @Path("generatePlans/dev/{projectId}/{branch}/query/{queryClassPath}/mapping/{mappingPath}")
    @Consumes({MediaType.APPLICATION_JSON, APPLICATION_ZLIB})
    public Response generatePlansDev(@Context HttpServletRequest request, @PathParam("projectId") String projectId, @PathParam("branch") String branch, @PathParam("queryClassPath") String queryClassPath, @PathParam("mappingPath") String mappingPath, Query query, @ApiParam(hidden = true) @Pac4JProfileManager ProfileManager<CommonProfile> pm)
    {
        MutableList<CommonProfile> profiles = ProfileManagerHelper.extractProfiles(pm);
        try (Scope scope = GlobalTracer.get().buildSpan("GraphQL: Execute").startActive(true))
        {
            PureModel pureModel = loadModel(profiles, request, projectId, branch);
            org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?> _class = pureModel.getClass(queryClassPath);
            Mapping mapping = pureModel.getMapping(mappingPath);
            org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime = HelperRuntimeBuilder.buildPureRuntime(ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().readValue(GraphQLExecute.class.getClassLoader().getResourceAsStream("exampleRuntime.json"), org.finos.legend.engine.protocol.pure.v1.model.packageableElement.runtime.Runtime.class), pureModel.getContext());

            Document document = GraphQLGrammarParser.newInstance().parseDocument(query.query);
            org.finos.legend.pure.generated.Root_meta_external_query_graphQL_metamodel_Document queryDoc = toPureModel(document, pureModel);
            if (isQueryIntrospection(findOperationForType(document, OperationType.query)))
            {
                return Response.ok("").type(MediaType.TEXT_HTML_TYPE).build();
            }
            else
            {
                RichIterable<? extends Pair<? extends String, ? extends Root_meta_pure_executionPlan_ExecutionPlan>> purePlans = core_external_query_graphql_transformation.Root_meta_external_query_graphQL_transformation_queryToPure_getPlansFromGraphQL_Class_1__Mapping_1__Runtime_1__Document_1__Extension_MANY__Pair_MANY_(_class, mapping, runtime, queryDoc, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), pureModel.getExecutionSupport());
                Collection<PlansResult.PlanUnit> plans = Iterate.collect(purePlans, p ->
                                                                         {
                                                                             Root_meta_pure_executionPlan_ExecutionPlan nPlan = PlanPlatform.JAVA.bindPlan(p._second(), "ID", pureModel, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()));
                                                                             try
                                                                             {
                                                                                 return new PlansResult.PlanUnit(p._first(),
                                                                                                                 ObjectMapperFactory.getNewStandardObjectMapperWithPureProtocolExtensionSupports().readValue(PlanGenerator.serializeToJSON(nPlan, PureClientVersions.production, pureModel, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), this.transformers), ExecutionPlan.class),
                                                                                                                 core_pure_executionPlan_executionPlan_print.Root_meta_pure_executionPlan_toString_planToString_ExecutionPlan_1__Boolean_1__Extension_MANY__String_1_(nPlan, true, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), pureModel.getExecutionSupport())
                                                                                 );
                                                                             }
                                                                             catch (JsonProcessingException e)
                                                                             {
                                                                                 throw new RuntimeException(e);
                                                                             }
                                                                         }
                );
                return Response.ok(new PlansResult(plans)).build();
            }
        }
        catch (Exception ex)
        {
            return ExceptionTool.exceptionManager(ex, LoggingEventType.EXECUTE_INTERACTIVE_ERROR, profiles);
        }
    }

    @POST
    @ApiOperation(value = "Execute a GraphQL query in the context of a Mapping and a Runtime.")
    @Path("execute/prod/{groupId}/{artifact}/{version}/query/{queryClassPath}/mapping/{mappingPath}/runtime/{runtimePath}")
    @Consumes({MediaType.APPLICATION_JSON, APPLICATION_ZLIB})
    public Response executeProd(@Context HttpServletRequest request, @PathParam("branch") String branch, @PathParam("projectId") String projectId, Query query, @ApiParam(hidden = true) @Pac4JProfileManager ProfileManager<CommonProfile> pm)
    {
        throw new RuntimeException("Not implemented yet");
    }

    @POST
    @ApiOperation(value = "Execute a GraphQL query in the context of an Interactive Application.")
    @Path("execute/dev/{projectId}/{branch}/{interactiveApplication}")
    @Consumes({MediaType.APPLICATION_JSON, APPLICATION_ZLIB})
    public Response executeDev(@Context HttpServletRequest request, @PathParam("projectId") String projectId, @PathParam("branch") String branch, @PathParam("interactiveApplication") String interactiveApplicationName, Query query, @ApiParam(hidden = true) @Pac4JProfileManager ProfileManager<CommonProfile> pm)
    {
        MutableList<CommonProfile> profiles = ProfileManagerHelper.extractProfiles(pm);
        try (Scope scope = GlobalTracer.get().buildSpan("GraphQL: Interactive Application: Execute").startActive(true))
        {
            PureModel pureModel = loadModel(profiles, request, projectId, branch);
            Root_meta_pure_crud_metamodel_InteractiveApplication interactiveApplication = (Root_meta_pure_crud_metamodel_InteractiveApplication) pureModel.getPackageableElement(interactiveApplicationName);
            org.finos.legend.pure.generated.Root_meta_pure_crud_metamodel_ResolvedInteractiveApplication resolvedInteractiveApplication =
                    org.finos.legend.pure.generated.core_interactive_application_interactive_application_functions.Root_meta_pure_crud_functions_resolveInteractiveApplication_InteractiveApplication_1__ResolvedInteractiveApplication_1_(interactiveApplication, pureModel.getExecutionSupport());
            return this.executeDev(
                    pureModel,
                    query,
                    () -> resolvedInteractiveApplication._queryClass(),
                    () -> resolvedInteractiveApplication._mapping(),
                    () -> resolvedInteractiveApplication._runtime(),
                    pm);
        }
        catch (Exception ex)
        {
            return Response.ok(new GraphQLErrorMain(ex.getMessage())).build();
        }
    }

    @POST
    @ApiOperation(value = "Execute a GraphQL query in the context of a Mapping and a Runtime.")
    @Path("execute/dev/{projectId}/{branch}/query/{queryClassPath}/mapping/{mappingPath}/runtime/{runtimePath}")
    @Consumes({MediaType.APPLICATION_JSON, APPLICATION_ZLIB})
    public Response executeDev(@Context HttpServletRequest request, @PathParam("projectId") String projectId, @PathParam("branch") String branch, @PathParam("queryClassPath") String queryClassPath, @PathParam("mappingPath") String mappingPath, @PathParam("runtimePath") String runtimePath, Query query, @ApiParam(hidden = true) @Pac4JProfileManager ProfileManager<CommonProfile> pm)
    {
        MutableList<CommonProfile> profiles = ProfileManagerHelper.extractProfiles(pm);
        try (Scope scope = GlobalTracer.get().buildSpan("GraphQL: Execute").startActive(true))
        {
            PureModel pureModel = loadModel(profiles, request, projectId, branch);
            return this.executeDev(
                    pureModel,
                    query,
                    () -> pureModel.getClass(queryClassPath),
                    () -> pureModel.getMapping(mappingPath),
                    () -> pureModel.getRuntime(runtimePath),
                    pm);
        }
        catch (Exception ex)
        {
            return Response.ok(new GraphQLErrorMain(ex.getMessage())).build();
        }
    }

    private Response executeDev(
            PureModel pureModel,
            Query query,
            Function0<org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?>> classFunction0,
            Function0<Mapping> mappingFunction0,
            Function0<org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime> runtimeFunction0,
            ProfileManager<CommonProfile> profileManager)
    {
        org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?> _class = classFunction0.value();

        Document document = GraphQLGrammarParser.newInstance().parseDocument(query.query);
        org.finos.legend.pure.generated.Root_meta_external_query_graphQL_metamodel_Document queryDoc = toPureModel(document, pureModel);

        OperationType operationType = determineOperationType(document);
        Mapping mapping = mappingFunction0.value();
        org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime = runtimeFunction0.value();

        if (operationType == OperationType.subscription)
        {
            return this.executeSubscription(_class, pureModel, mapping, runtime, queryDoc, profileManager);
        }
        else if (operationType == OperationType.mutation)
        {
            return this.executeMutation(_class, pureModel, mapping, runtime, queryDoc, profileManager);
        }
        else if (operationType == OperationType.query)
        {
            if (isQueryIntrospection(findOperationForType(document, OperationType.query)))
            {
                return Response.ok("{" +
                                           "  \"data\":" + core_external_query_graphql_introspection_transformation.Root_meta_external_query_graphQL_introspection_graphQLIntrospectionQuery_Class_1__Document_1__String_1_(_class, queryDoc, pureModel.getExecutionSupport()) +
                                           "}").type(MediaType.TEXT_HTML_TYPE).build();
            }

            return this.executeQuery(_class, pureModel, mapping, runtime, queryDoc);
        }
        else
        {
            return Response.status(Response.Status.BAD_REQUEST).entity("unknown operation type").type("text/plain").build();
        }
    }

    private Response executeMutation(
            org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?> _class,
            PureModel pureModel,
            Mapping mapping,
            org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime,
            org.finos.legend.pure.generated.Root_meta_external_query_graphQL_metamodel_Document queryDoc,
            ProfileManager<CommonProfile> profileManager)
    {
        Pair<? extends AbstractProperty<? extends Object>, ? extends PureMap> propertyAndArguments = core_external_query_graphql_transformation.Root_meta_external_query_graphQL_transformation_mutationToPure_graphQLDocumentToPure_Class_1__Document_1__Pair_MANY_(_class, queryDoc, pureModel.getExecutionSupport()).getOnly();
        QualifiedProperty queryProperty = (QualifiedProperty) propertyAndArguments._first();
        PureMap argumentsByName = propertyAndArguments._second();

        //TODO: AJH: do this better - determining which argument is the payload (or no payload for delete)
        RichIterable<? extends VariableExpression> qualifiedPropertyArguments = ((FunctionType) queryProperty._classifierGenericType()._typeArguments().getOnly()._rawType())._parameters();
        VariableExpression payloadArgument = qualifiedPropertyArguments.detect(qpa -> qpa._genericType()._rawType().equals(queryProperty._genericType()._rawType()));
        Root_meta_json_JSONElement newInstance = payloadArgument == null ? null : (Root_meta_json_JSONElement) argumentsByName.getMap().get(payloadArgument._name());

        MutableList<CommonProfile> profiles = ProfileManagerHelper.extractProfiles(profileManager);

        AlloyWrite alloyWrite = instantiateAlloyWriteInstance(runtime);
        Root_meta_json_JSONElement persistedInstance = alloyWrite.persist(pureModel, mapping, runtime, profiles, queryProperty, argumentsByName, newInstance);

        return Response.ok(
                (StreamingOutput) outputStream ->
                {
                    try (JsonGenerator generator = new JsonFactory().createGenerator(outputStream)
                            .disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)
                            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);)
                    {
                        generator.writeStartObject();
                        generator.setCodec(new ObjectMapper());
                        generator.writeFieldName("data");

                        writePureJson(queryProperty._genericType()._rawType(), persistedInstance, generator);

                        generator.writeEndObject();
                    }
                }).build();
    }

    private static void writePureJson(org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Type pureType, Root_meta_json_JSONElement jsonElement, JsonGenerator jsonGenerator) throws IOException
    {
        if (Root_meta_json_JSONBoolean.class.isAssignableFrom(jsonElement.getClass()))
        {
            jsonGenerator.writeBoolean(((Root_meta_json_JSONBoolean) jsonElement)._value());
        }
        else if (Root_meta_json_JSONString.class.isAssignableFrom(jsonElement.getClass()))
        {
            jsonGenerator.writeString(((Root_meta_json_JSONString) jsonElement)._value());
        }
        else if (Root_meta_json_JSONNumber.class.isAssignableFrom(jsonElement.getClass()))
        {
            Number number = ((Root_meta_json_JSONNumber) jsonElement)._value();
            switch (pureType._name())
            {
                case "Integer":
                    jsonGenerator.writeNumber(number.longValue());
                    break;
                case "Float":
                    jsonGenerator.writeNumber(number.doubleValue());
                    break;
                default:
                    throw new UnsupportedOperationException("Unknown primitive number type: " + pureType._name());
            }
        }
        else if (Root_meta_json_JSONNull.class.isAssignableFrom(jsonElement.getClass()))
        {
            jsonGenerator.writeNull();
        }
        else if (Root_meta_json_JSONArray.class.isAssignableFrom(jsonElement.getClass()))
        {
            jsonGenerator.writeStartArray();
            for (Root_meta_json_JSONElement je : ((Root_meta_json_JSONArray) jsonElement)._values())
            {
                writePureJson(pureType, je, jsonGenerator);
            }
            jsonGenerator.writeEndArray();
        }
        else if (Root_meta_json_JSONObject.class.isAssignableFrom(jsonElement.getClass()))
        {
            jsonGenerator.writeStartObject();
            Root_meta_json_JSONObject jsonObject = (Root_meta_json_JSONObject) jsonElement;
            MapIterable<Root_meta_json_JSONString, ? extends Root_meta_json_JSONKeyValue> jsonValues = jsonObject._keyValuePairs().groupByUniqueKey(Root_meta_json_JSONKeyValue::_key);
            MapIterable<String, Property> propertyPureTypes = ((Class) pureType)._properties().groupByUniqueKey(p -> ((Property) p)._name());

            for (Root_meta_json_JSONKeyValue jsonKeyValue : jsonObject._keyValuePairs())
            {
                jsonGenerator.writeFieldName(jsonKeyValue._key()._value());
                writePureJson(propertyPureTypes.get(jsonKeyValue._key()._value())._genericType()._rawType(), jsonKeyValue._value(), jsonGenerator);
            }

            jsonGenerator.writeEndObject();
        }
        else
        {
            throw new UnsupportedOperationException("Unknown JSONElement: " + jsonElement.getClass());
        }
    }

    private AlloyWrite instantiateAlloyWriteInstance(org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime)
    {
        //TODO: AJH: make this better - push to Store implementation?
        java.lang.Class<? extends Connection> connectionClass = runtime._connections().getOnly().getClass();
        if (Root_meta_pure_alloy_connections_RelationalDatabaseConnection.class.isAssignableFrom(connectionClass))
        {
            return new RelationalAlloyWrite(this.planExecutor);
        }

        throw new IllegalStateException("Unsupported store connection: " + connectionClass.getName());
    }

    private Response executeSubscription(
            org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?> _class,
            PureModel pureModel,
            Mapping mapping,
            org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime,
            org.finos.legend.pure.generated.Root_meta_external_query_graphQL_metamodel_Document queryDoc,
            ProfileManager<CommonProfile> profileManager)
    {
        return Response.status(Response.Status.BAD_REQUEST).entity("not yet implemented").type("text/plain").build();
    }

    private Response executeQuery(
            org.finos.legend.pure.m3.coreinstance.meta.pure.metamodel.type.Class<?> _class,
            PureModel pureModel,
            Mapping mapping,
            org.finos.legend.pure.m3.coreinstance.meta.pure.runtime.Runtime runtime,
            org.finos.legend.pure.generated.Root_meta_external_query_graphQL_metamodel_Document queryDoc)
    {
        RichIterable<? extends Pair<? extends String, ? extends Root_meta_pure_executionPlan_ExecutionPlan>> purePlans = core_external_query_graphql_transformation.Root_meta_external_query_graphQL_transformation_queryToPure_getPlansFromGraphQL_Class_1__Mapping_1__Runtime_1__Document_1__Extension_MANY__Pair_MANY_(_class, mapping, runtime, queryDoc, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), pureModel.getExecutionSupport());
        Collection<org.eclipse.collections.api.tuple.Pair<String, SingleExecutionPlan>> plans = Iterate.collect(purePlans, p ->
                                                                                                                {
                                                                                                                    Root_meta_pure_executionPlan_ExecutionPlan nPlan = PlanPlatform.JAVA.bindPlan(p._second(), "ID", pureModel, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()));
                                                                                                                    return Tuples.pair(p._first(), PlanGenerator.stringToPlan(PlanGenerator.serializeToJSON(nPlan, PureClientVersions.production, pureModel, Root_meta_relational_extension_relationalExtensions__Extension_MANY_(pureModel.getExecutionSupport()), this.transformers)));
                                                                                                                }
        );

        return Response.ok(
                (StreamingOutput) outputStream ->
                {
                    try (JsonGenerator generator = new JsonFactory().createGenerator(outputStream)
                            .disable(JsonGenerator.Feature.AUTO_CLOSE_JSON_CONTENT)
                            .enable(JsonGenerator.Feature.WRITE_BIGDECIMAL_AS_PLAIN);)
                    {
                        generator.writeStartObject();
                        generator.setCodec(new ObjectMapper());
                        generator.writeFieldName("data");
                        generator.writeStartObject();

                        plans.forEach(p ->
                                      {
                                          JsonStreamingResult result = null;
                                          try
                                          {
                                              generator.writeFieldName(p.getOne());
                                              result = (JsonStreamingResult) planExecutor.execute(p.getTwo());
                                              result.getJsonStream().accept(generator);
                                          }
                                          catch (IOException e)
                                          {
                                              throw new RuntimeException(e);
                                          }
                                          finally
                                          {
                                              result.close();
                                          }
                                      });
                        generator.writeEndObject();
                        generator.writeEndObject();
                    }
                }).build();
    }

    private static boolean isQueryIntrospection(OperationDefinition operationDefinition)
    {
        List<Selection> selections = operationDefinition.selectionSet;
        return !selections.isEmpty() && selections.get(0) instanceof Field && ((Field) selections.get(0)).name.equals("__schema");
    }

    private static OperationType determineOperationType(Document document)
    {
        ListIterable<OperationType> operationTypes = extractOperations(document).collect(op -> op.type);

        if (operationTypes.isEmpty())
        {
            throw new RuntimeException("Please provide an operation");
        }
        else if (operationTypes.size() > 1)
        {
            throw new RuntimeException("Found more than one operations");
        }
        else
        {
            return operationTypes.getFirst();
        }
    }

    private static OperationDefinition findOperationForType(Document document, OperationType operationType)
    {
        ListIterable<OperationDefinition> res = extractOperations(document).select(op -> op.type == operationType);

        if (res.isEmpty())
        {
            throw new RuntimeException("Please provide a '" + operationType + "'");
        }
        else if (res.size() > 1)
        {
            throw new RuntimeException("Found more than one '" + operationType + "'");
        }
        else
        {
            return res.getFirst();
        }
    }

    private static ListIterable<OperationDefinition> extractOperations(Document document)
    {
        return ListAdapter.adapt(document.definitions)
                .select(d -> d.accept(new DefinitionVisitor<Boolean>()
                                      {

                                          @Override
                                          public Boolean visit(DirectiveDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(EnumTypeDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(ExecutableDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(FragmentDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(InterfaceTypeDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(ObjectTypeDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(OperationDefinition val)
                                          {
                                              return true;
                                          }

                                          @Override
                                          public Boolean visit(ScalarTypeDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(SchemaDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(Type val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(TypeSystemDefinition val)
                                          {
                                              return false;
                                          }

                                          @Override
                                          public Boolean visit(UnionTypeDefinition val)
                                          {
                                              return false;
                                          }
                                      }
                )).collect(d -> (OperationDefinition) d);
    }
}
