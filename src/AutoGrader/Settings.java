import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

public class Settings {
	private HWData hwData;
	private TestSuite testSuite;
	private static final Settings inst=new Settings();

	public static void load(String[] args) throws AutoGraderException {
		Settings.inst.verifyArgs(args);
		Settings.inst.setHWData(args[0]);
		Settings.inst.setTestSuite(args[1]);
	}

	public static HWData getHWData(){
		return Settings.inst.hwData;
	}

	public static TestSuite getTestSuite(){
		return Settings.inst.testSuite;
	}

	private Settings(){
		this.hwData=null;
		this.testSuite=null;
	}

	private void verifyArgs(String[] args) throws AutoGraderException {
		boolean argsOk=(args.length==2);
		for (int i=0; i<args.length && argsOk; i++){
			argsOk&=Files.exists(Paths.get(args[i]));
		}
		if (!argsOk){
			throw new AutoGraderException("One of the settings files given in args is non-existent.");
		}
	}

	private void setHWData(String settingsFile) throws AutoGraderException {
		try{
			GsonBuilder builder=new GsonBuilder();
			builder.setPrettyPrinting();
			Gson gson=builder.create();
			String jsonStr=new String(Files.readAllBytes(Paths.get(settingsFile)));
			this.hwData=gson.fromJson(jsonStr,HWData.class);
			this.hwData.verifyInputs();
		} catch(IOException e){
			throw new AutoGraderException("The given HWData settings file is non-existent.");
		}
	}

	private void setTestSuite(String testsFile) throws AutoGraderException {
		try {
			GsonBuilder builder=new GsonBuilder();
			builder.registerTypeAdapter(Test.class, new TestAdapter());
			builder.setPrettyPrinting();
			Gson gson=builder.create();
			String jsonStr=new String(Files.readAllBytes(Paths.get(testsFile)));
			this.testSuite=gson.fromJson(jsonStr,TestSuite.class);
		} catch (IOException e){
			throw new AutoGraderException("The given code settings file is non-existent.");
		}

		//java.lang.reflect.Type listType=new com.google.gson.reflect.TypeToken<ArrayList<Test>>() {}.getType();
		//JsonArray jsonList=(JsonArray)(gson.fromJson(jsonStr,JsonObject.class).get("tests"));
		//System.out.println(jsonList.toString());
		//ArrayList<Test> itemList=gson.fromJson(jsonList,listType);

		//for (Test item: itemList){
		//	System.out.println(item.getPoints());
		//	System.out.println(item.getComment());
		//}

		//return new TestSuite(itemList);
	}
}

