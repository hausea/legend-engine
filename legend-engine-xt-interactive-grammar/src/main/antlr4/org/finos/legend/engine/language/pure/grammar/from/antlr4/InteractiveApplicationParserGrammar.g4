parser grammar InteractiveApplicationParserGrammar;

import M3ParserGrammar;

options
{
    tokenVocab = InteractiveApplicationLexerGrammar;
}

// -------------------------------------- IDENTIFIER --------------------------------------

identifier:                                 VALID_STRING | STRING
;

// -------------------------------------- DEFINITION --------------------------------------

definition:                                 imports
                                                (application)*
                                            EOF
;
imports:                                    (importStatement)*
;
importStatement:                            IMPORT packagePath PATH_SEPARATOR STAR SEMI_COLON
;
application:                                APPLICATION PAREN_OPEN qualifiedName PAREN_CLOSE
                                                BRACE_OPEN
                                                    (
                                                        documentation
                                                        | store
                                                        | globalAuthorization
                                                        | rootType
                                                    )*
                                                BRACE_CLOSE
;
documentation:                              INTERACTIVE_DOC COLON STRING SEMI_COLON
;
store:                                      STORE COLON (relationalStore)
;
relationalStore:                            RELATIONAL_STORE
                                                BRACE_OPEN
                                                    (
                                                        relationalType
                                                        | relationalAuth
                                                        | relationalDatasourceSpec
                                                    )*
                                                BRACE_CLOSE
;
relationalType:                             RELATIONAL_TYPE COLON relationalTypeOption SEMI_COLON
;
relationalTypeOption:                       (RELATIONAL_TYPE_H2)
;





relationalAuth:                             RELATIONAL_AUTH_STRATEGY COLON specification SEMI_COLON
;
relationalDatasourceSpec:                   RELATIONAL_DATASOURCE_SPEC COLON specification SEMI_COLON
;
specification:                              specificationType (specificationValueBody)?
;
specificationType:                          VALID_STRING
;
specificationValueBody:                     ISLAND_OPEN (specificationValue)*
;
specificationValue:                         ISLAND_START | ISLAND_BRACE_OPEN | ISLAND_CONTENT | ISLAND_HASH | ISLAND_BRACE_CLOSE | ISLAND_END
;




globalAuthorization:                        GLOBAL_AUTHORIZATION COLON BRACKET_OPEN (identifier|NONE) (COMMA identifier)* BRACKET_CLOSE SEMI_COLON
;
rootType:                                   ROOT_TYPE PAREN_OPEN qualifiedName PAREN_CLOSE
                                                BRACE_OPEN
                                                    (
                                                        graphScope |
                                                        primaryKey |
                                                        stringLength |
                                                        service
                                                    )*
                                                BRACE_CLOSE
;
graphScope:                                 GRAPH_SCOPE COLON PAREN_OPEN (identifier (COMMA identifier)*)? PAREN_CLOSE SEMI_COLON
;
primaryKey:                                 PRIMARY_KEY COLON identifier ARROW primaryKeyStrategy SEMI_COLON
;
primaryKeyStrategy:                         (PRIMARY_KEY_STRATEGY_NONE | PRIMARY_KEY_STRATEGY_MAX | PRIMARY_KEY_STRATEGY_SYNTHETIC)
;
stringLength:                               STRING_LENGTH COLON identifier ARROW stringLengthValue SEMI_COLON
;
stringLengthValue:                          INTEGER
;
service:                                    readService | createService | updateService | upsertService | deleteService
;
readService:                                READ_SERVICE serviceDescription
                                                BRACE_OPEN
                                                    (
                                                        authorization |
                                                        query
                                                    )*
                                                BRACE_CLOSE
;
createService:                              CREATE_SERVICE serviceDescription
                                                BRACE_OPEN
                                                    (
                                                        authorization |
                                                        query
                                                    )*
                                                BRACE_CLOSE
;
updateService:                              UPDATE_SERVICE serviceDescription
                                                BRACE_OPEN
                                                    (
                                                        authorization |
                                                        query
                                                    )*
                                                BRACE_CLOSE
;
upsertService:                              UPSERT_SERVICE serviceDescription
                                                BRACE_OPEN
                                                    (
                                                        authorization |
                                                        query
                                                    )*
                                                BRACE_CLOSE
;
deleteService:                              DELETE_SERVICE serviceDescription
                                                BRACE_OPEN
                                                    (
                                                        authorization |
                                                        query
                                                    )*
                                                BRACE_CLOSE
;
serviceDescription:                         PAREN_OPEN (lambdaParam (COMMA lambdaParam)* ARROW)? identifier PAREN_CLOSE
;
authorization:                              AUTHORIZATION COLON BRACKET_OPEN (identifier|NONE) (COMMA identifier)* BRACKET_CLOSE SEMI_COLON
;
query:                                      QUERY COLON lambdaFunction SEMI_COLON
;
