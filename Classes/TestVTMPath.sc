TestVTMPath : VTMUnitTest {
	test_newGlobalPath{
		var pathStr = "/:hardwareDevices/xTouch/layer.A/fader.1";
		var path = VTMPath(pathStr);

		this.assertEquals(5, path.length(), "VTMPath as of same length");
		this.assert(path.isGlobal, "VTMPath was global");
		this.assert(path.isLocal.not, "VTMPath was not local");
		this.assert(path.hasParent, "Has parent");
		this.assertEquals(path.parent.asString, "/:hardwareDevices/xTouch/layer.A");
		this.assertEquals(path.parent.parent.asString, "/:hardwareDevices/xTouch");
		this.assertEquals(path.parent.parent.parent.asString, "/:hardwareDevices");
		this.assertEquals(path.parent.parent.parent.parent.asString, "/");
		this.assertEquals(path.parent.parent.parent.parent.parent.asString, "/", "Parentless returned itself");
		this.assert(path.parent.parent.parent.parent.parent.hasParent.not, "Parentless has no parent");

	}
}