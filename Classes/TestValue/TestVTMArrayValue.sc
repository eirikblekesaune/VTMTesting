TestVTMArrayValue : TestVTMCollectionValue {
	*generateRandomValue{arg params;
		^3.collect({1.0.rand2});// float list return here for now.
	}

	test_initDefaultItemValues{
		var defaultItemValue = 0.25;
		var size = 10;
		var props = (
			itemType: \decimal,
			size: size,
			defaultValue: defaultItemValue,
			minVal: 0.0,
			maxVal: 1.0
		);
		var v = VTMArrayValue(props);
		this.assertEquals(v.value, defaultItemValue ! size);
		this.assertEquals(v.defaultValue, defaultItemValue);
	}
}
