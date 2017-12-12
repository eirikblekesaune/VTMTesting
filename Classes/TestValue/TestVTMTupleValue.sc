TestVTMTupleValue : TestVTMListValue {

	*generateRandomValue{arg params;
		var result;
		result = super.generateRandomValue(params);
		^result;
	}

}
