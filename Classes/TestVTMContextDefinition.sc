TestVTMContextDefinition : VTMUnitTest {
	*generateRandomDefinitionEnvironment{arg params;
		var result = Environment.new;
		result.use{
			//Make prepare function
			~prepare = {
				"ContextDefinition '%' - prepare".format(~name).postln;
			};
			//Make run function
			~run = {
				"ContextDefinition '%' - run".format(~name).postln;
			};
			//Make free function
			~free = {
				"ContextDefinition '%' - free".format(~name).postln;
			};
			//Make parameter descriptions

			TestVTMAttribute.generateRandomDeclaration;
			TestVTMAttribute.generateRandomDescription;
			TestVTMIntegerValue.generateRandomProperties;
			//Make attribute descriptions
			//Make command descriptions
			//Make signal description
			//Make return descriptions
		};
		^result;
	}

	*generateRandomContextDefinition{arg params, context;
		var result;
		var envir = this.generateRandomDefinitionEnvironment(params);
		var defname = this.generateRandomSymbol;


		result = VTMContextDefinition(defname, envir, context);
		^result;
	}
}
