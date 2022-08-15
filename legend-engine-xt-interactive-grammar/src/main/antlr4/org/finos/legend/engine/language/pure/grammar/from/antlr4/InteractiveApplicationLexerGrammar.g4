lexer grammar InteractiveApplicationLexerGrammar;

import M3LexerGrammar;

// -------------------------------------- KEYWORD --------------------------------------

// COMMON
ARROW:                                      '->';
IMPORT:                                     'import';
NONE:                                       'None';

//**********
// INTERACTIVE APPLICATION
//**********

APPLICATION:                                'Application';
INTERACTIVE_DOC:                            'doc';
STORE:                                      'store';
GLOBAL_AUTHORIZATION:                       'globalAuthorization';

// STORE
RELATIONAL_STORE:                           'Relational';
IN_MEMORY_STORE:                            'InMemory';
RELATIONAL_TYPE:                            'type';
RELATIONAL_TYPE_H2:                         'H2';

RELATIONAL_DATASOURCE_SPEC:                 'specification';
RELATIONAL_AUTH_STRATEGY:                   'auth';

QUOTE_IDENTIFIERS:                          'quoteIdentifiers';

// TYPES
ROOT_TYPE:                                  'RootType';
GRAPH_SCOPE:                                'graphScope';

// TYPE CONFIGURATION
PRIMARY_KEY:                                'primaryKey';
PRIMARY_KEY_STRATEGY_NONE:                  'NONE';
PRIMARY_KEY_STRATEGY_MAX:                   'MAX';
PRIMARY_KEY_STRATEGY_SYNTHETIC:             'SYNTHETIC';
STRING_LENGTH:                              'stringLength';

// SERVICES
READ_SERVICE:                               'ReadService';
CREATE_SERVICE:                             'CreateService';
UPDATE_SERVICE:                             'UpdateService';
UPSERT_SERVICE:                             'UpsertService';
DELETE_SERVICE:                             'DeleteService';
AUTHORIZATION:                              'authorization';
QUERY:                                      'query';
