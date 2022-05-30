package org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElement;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.PackageableElementVisitor;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.authorization.InteractiveAuthorization;
import org.finos.legend.engine.protocol.pure.v1.model.packageableElement.crud.store.InteractiveApplicationStore;

import java.util.Collections;

public class InteractiveApplication extends PackageableElement
{
    public String documentation;
    public InteractiveApplicationStore store;
    public InteractiveAuthorization globalAuthorization;
    public java.util.List<InteractiveType> types = Collections.emptyList();

    @Override
    public <T> T accept(PackageableElementVisitor<T> visitor)
    {
        return visitor.visit(this);
    }
}


/*
###Pure
import meta::legend::crud::functions::*;
import meta::legend::crud::metamodel::*;
import meta::legend::relational::crud::metamodel::*;

import meta::pure::graphFetch::*;

import com::hausea::crud::*;

function com::hausea::crud::buildApp(): InteractiveApplication[1]
{
  ^InteractiveApplication(
    package = 'com::hausea::crud',
    name = 'demoApp',
    store = meta::legend::relational::crud::functions::defaultRelationalStore({
      store: meta::pure::store::Store[1]|
        ^meta::relational::runtime::TestDatabaseConnection(
          element = $store,
          type = meta::relational::runtime::DatabaseType.H2
        )
      }
    ),

    globalAuthorization = [],
    types = [
      ^InteractiveType(
        baseClass = Firm,
        graphScope = ^RootGraphFetchTree<Firm>(
          class = Firm
        ),
        configuration = [
          ^InteractiveTypePrimaryKeysConfiguration(
            primaryKeys = [
              ^InteractiveTypePrimaryKeysPrimaryKeyConfiguration(
                property = Firm->propertyByName('id')->toOne()->cast(@Property<Nil, Any|1>),
                strategy = PrimaryKeyStrategy.NONE
              )
            ]
          ),
          ^InteractiveTypeStringPropertyConfiguration(
            property = Firm->propertyByName('name')->toOne()->cast(@Property<Nil, Any|*>),
            maxLength = 256
          )
        ],
        services = [
          ^InteractiveService(
            name = 'allFirms',
            authorization = [],
            query = |Firm.all()
          ),
          ^InteractiveService(
            name = 'firmById',
            authorization = [],
            query = id: Integer[1]|Firm.all()->filter(f | $f.id == $id)
          )
        ]
      )
    ]
  );
}

Class com::hausea::crud::Firm
{
  id: Integer[1];
  name: String[0..1];
}





Class up::application::name::Query
{
    allFirms()
    {
        Firm.all()
    }: Firm[*];

    firmById(id: Integer[1])
    {
        Firm.all()->filter(f|$f.id == $id)
    {: Firm[0..1];

    createFirm(firm: Firm[1])
    {
        firm.insert()
    }: Firm[1];

    deleteFirm(firm: Firm[1])
    {
        firm.delete()
    }: Nil[0..1];

    updateFirm(firm: Firm[1])
    {
        firm.materialize()
    }: Firm[1];
}


function up::function()
{
    let f = ^Firm(id: 5, name: 'name');
    let f2 = ^Firm($f);
    $f2.insert();

}

 */