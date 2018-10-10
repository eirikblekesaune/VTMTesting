TestVTMNoneValue : TestVTMValue{
	*generateRandomValue{
		^VTMValue.typeToClass(
			TestVTMValue.testTypes.reject({|it|
				it == TestVTMNoneValue;
			}).choose;
		).prDefaultValueForType.class.generateRandom;
	}
}
