TestVTMComposableContext : TestVTMContext {
	test_Ownership{
		var a,x,y,z;
		a = VTMScene('aaa');
		x = VTMModule('xxx');
		y = VTMModule('yyy');
		z = VTMModule('zzz');

		a.takeOwnership(x);
		a.takeOwnership(y);
		a.takeOwnership(z);

		this.assertEquals(
			a.ownedContexts,
			Dictionary[
				x.fullPath -> x,
				y.fullPath -> y,
				z.fullPath -> z,
			].as(Event),
			"Owned contexts was registered"
		);

		this.assert(
			a === x.owner,
			"Owner is identical with registered owner in owned context"
		);

		a.releaseOwnership(x);

		this.assert(
			x.isOwned.not
			and: {a.ownedContexts.includes(x).not}
			and: {x.owner.isNil}
			and: {a.ownedContexts == Dictionary[
				y.fullPath -> y,
				z.fullPath -> z,
			].as(Event)}
			,
			"Owned context was unregistered"
		);
	}
}
