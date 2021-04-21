TestVTMControlPage : VTMUnitTest {
	test_construction{
		var scene = VTMScene('controlPageTester');
		var page = VTMControlPage(scene);

		this.assert(true);
	}
}
