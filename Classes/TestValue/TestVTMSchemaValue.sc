TestVTMSchemaValue : TestVTMDictionaryValue {

	*generateRandomValue{arg params;
		^super.generateRandomValue(params);
	}

	test_DefaultProperties{
		var testValue, testSchemas;
		var valueObj = VTMSchemaValue.new;
		//value should be be nil
		// this.assertEquals(
		// 	valueObj.value, Dictionary.new,
		// 	"SchemaValue value is an empty Dictionary by default"
		// );

		//defaultValue should be nil
		// this.assertEquals(
		// 	valueObj.defaultValue, Dictionary.new,
		// 	"SchemaValue defaultValue is an empty Dictionary by default"
		// );

		//Properites are an empty Dictionary by default.
		// this.assertEquals(
		// 	valueObj.schema, nil,
		// 	"SchemaValue schema is nil by default"
		// );
	}
	//Annotations in a subschema contained within a "not", at any depth, including any number of intervening additional "not" subschemas, MUST be ignored.

	//Similarly, annotations within a failing branch of a "oneOf", "anyOf", "then", or "else" MUST be ignored.

	//Annotation keywords MUST be applied to all possible sub-instances. Even if such application can be short-circuited when only assertion evaluation is needed. For instance, the "contains" keyword need only be checked for assertions until at least one array item proves valid. However, when working with annotations, all items in the array must be evaluated to determine all items with which the annotations should be associated.

	//Finally, implementations MUST NOT take regular expressions to be anchored, neither at the beginning nor at the end. This means, for instance, the pattern "es" matches "expression".


	//6.1.1 type - The value of this keyword MUST be either a string or an array.  If it is an array, elements of the array MUST be strings and MUST be unique. String values MUST be one of the six primitive types ("null", "boolean", "object", "array", "number", or "string"), or "integer" which matches any number with a zero fractional part. An instance validates if and only if the instance is in any of the sets listed for this keyword.

	//6.1.2 enum - The value of this keyword MUST be an array. This array SHOULD have at least one element. Elements in the array SHOULD be unique.  An instance validates successfully against this keyword if its value is equal to one of the elements in this keyword's array value.  Elements in the array might be of any value, including null.


	//6.1.3 - const - The value of this keyword MAY be of any type, including null.  An instance validates successfully against this keyword if its value is equal to the value of the keyword.


	//6.2.1. multipleOf The value of "multipleOf" MUST be a number, strictly greater than 0.

	//6.2.2. maximum The value of "maximum" MUST be a number, representing an inclusive upper limit for a numeric instance.  If the instance is a number, then this keyword validates only if the instance is less than or exactly equal to "maximum".

	//6.2.3. exclusiveMaximum The value of "exclusiveMaximum" MUST be number, representing an exclusive upper limit for a numeric instance.  If the instance is a number, then the instance is valid only if it has a value strictly less than (not equal to) "exclusiveMaximum".  


	//6.2.4. minimum The value of "minimum" MUST be a number, representing an inclusive lower limit for a numeric instance.  If the instance is a number, then this keyword validates only if the instance is greater than or exactly equal to "minimum".

	//6.2.5. exclusiveMinimum The value of "exclusiveMinimum" MUST be number, representing an exclusive lower limit for a numeric instance.  If the instance is a number, then the instance is valid only if it has a value strictly greater than (not equal to) "exclusiveMinimum"


	//6.3.1. maxLength The value of this keyword MUST be a non-negative integer.  A string instance is valid against this keyword if its length is less than, or equal to, the value of this keyword.

	//6.3.2. minLength The value of this keyword MUST be a non-negative integer.

	//6.3.3. pattern The value of this keyword MUST be a string. This string SHOULD be a valid regular expression, according to the ECMA 262 regular expression dialect.  


	//6.4. Validation Keywords for Arrays
	//6.4.1. items The value of "items" MUST be either a valid JSON Schema or an array of valid JSON Schemas.  This keyword determines how child instances validate for arrays, and does not directly validate the immediate instance itself.  If "items" is a schema, validation succeeds if all elements in the array successfully validate against that schema.  If "items" is an array of schemas, validation succeeds if each element of the instance validates against the schema at the same position, if any.  Omitting this keyword has the same behavior as an empty schema.

	//6.4.2. additionalItems The value of "additionalItems" MUST be a valid JSON Schema.  This keyword determines how child instances validate for arrays, and does not directly validate the immediate instance itself.  If "items" is an array of schemas, validation succeeds if every instance element at a position greater than the size of "items" validates against "additionalItems".  Otherwise, "additionalItems" MUST be ignored, as the "items" schema (possibly the default value of an empty schema) is applied to all elements.  Omitting this keyword has the same behavior as an empty schema.

	//6.4.3. maxItems The value of this keyword MUST be a non-negative integer.  An array instance is valid against "maxItems" if its size is less than, or equal to, the value of this keyword.

	//6.4.4. minItems The value of this keyword MUST be a non-negative integer.  An array instance is valid against "minItems" if its size is greater than, or equal to, the value of this keyword.  Omitting this keyword has the same behavior as a value of 0.

	//6.4.5. uniqueItems The value of this keyword MUST be a boolean.  If this keyword has boolean value false, the instance validates successfully. If it has boolean value true, the instance validates successfully if all of its elements are unique.  Omitting this keyword has the same behavior as a value of false.

	//6.4.6. contains The value of this keyword MUST be a valid JSON Schema.  An array instance is valid against "contains" if at least one of its elements is valid against the given schema.

	//6.5. Validation Keywords for Objects
	//6.5.1. maxProperties The value of this keyword MUST be a non-negative integer.  An object instance is valid against "maxProperties" if its number of properties is less than, or equal to, the value of this keyword.

//6.5.2. minProperties The value of this keyword MUST be a non-negative integer.  An object instance is valid against "minProperties" if its number of properties is greater than, or equal to, the value of this keyword.  Omitting this keyword has the same behavior as a value of 0.

//6.5.3. required The value of this keyword MUST be an array. Elements of this array, if any, MUST be strings, and MUST be unique.  An object instance is valid against this keyword if every item in the array is the name of a property in the instance.  Omitting this keyword has the same behavior as an empty array.

//6.5.4. properties The value of "properties" MUST be an object. Each value of this object MUST be a valid JSON Schema.  This keyword determines how child instances validate for objects, and does not directly validate the immediate instance itself.  Validation succeeds if, for each name that appears in both the instance and as a name within this keyword's value, the child instance for that name successfully validates against the corresponding schema.  Omitting this keyword has the same behavior as an empty object.

//6.5.5. patternProperties The value of "patternProperties" MUST be an object. Each property name of this object SHOULD be a valid regular expression, according to the ECMA 262 regular expression dialect. Each property value of this object MUST be a valid JSON Schema.  This keyword determines how child instances validate for objects, and does not directly validate the immediate instance itself. Validation of the primitive instance type against this keyword always succeeds.  Validation succeeds if, for each instance name that matches any regular expressions that appear as a property name in this keyword's value, the child instance for that name successfully validates against each schema that corresponds to a matching regular expression.  Omitting this keyword has the same behavior as an empty object.

//6.5.6. additionalProperties The value of "additionalProperties" MUST be a valid JSON Schema.  This keyword determines how child instances validate for objects, and does not directly validate the immediate instance itself.  Validation with "additionalProperties" applies only to the child values of instance names that do not match any names in "properties", and do not match any regular expression in "patternProperties".  For all such properties, validation succeeds if the child instance validates against the "additionalProperties" schema.  Omitting this keyword has the same behavior as an empty schema.

//6.5.7. dependencies [CREF1] This keyword specifies rules that are evaluated if the instance is an object and contains a certain property.  This keyword's value MUST be an object. Each property specifies a dependency. Each dependency value MUST be an array or a valid JSON Schema.  If the dependency value is a subschema, and the dependency key is a property in the instance, the entire instance must validate against the dependency value.  If the dependency value is an array, each element in the array, if any, MUST be a string, and MUST be unique. If the dependency key is a property in the instance, each of the items in the dependency value must be a property that exists in the instance.  Omitting this keyword has the same behavior as an empty object.

//6.5.8. propertyNames The value of "propertyNames" MUST be a valid JSON Schema.  If the instance is an object, this keyword validates if every property name in the instance validates against the provided schema. Note the property name that the schema is testing will always be a string.  Omitting this keyword has the same behavior as an empty schema.

//6.6. Keywords for Applying Subschemas Conditionally These keywords work together to implement conditional application of a subschema based on the outcome of another subschema.  These keywords MUST NOT interact with each other across subschema boundaries. In other words, an "if" in one branch of an "allOf" MUST NOT have an impact on a "then" or "else" in another branch.

//6.6.1. if This keyword's value MUST be a valid JSON Schema.  Instances that successfully validate against this keyword's subschema MUST also be valid against the subschema value of the "then" keyword, if present.  Instances that fail to validate against this keyword's subschema MUST also be valid against the subschema value of the "else" keyword.  Validation of the instance against this keyword on its own always succeeds, regardless of the validation outcome of against its subschema.

//6.6.2. then This keyword's value MUST be a valid JSON Schema.  When present alongside of "if", the instance successfully validates against this keyword if it validates against both the "if"'s subschema and this keyword's subschema.  When "if" is absent, or the instance fails to validate against its subschema, validation against this keyword always succeeds. Implementations SHOULD avoid attempting to validate against the subschema in these cases.

//6.6.3. else This keyword's value MUST be a valid JSON Schema.  When present alongside of "if", the instance successfully validates against this keyword if it fails to validate against the "if"'s subschema, and successfully validates against this keyword's subschema.  When "if" is absent, or the instance successfully validates against its subschema, validation against this keyword always succeeds. Implementations SHOULD avoid attempting to validate against the subschema in these cases.  

//6.7. Keywords for Applying Subschemas With Boolean Logic

//6.7.1. allOf This keyword's value MUST be a non-empty array. Each item of the array MUST be a valid JSON Schema.  An instance validates successfully against this keyword if it validates successfully against all schemas defined by this keyword's value.

//6.7.2. anyOf This keyword's value MUST be a non-empty array. Each item of the array MUST be a valid JSON Schema.  An instance validates successfully against this keyword if it validates successfully against at least one schema defined by this keyword's value.

//6.7.3. oneOf This keyword's value MUST be a non-empty array. Each item of the array MUST be a valid JSON Schema.  An instance validates successfully against this keyword if it validates successfully against exactly one schema defined by this keyword's value.

//6.7.4. not This keyword's value MUST be a valid JSON Schema.  An instance is valid against this keyword if it fails to validate successfully against the schema defined by this keyword.
}


