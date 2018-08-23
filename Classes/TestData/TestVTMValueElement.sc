TestVTMControl : TestVTMData {
	*generateRandomParameter{arg key, params;
		var result = super.generateRandomParameter(key, params);
		result = switch(key,
			\type, {TestVTMValue.classesForTesting.collect(_.type).choose; },
			{result}
		);
		^result;
	}
}
