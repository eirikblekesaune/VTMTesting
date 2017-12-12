TestVTMNoneValue : TestVTMValue{
	*generateRandomValue{
		VTMValue.typeToClass(TestVTMValue.testTypes.choose).prDefaultValueForType.class.generateRandom;
	}
}
