package io.openapiprocessor.core.processor.mapping.v2

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory
import com.networknt.schema.JsonSchemaFactory
import com.networknt.schema.SpecVersion
import io.kotest.core.spec.style.StringSpec
import java.io.InputStream
import io.kotest.matchers.collections.shouldBeEmpty


class ValidateSpec: StringSpec({

    fun getResource(path: String): InputStream {
        return this.javaClass.getResourceAsStream(path)
    }

    "validate" {
        val yaml = ObjectMapper(YAMLFactory ())
        val yamlData = getResource("/mapping/v2/mapping.example.yaml")
        val node = yaml.readTree(yamlData)

        val factory = JsonSchemaFactory.getInstance(SpecVersion.VersionFlag.V7)
        val schemaData = getResource("/mapping/v2/mapping.yaml.json")
        val schema = factory.getSchema(schemaData)

        val errors = schema.validate(node)

        errors.shouldBeEmpty()
    }

})
