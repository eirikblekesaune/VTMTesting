TestVTMData : VTMUnitTest {

	*classesForTesting{
		^this.findTestedClass.allSubclasses.reject(_.isAbstractClass);
	}

	*generateRandomParameters{| parameterKeys, randomizationSettings |
		var result = VTMOrderedIdentityDictionary[];
		var testedClass = this.findTestedClass;
		var parameterDescriptions;
		var keys;

		//if no parameter settings declared,
		//generate all parameters in description
		if(parameterKeys.notNil, {
			keys = parameterKeys;
		}, {
			keys = testedClass.parameterDescriptions.keys;
		});
		parameterDescriptions = testedClass.description[\parameters];

		keys.do({| key |
			var val, description, randSettings;
			if(randomizationSettings.notNil
				and: {randomizationSettings.includesKey(key)}, {
				randSettings = randomizationSettings[key];
			});
			description = parameterDescriptions[key];
			val = this.generateRandomParameter(
				key, description, randSettings
			);

			if(val.notNil, {
				result.put(key, val);
			});
		});
		^result;
	}

	*generateRandomDeclaration{arg params;
		^this.generateRandomParameters(params);
	}

	*generateRandomParameter{arg key, params;
		var result;
		result = switch(key,
			\name, { this.generateRandomSymbol; }
		);

		^result;
	}

	*generateRandomManagerObject{
		var result;
		var managerClass;
		managerClass = this.findTestedClass.managerClass;
		result = managerClass.new();
		^result;
	}

	test_noNameShouldError{
		var obj;
		//Should throw error when no name is given.
		this.class.classesForTesting.do({arg class;
			try{
				obj = class.new(nil);
				this.failed(thisMethod,
					"[%] - Did not throw error when no name was given"
					.format(class)
				);
			} {
				this.passed(thisMethod,
					"[%] - Threw error when no name was given"
					.format(class);
				);
			};
		});
	}

	test_defaultConstruction{
		this.class.classesForTesting.do({arg class;
			var testName;
			var obj;
			var testClass;
			var parameters;
			var declaration;
			testClass = this.class.findTestClass(class);
			//only test using mandatory parameters
			parameters = testClass.generateRandomParameters(
				class.mandatoryParameters
			);
			testName = testClass.generateRandomSymbol;
			declaration = parameters.deepCopy;
			try{
				"testName: % declaration: %".format(
				testName, declaration).postln;
				obj = class.new(testName, declaration);
				this.passed(thisMethod,
					"[%] - Made default Data object"
					.format(class)
				);
			} {|err|
				this.failed(thisMethod,
					"[%] - Failed to make default Data object: %"
					.format(class, err.errorString);
				);
				err.postProtectedBacktrace;
			};
		});
	}

	test_initName{
		var obj, testParameters, managerObj;
		this.class.classesForTesting.do({arg class;
			var testClass = VTMUnitTest.findTestClass(class);
			var testName = VTMUnitTest.generateRandomSymbol;

			testParameters = testClass.generateRandomParameters;
			//"Making with these parameters: %".format(testParameters).debug;

			//Testing without manager
			testName = VTMUnitTest.generateRandomSymbol;
			obj = class.new(
				testName,
				testParameters
			);

			// //check if name initialized
			this.assertEquals(
				obj.name,
				testName,
				"[%] - init 'name' correctly".format(class)
			);

			obj.free;
		});
	}

	test_addToManager{
		var obj, testParameters, managerObj;
		this.class.classesForTesting.do({arg class;
			var testClass = VTMUnitTest.findTestClass(class);
			var testName = VTMUnitTest.generateRandomSymbol;
			var managerClass = class.managerClass;
			//managerClass shouldn not be nil
			this.assert(managerClass.notNil,
				"[%] - found manager class for test class".format(class)
			);
			managerObj = managerClass.new;
			this.assert(managerObj.notNil,
				"[%] - made manager obj for test class".format(class)
			);

			testParameters = testClass.generateRandomParameters;

			testName = VTMUnitTest.generateRandomSymbol;
			obj = class.new(
				testName,
				testParameters
			);

			//Add it to a manager
			managerObj.addItem(obj);
			//
			//Should now be added to the manager items
			this.assert(
				managerObj.hasItemNamed(obj.name)
				and: {managerObj[obj.name] === obj},
				"[%] Manager added the data object".format(class)
			);

			//the manager object must be identical
			this.assert(
				 obj.manager === managerObj,
				"[%] - data got the manager updated".format(class)
			);

			obj.free;

			//Should now be removed from the manager
			this.assert(
				managerObj.hasItemNamed(obj.name).not,
				"Manager removed the freed object."
			);
			managerObj.free;
		});
	}
}
