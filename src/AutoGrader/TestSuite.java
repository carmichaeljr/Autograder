import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

class TestSuite {
	@SerializedName("tests")
	private ArrayList<Test> tests;

	public TestSuite(ArrayList<Test> tests){
		this.tests=tests;
	}

	public Test get(int i){
		if (i>=0 && i<this.tests.size()){
			return this.tests.get(i);
		}
		return null;
	}

	public int size(){
		return this.tests.size();
	}
}

