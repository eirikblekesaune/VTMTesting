TestVTMValueMapping : VTMUnitTest {
	test_setupValueMapping{
		var source, destination, mapping;
		source = VTMValue.integer(
			(minVal: 0, maxVal: 10)
		);
		destination = VTMValue.integer(
			(minVal: 0, maxVal: 100)
		);

		mapping = VTMValueMapping((source: source, destination: destination));
		mapping.enable;
		//should forward value from source to destination by default
		source.value_(5);


		this.assertEquals(destination.value, 50);
	}

	test_setupBinding{
		var source, destination, mapping;
		source = VTMValue.integer(
			(minVal: 0, maxVal: 10)
		);
		destination = VTMValue.integer(
			(minVal: 0, maxVal: 100)
		);

		mapping = VTMValueMapping((
			source: source,
			destination: destination,
			type: \bind
		));
		mapping.enable;
		//should forward value from source to destination by default
		source.value_(5);
		this.assertEquals(destination.value, 50);
		destination.value_(10);
		this.assertEquals(source.value, 1);
	}

}
