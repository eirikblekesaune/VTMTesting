TestVTMAttributeManager : TestVTMDataManager {

	*generateRandomPresetForAttributeDeclaration{arg declaration;
		var result = IdentityDictionary.new;
		declaration.do({arg attr;
			result.put(
				attr[\name],
				TestVTMAttribute.testclassForType(attr[\type]).generateRandomValue
			);
		});
		^result.asKeyValuePairs;
	}

	//make one of each type for testing
	*makeTestDeclaration{
		^TestVTMAttribute.testTypes.collect{arg item;
			TestVTMAttribute.generateRandomDeclaration(item)
		};
	}

	*generateRandomPresetForElement{arg element;
		var result = IdentityDictionary.new;
		element.attributes.do({arg attrName;
			var attr;
			attr = element.getAttribute(attrName);
			result.put(attrName, TestVTMAttribute.testclassForType(attr.type).generateRandomValue);
		});
		^result.asKeyValuePairs;
	}

	*generateRandomPresetDeclarationForElement{arg element;
		var result = IdentityDictionary.new;
		rrand(3,8).do{arg i;
			result.put(this.generateRandomString, this.generateRandomPresetForElement(element));
		};
		^result.asKeyValuePairs;
	}

	*generateRandomPresetDeclarationForAttributeDeclaration{arg declaration;
		var result = IdentityDictionary.new;
		rrand(3,8).do{arg i;
			result.put(this.generateRandomString, this.generateRandomPresetForAttributeDeclaration(declaration));
		};
		^result.asKeyValuePairs;
	}


	test_AddingAndRemovingPresets{
		var element, testPresets;
		var testPresetNames;
		element = TestVTMElement.generateRandomElement;
		element.prepare;

		//make some random presets
		testPresets = this.class.generateRandomPresetDeclarationForElement(element);
		testPresetNames = testPresets.clump(2).flop[0];

		testPresets.pairsDo({arg presetName, presetData;
			element.addPreset(presetData, presetName);
		});

		this.assertEquals(
			element.presets, testPresetNames,
			"AttributeManager added preset names correctly"
		);

		this.assertEquals(
			element.presetDeclaration, testPresets,
			"AttributeManager returned preset declaration correctly"
		);

		element.free;
	}

	test_InitElementWithPresets{}


}
