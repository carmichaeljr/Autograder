import java.util.Map;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;

class Gradebook {
	private Map<String,Pair<Float,ArrayList<String>>> grades;
	private static final Gradebook inst=new Gradebook();
	
	public static void load(){
		//
	}

	public static void record(String name, Pair<Float,ArrayList<String>> data){
		Gradebook.inst.grades.put(name,data);
	}
	public static void record(String name, float score, ArrayList<String> comments){
		Gradebook.record(name,new Pair<Float,ArrayList<String>>(score,comments));
	}

	public static void save(){
		System.out.println(Gradebook.inst.grades.toString());
	}

	private Gradebook(){
		this.grades=Collections.synchronizedMap(new HashMap<String,Pair<Float,ArrayList<String>>>());
	}
}


