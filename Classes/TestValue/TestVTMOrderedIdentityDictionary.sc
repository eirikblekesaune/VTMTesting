TestVTMOrderedIdentityDictionary : VTMUnitTest{

	test_MakeFromAssociationArray{
		var obj = VTMOrderedIdentityDictionary.newFromAssociationArray([
			\aaa -> [
				\mode -> \attribute,
				\type -> \integer
			],
			\bbb -> 22,
			\ccc -> [
				\gunner -> 333,
				\bingo -> \bjarne
			],
			\kjukken -> \hange,
			\fff -> [
				\haope -> [ \je, \aja, \rrr ],
				\tinig -> 3,
				\guing -> [
					\sokke -> [
						\giigie -> [ 1111,2222,3333]
					]
				]
			]
		], true);


		this.assertEquals(obj[\aaa], VTMOrderedIdentityDictionary[
			\mode -> \attribute, \type -> \integer]);
		this.assertEquals(obj[\fff][\tinig], 3);
		this.assertEquals(obj[\fff][\guing][\sokke][\giigie], [1111,2222,3333]);
	}
}