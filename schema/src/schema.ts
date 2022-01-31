/**
 * flatten & merge allOf
 */

import $RefParser, {JSONSchema} from '@apidevtools/json-schema-ref-parser';
import merge from 'json-schema-merge-allof';
import * as fs from 'fs';


async function parse(schema: string): Promise<JSONSchema> {
  try {
    return await $RefParser.parse(schema);
  } catch (e) {
    return e;
  }
}

async function flatten(schema: string): Promise<JSONSchema> {
  try {
    return await $RefParser.dereference(schema);
  } catch (e) {
    return e;
  }
}

async function mergeAllOf(schema: JSONSchema): Promise<JSONSchema> {
  try {
    return await merge(schema,{
          ignoreAdditionalProperties: true
        });
  } catch (e) {
    return e;
  }
}

function print(schema: JSONSchema) {
  console.log(JSON.stringify(schema, null, 2));
}

function write(schema: JSONSchema) {
  fs.writeFileSync("../src/main/resources/mapping/v2/mapping.flat.yaml.json",
    JSON.stringify(schema, null, 2) + "\n");
}

async function run(): Promise<JSONSchema> {
  let expanded = await flatten("../src/main/resources/mapping/v2/mapping.yaml.json");
  return await mergeAllOf(expanded);
}

run().then((schema: JSONSchema) => {
  write(schema);
})
