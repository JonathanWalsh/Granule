// Copyright 2009 The Closure Library Authors. All Rights Reserved.
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS-IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

/**
 * @fileoverview Common test functions for tweak unit tests.
 *
 * @author agrieve@google.com (Andrew Grieve)
 */

goog.provide('goog.tweak.testhelpers');

goog.require('goog.tweak');

var boolEntry;
var boolEntry2;
var strEntry;
var strEntry2;
var strEnumEntry;
var numEntry;
var numEnumEntry;
var boolGroup;
var boolOneEntry;
var boolTwoEntry;
var buttonEntry;


/**
 * Creates a registry with some entries in it.
 * @param {string} queryParams The query parameter string to use for the
 *     registry.
 */
function createRegistryEntries(queryParams) {
  // Initialize the registry with the given query string.
  var registry = new goog.tweak.Registry(queryParams);
  goog.tweak.registry_ = registry;

  goog.tweak.registerBoolean('Bool', 'The bool');
  goog.tweak.registerBoolean('Bool2', 'The bool2', true);

  goog.tweak.registerString('Str', 'The Str', undefined, {
    paramName: 's'
  });
  goog.tweak.registerString('Str2', 'The str2', 'foo');
  goog.tweak.registerString('Enum', 'The Enum', undefined, {
    validValues: ['A', 'B', 'C'],
    restartRequired: false
  });

  goog.tweak.registerNumber('Num', 'The Num', 99);
  goog.tweak.registerNumber('Enum2', 'The enum2', undefined, {
    validValues: [1, 2, 3],
    label: 'Enum the second&'
  });

  goog.tweak.beginBooleanGroup('BoolGroup', 'The Bool Group');
  goog.tweak.registerBoolean('BoolOne', 'Desc for 1', false, {
    token: 'B1',
    restartRequired: false
  });
  goog.tweak.registerBoolean('BoolTwo', 'Desc for 2', true);
  goog.tweak.endBooleanGroup();

  goog.tweak.registerButton('Button', 'The Btn', goog.nullFunction, '<btn>');

  boolEntry = registry.getEntry('Bool');
  boolEntry2 = registry.getEntry('Bool2');
  strEntry = registry.getEntry('Str');
  strEntry2 = registry.getEntry('Str2');
  strEnumEntry = registry.getEntry('Enum');
  numEntry = registry.getEntry('Num');
  numEnumEntry = registry.getEntry('Enum2');
  boolGroup = registry.getEntry('BoolGroup');
  boolOneEntry = registry.getEntry('BoolOne');
  boolTwoEntry = registry.getEntry('BoolTwo');
  buttonEntry = registry.getEntry('Button');
}


