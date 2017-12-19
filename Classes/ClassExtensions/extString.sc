+ String {
	*makeRandomHexString { arg bytes = 8;
		^"0x%".format(
			String.newFrom(String.newFrom({"0123456789ABCDEF".choose} ! bytes))
		);
	}
}
