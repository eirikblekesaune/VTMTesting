TestVTMValue : VTMUnitTest {
	*classesForTesting{
		^[
			//VTMNoneValue,
			VTMDecimalValue,
			VTMIntegerValue,
			// VTMTimecodeValue,
			// VTMListValue,
			// VTMDictionaryValue,
			// VTMArrayValue,
			// VTMSchemaValue,
			// VTMTupleValue
		];
	}

	*testclassForType{arg val;
		^"TestVTM%Value".format(val.asString.capitalize).asSymbol.asClass;
	}

	*testTypes{
		^this.classesForTesting.collect(_.type);
	}


	//params args expects a dictionary where the
	//keys are the property keys, and the values being
	//parameters for the random generator methods.
	//e.g. ( value: (minVal: 0, maxVal: 10) ); generates a value between 0 to 10
	//if no random parameters are to given, an empty divt should be used as value:
	// (value: (), defaultValue: () ); makes random properties for given keys.
	*generateRandomProperties{arg params;
		var testClass, class, propKeys, result;
		var type;
		result = VTMValueProperties.new;
		if(this == TestVTMValue, {
			class = TestVTMValue.classesForTesting.choose;
		}, {
			class = this.findTestedClass;
		});

		type = class.type;

		testClass = this.findTestClass(class);
		//"Class: %\ntype: %\ntestClass: %".format(class, type, testClass).debug;

		if(params.notNil and: {params.isKindOf(Dictionary)}, {
			propKeys = params.keys;
		}, {
			propKeys = class.propertyKeys;
		});

		result.put(\type, type);

		propKeys.do({arg propKey;
			var propParams;

			if(params.notNil and: {params.includesKey(propKey);}, {
				propParams = params[propKey];
			});
			result.put(
				propKey,
				testClass.generateRandomProperty(propKey, propParams)
			);
		});
		^result;
	}

	*generateRandomObject{arg params;
		var props = this.generateRandomProperties( params );
		^VTMValue.makeFromProperties(props);
	}

	*generateRandomValue{arg params;
		^this.subclassResponsibility(thisMethod);
	}

	*generateRandomProperty{arg key, params;
		var result;
		switch(key,
			\enabled, {result = this.generateRandomBoolean(params)},
			\filterRepetitions, { result = this.generateRandomBoolean(params); },
			\value, { result = this.generateRandomValue(params); },
			\defaultValue, { result = this.generateRandomValue(params); },
			\enum, { result = this.generateRandomEnum(params); },
			\restrictValueToEnum, { result = this.generateRandomBoolean(params); }
		);
		^result;
	}

	*generateRandomEnum{arg params;
		var minRand = 5, maxRand = 10;
		^rrand(minRand, maxRand).collect({arg i;
			[
				[i, this.generateRandomSymbol((noNumbers: true, noSpaces: true))].choose,
				this.generateRandomValue
			];
		}).flatten;
	}

	test_AreAllPropertiesSettersAndGettersImplemented{
		this.class.classesForTesting.do({arg class;
			var obj = class.new;
			var testClass = this.class.findTestClass(class);
			obj.class.propertyKeys.do({arg propertyKey;
				var testVal;
				this.assert(obj.respondsTo(propertyKey),
					"[%] - responds to properties getter '%'".format(class, propertyKey)
				);
				this.assert(obj.respondsTo(propertyKey.asSetter),
					"[%] - responds to properties setter '%'".format(class, propertyKey.asSetter)
				);
				//setting properties should affect the obj properties
				testVal = testClass.generateRandomProperty(propertyKey);
				try{
					obj.perform(propertyKey.asSetter, testVal);
					this.assertEquals(
						obj.properties[propertyKey],
						testVal,
						"[%] - Property setter changed the internal properties for '%'".format(class, propertyKey)
					);
				} {
					this.failed(thisMethod,
						"[%] - Should not throw error when calling properties setter for '%'".format(class, propertyKey)
					);
				}
			});
		});
	}

	test_SetAndDoActionWithParamAsArg{
		this.class.classesForTesting.do({arg testClass;
			var valueObj = testClass.new();
			var wasRun = false;
			var gotParamAsArg = false;
			valueObj.action = {arg valueObj;
				wasRun = true;
				gotParamAsArg = valueObj === valueObj;
			};
			valueObj.doAction;
			this.assert(
				wasRun and: {gotParamAsArg},
				"[%] - Value action was set, run and got passed itself as arg.".format(testClass)
			);
			valueObj.free;
		});
	}

	test_SetGetRemoveEnableAndDisableAction{
		this.class.classesForTesting.do({arg testClass;
			var valueObj = testClass.new();
			var wasRun, aValue;
			var anAction, anotherAction;
			anAction = {arg valueObj; wasRun = true; };
			anotherAction = {arg valueObj; wasRun = true; };

			//action should point to identical action as defined
			valueObj.action = anAction;
			this.assert(
				anAction === valueObj.action, "Value action point to correct action");

			//Remove action
			valueObj.action = nil;
			this.assert(valueObj.action.isNil, "Removed Value action succesfully");

			//should be enabled by default
			this.assert( valueObj.enabled, "Value should be enabled by default" );

			//disable should set 'enabled' false
			valueObj.disable;
			this.assert( valueObj.enabled.not, "Value should be disabled by calling '.disable'" );

			//disable should prevent action from being run
			wasRun = false;
			valueObj.action = anAction;
			valueObj.doAction;
			this.assert(wasRun.not, "Value action was prevented to run by disable");

			//We should still be able to acces the action instance
			this.assert(
				valueObj.action.notNil and: {valueObj.action === anAction},
				"Wasn't able to access Value action while being disabled"
			);

			//enable should set 'enabled' true
			valueObj.enable;
			this.assert(valueObj.enabled, "Value was enabled again");

			//enable should allow action to be run
			wasRun = false;
			valueObj.doAction;
			this.assert(wasRun, "Value enabled, reenabled action to run");

			//If another action is set when Value is disabled, the
			//other action should be returned and run when the Value is enabled
			//again
			anAction = {arg valueObj; aValue = 111;};
			anotherAction = {arg valueObj; aValue = 222;};
			valueObj.action = anAction;
			valueObj.disable;
			valueObj.action = anotherAction;
			valueObj.enable;
			valueObj.doAction;
			this.assert(valueObj.action === anotherAction and: { aValue == 222; },
				"Value action was changed correctly during disabled state"
			);

			//Action should be run upon enable if optionally defined in enable call
			wasRun = false;
			valueObj.disable;
			valueObj.action = {arg valueObj; wasRun = true; };
			valueObj.enable(doActionWhenEnabled: true);
			this.assert(wasRun,
				"Value action was optionally performed on enabled");
			valueObj.free;
		});

	}

	test_SetVariablesThroughProperties{
		this.class.classesForTesting.do({arg testClass;
			var valueObj, aProperties, anAction;
			var wasRun = false;
			anAction = {arg valueObj; wasRun = true;};
			aProperties = (
				enabled: false
			);
			valueObj = testClass.new(aProperties);
			valueObj.action = anAction;

			//enabled is set through properties
			this.assert(valueObj.enabled.not,
				"Value was disabled through properties"
			);

			//action is set through properties
			valueObj.enable; //Reenable Value
			valueObj.doAction;
			this.assert(wasRun and: {valueObj.action === anAction},
				"Value action was set through properties"
			);
			valueObj.free;
		});
	}

	test_GetProperties{
		this.class.classesForTesting.do({arg class;
			var wasRun = false;
			var testClass = this.class.findTestClass(class);
			var properties = testClass.generateRandomProperties();
			var valueObj = class.new( properties );

			this.assertEquals(
				valueObj.properties(), properties,
				"% returned correct properties.".format(class)
			);
			valueObj.free;
		});
	}


	//previously Value PArameter test methods
	test_SetGetValue{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue;
			var valueObj = class.new();
			testClass = this.class.testclassForType( class.type );
			testValue = testClass.generateRandomValue;
			valueObj.value = testValue;
			this.assertEquals(
				valueObj.value, testValue, "% 'value' was set".format(testClass.name)
			);
			valueObj.free;
		});
	}

	test_SetGetDefaultValue{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue;
			var valueObj;
			try{
				testClass = this.class.testclassForType( class.type );
				testValue = testClass.generateRandomValue;
				valueObj = class.new((defaultValue: testValue));
				this.assertEquals(
					valueObj.defaultValue, testValue, "Value defaultValue was set [%]".format(testClass.name)
				);
				this.assertEquals(
					valueObj.value, testValue,
					"Value value was set to defined defaultValue when value was not defined [%]".format(testClass.name)
				);
				valueObj.free;
			} {|err|
				this.failed(
					thisMethod,
					"Unknown test fail for %\n\t%".format(class, err.errorString)
				);
			};
		});
	}

	test_ResetSetValueToDefault{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue, wasRun;
			var valueObj;
			testClass = this.class.testclassForType( class.type );
			testValue = testClass.generateRandomValue;
			valueObj = class.new;
			valueObj.value = testClass.generateRandomValue;
			valueObj.defaultValue = testClass.generateRandomValue;
			valueObj.reset;
			this.assertEquals(
				valueObj.value, valueObj.defaultValue,
				"[%] - Value value was set to defaultValue upon reset".format(class)
			);
			wasRun = false;
			valueObj.action_({arg p; wasRun = true;});
			valueObj.value = testClass.generateRandomValue;
			valueObj.defaultValue = testClass.generateRandomValue;
			valueObj.reset(doActionUponReset: true);
			this.assert(
				wasRun,
				"[%] - Value action was run upon reset when defined to do so".format(class)
			);
			valueObj.free;
		});
	}

	test_DefaultValueShouldNotBeNil{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue, wasRun;
			var valueObj;
			testClass = this.class.testclassForType( class.type );
			valueObj = class.new();
			this.assert(
				valueObj.defaultValue.notNil,
				"Value did not initialize defaultValue to nil [%]".format(
					class.name
				)
			);
			valueObj.free;
		});
	}

	test_Typechecking{
		var wrongValuesForType = (
			integer: \hei,
			decimal: \hei,
			string: 432,
			boolean: 1,
			array: \bingo,
			list: \halo,
			dictionary: -0.93,
			function: 123,
			schema: \hei,
			tuple: \halo
		);
		this.class.classesForTesting.do({arg class;
			var testClass, testValue, wasRun;
			var valueObj;
			testClass = this.class.testclassForType( class.type );
			valueObj = class.new();
			testValue = wrongValuesForType[class.type];
			try{
				this.assert(
					valueObj.isValidType(testValue).not,
					"Value value input of wrong type failed validation check corretly [%]".format(class.name)
				);
			} {|err|
				this.failed(
					thisMethod,
					"Value input value validation failed by unknown error. [%]\n\t%".format(class.name, err.errorString)
				);
			};
		});
	}

	test_AccessValueInAction{
		this.class.classesForTesting.do({arg class;
			try{
				var testClass, testValue, wasRun;
				var valueObj, gotValue = false, gotParamPassed = false;
				testClass = this.class.testclassForType( class.type );
				valueObj = class.new();
				testValue = testClass.generateRandomValue;
				valueObj.value = testValue;
				valueObj.action = {arg p;
					gotParamPassed = p === valueObj;
					gotValue = p.value == testValue;
				};
				valueObj.doAction;
				this.assert(gotParamPassed,
					"ValueValue got valueObj passed in action [%]".format(class.name));
				this.assert(gotValue,
					"ValueValue got value in action [%]".format(class.name));
				valueObj.free;
			} {|err|
				this.failed(
					thisMethod,
					"Value test failed unknown error [%]\n\t%".format(
						class.name,
						err.errorString
					)
				);
			};
		});
	}
	test_ValueAction{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue;
			var valueObj, wasRun, gotUpdatedValue;
			wasRun = false;
			gotUpdatedValue = false;
			testClass = this.class.testclassForType( class.type );
			testValue = testClass.generateRandomValue;
			valueObj = class.new();
			valueObj.action = {arg p;
				wasRun = true;
				gotUpdatedValue = p.value == testValue;
			};
			valueObj.value = testClass.generateRandomValue;
			valueObj.valueAction_(testValue);
			this.assert(
				wasRun and: {gotUpdatedValue},
				"ValueValue valueAction set correct value and passed it into action"
			);
			valueObj.free;
		});
	}
	test_FilterRepeatingValues{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue;
			var valueObj;
			var wasRun = false;
			testClass = this.class.testclassForType( class.type );
			testValue = testClass.generateRandomValue;
			valueObj = class.new();
			valueObj.filterRepetitions = true;
			valueObj.action = {arg p;
				wasRun = true;
			};
			//Action should not be run when input value are equal to current value
			valueObj.value = testValue;
			valueObj.valueAction_(testValue);
			this.assert(
				wasRun.not, "ValueValue action was prevented to run since values where equal"
			);
		});
	}

	test_SetVariablesFromProperties{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue, testPropParams;
			var valueObj;
			var testProperties;
			testClass = this.class.testclassForType( class.type );
			testPropParams = VTMOrderedIdentityDictionary[
				\value -> (),
				\defaultValue -> (),
				\filterRepetitions -> ()
			];
			testProperties = testClass.generateRandomProperties(
				testPropParams
			);
			valueObj = VTMValue.makeFromProperties(testProperties);
			testPropParams.keysValuesDo({arg key, val;
				this.assertEquals(
					valueObj.perform(key), testProperties[key],
					"Value set % through properties [%]".format(key, class.name)
				);
			});
			valueObj.free;
		});
	}

	test_Enum{
		this.class.classesForTesting.do({arg class;
			var testClass, testValue;
			var valueObj;
			var testEnum;
			var testProperties;
			var testPropParams = VTMOrderedIdentityDictionary[
				\value -> (),
				\defaultValue -> (),
				\filterRepetitions -> (),
				\enabled -> (value: true),
				\enum -> ()
			];
			testClass = this.class.testclassForType( class.type );
			testProperties = testClass.generateRandomProperties(
				testPropParams
			);
			testEnum = testProperties.at(\enum);
			valueObj = VTMValue.makeFromType(class.type, testProperties);
			this.assertEquals(
				valueObj.enum, testEnum,
				"[%] set and returned correct enum".format(class)
			);
			valueObj.free;
		});
	}

	// test_GetProperties{
	// 	this.class.classesForTesting.do({arg class;
	// 		var testClass, testValue;
	// 		var name = "my%".format(class.name);
	// 		var valueObj;
	// 		var testProperties, testProperties;
	// 		testClass = this.class.testclassForType( class.type );
	// 		testProperties = testClass.generateRandomProperties(
	// 			[
	// 				\value,
	// 				\defaultValue,
	// 				\path,
	// 				\action -> {arg p; 1 + 1 },
	// 				\filterRepetitions,
	// 				\name,
	// 				\type -> class.type,
	// 				\enabled -> true,
	// 				\enum
	// 			]
	// 		);
	// 		valueObj = VTMValue.makeFromProperties(testProperties);
	// 		testProperties = testProperties.deepCopy;
	// 		testProperties.put(\action, testProperties[\action].asCompileString);
	// 		this.assert(
	// 			testProperties.keys.sect(valueObj.properties.keys) == testProperties.keys,
	// 			"ValueValue returned correct properties keys for ValueValue level [%]".format(class.name)
	// 		);
	// 		//			this.assertEquals(
	// 		//				testProperties.sect(valueObj.properties),
	// 		//			   	testProperties,
	// 		//			   	"ValueValue returned correct properties values for ValueValue level [%]".format(class.name)
	// 		//			);
	// 		valueObj.free;
	// 	});
	// }

}
