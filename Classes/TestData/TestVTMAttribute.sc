TestVTMAttribute : TestVTMControl {

	test_initDefaultItemValues{
		var defaultItemValue = 0.25;
		var size = 10;
		var props = (
			type: \array,
			itemType: \decimal,
			size: size,
			defaultValue: defaultItemValue,
			minVal: 0.0,
			maxVal: 1.0
		);
		var v = VTMAttribute('attributeArrayValueTest', props);
		this.assertEquals(v.get(\value), defaultItemValue ! size);
		this.assertEquals(v.get(\defaultValue), defaultItemValue);
	}
}
