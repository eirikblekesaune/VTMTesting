TestVTMPath : VTMUnitTest {
	test_newGlobalPath{
		var pathStr = "/:hardwareDevices/xTouch/layer.A/fader.1";
		var path = VTMPath(pathStr);

		this.assertEquals(5, path.length(), "VTMPath as of same length");
		this.assert(path.isGlobal, "VTMPath was global");
		this.assert(path.isLocal.not, "VTMPath was not local");
		this.assert(path.hasParentPath, "Has parent");
		this.assertEquals(path.parentPath.asString, "/:hardwareDevices/xTouch/layer.A");
		this.assertEquals(path.parentPath.parentPath.asString, "/:hardwareDevices/xTouch");
		this.assertEquals(path.parentPath.parentPath.parentPath.asString, "/:hardwareDevices");
		this.assertEquals(path.parentPath.parentPath.parentPath.parentPath.asString, "/");
		this.assertEquals(
			path.parentPath.parentPath.parentPath.parentPath.parentPath.asString,
			"/", "Parentless returned itself");
		this.assert(
			path.parentPath.parentPath.parentPath.parentPath.parentPath.hasParentPath.not,
			"Parentless has no parent");
	}
}