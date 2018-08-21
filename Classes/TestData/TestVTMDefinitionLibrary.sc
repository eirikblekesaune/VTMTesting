TestVTMDefinitionLibrary : TestVTMElement {
	*generateRandomParameter{arg key, params;
		var result;
		result = super.generateRandomParameter(key, params);
		result = switch(key,
			\folderPath, {VTM.vtmPath +/+ 'Classes/TestClasses/DataForTests/Definitions'},
			\includedPaths, { { this.generateRandomPath } ! rrand(1,7)},
			\excludedPaths, { { this.generateRandomPath } ! rrand(1,7)}
		);
		^result;
	}

}
