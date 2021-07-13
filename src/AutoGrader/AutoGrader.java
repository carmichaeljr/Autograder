import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.io.IOException;
import java.lang.Runnable;
import java.lang.Thread;
import java.util.concurrent.CountDownLatch;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.SerializedName;

abstract class Test {
	@SerializedName("points")
	protected float points;
	@SerializedName("comment")
	protected String comment;
	@SerializedName("applyTo")
	protected String applyTo;

	public Test(float points, String comment){
		this.points=points;
		this.comment=comment;
	}

	public float getPoints(){
		return this.points;
	}
	public String getComment(){
		return this.comment;
	}
	public String getApplyableFiles(){
		return this.applyTo;
	}

	abstract public Pair<Float,String> run(RarsProcManager rarsProcRef);
}

class LengthTest extends Test {
	@SerializedName("minLines")
	private int minLines;

	public LengthTest(float points, String comment){
		super(points,comment);
		this.minLines=0;
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef){
		return new Pair<Float,String>(super.points,super.comment);
	}
}

class CommentTest extends Test {
	@SerializedName("percentage")
	private float percentage;
	@SerializedName("symbol")
	private String symbol;

	public CommentTest(float points, String comment, float percentage, String symbol){
		super(points,comment);
		this.percentage=percentage;
		this.symbol=symbol;
	}

	public float getPercentage(){
		return this.percentage;
	}
	public String getSymbol(){
		return this.symbol;
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef){
		return new Pair<Float,String>(super.points,super.comment);
	}
}

class KeywordTest extends Test {
	@SerializedName("takeAwayPoints")
	private boolean takeAwayPoints;
	@SerializedName("onePointPer")
	private boolean onePointPer;
	@SerializedName("words")
	private ArrayList<String> words;

	public KeywordTest(float points, String comment){
		super(points,comment);
		this.takeAwayPoints=false;
		this.onePointPer=false;
		this.words=new ArrayList<String>();
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef){
		return new Pair<Float,String>(super.points,super.comment);
	}
}

class ExecuteTest extends Test {
	@SerializedName("removeWhitespace")
	private boolean removeWhiteSpace;
	@SerializedName("outputConditional")
	private String outputConditional;
	@SerializedName("input")
	private ArrayList<String> inputs;
	@SerializedName("output")
	private ArrayList<String> outputs;
	@SerializedName("regVals")
	private HashMap<String,Integer> regVals;

	public ExecuteTest(float points, boolean removeWhiteSpace, String outputConditional){
		super(points,"");
		this.removeWhiteSpace=removeWhiteSpace;
		this.outputConditional=outputConditional;
		this.inputs=new ArrayList<String>();
		this.outputs=new ArrayList<String>();
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef){
		return new Pair<Float,String>(super.points,super.comment);
	}
}

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

//https://stackoverflow.com/questions/4112470/java-how-to-both-read-and-write-to-from-process-thru-pipe-stdin-stdout/4115082#4115082
class RarsProcManager {
	private Process proc;
	private BufferedReader in;
	private BufferedWriter out;
	private static final String PROC_START=String.format(
			"java -cp \"%s/dependencies/*\" RarsProc",
			new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath()
			);


	public RarsProcManager(){
		try {
			this.proc=Runtime.getRuntime().exec(RarsProcManager.PROC_START);
			this.in=new BufferedReader(new InputStreamReader(this.proc.getInputStream()));
			this.out=new BufferedWriter(new OutputStreamWriter(this.proc.getOutputStream()));
		} catch (IOException e) {
			Print.warning("Some submissions were unable to be run in RARS. Some students scores will be inaccurate.");
		}
	}

	public void pipeNoResponse(String message){
		try {
			this.out.write(message);
			this.out.flush();
		} catch (IOException e){
			Print.warning("An error occurred writing to a RARS process.");
		}
	}

	//public String pipe(String message){
	//	String rv;
	//	this.out.write(message);
	//	this.out.flush();
	//	//rv=this.in.readAll();
	//	return rv;
	//}
	//public String pipe(ArrayList<String> files, ArrayList<String> stdin, HashMap<String> registers) throws IOException {
	//	return this.pipe(this.generateMessage(files,stdin,registers));
	//}

	//private String generateMessage(ArrayList<String> files, ArrayList<String> stdin, HashMap<String> registers){
	//	return "";
	//}

	public void destroy(){
		this.pipeNoResponse("done");
		this.proc.destroy();
	}
}

class TestSuiteRunner implements Runnable {
	private Thread thread;
	private CountDownLatch latch;
	private TestSuite tests;
	private RarsProcManager proc;
	private HashSet<String> students;
	
	public TestSuiteRunner(CountDownLatch latch, TestSuite tests, HashSet<String> students, int num){
		this.tests=tests;
		this.latch=latch;
		this.students=students;
		this.thread=new Thread(this,String.format("TestSuiteRunner%d",num));
		this.proc=new RarsProcManager();
	}

	public void run(){
		System.out.println("Running: "+this.thread.getName());
		for (String student: this.students) {
			//System.out.println("Grading: "+student);
			if (SubmissionManager.prepareForGrading(student)){
				this.runTestsOnStudent(student);
				SubmissionManager.cleanUpAfterGrading(student);
			}
		}

		this.latch.countDown();
		System.out.println("Ending: "+this.thread.getName());
		this.proc.destroy();
	}

	public void runTestsOnStudent(String student){
		float studentScore=0;
		ArrayList<String> comments=new ArrayList<String>();
		for (int k=0; k<this.tests.size(); k++){
			Pair<Float,String> result=this.tests.get(k).run(this.proc);
			studentScore+=result.getKey();
			if (result.getValue()!=null && result.getValue().length()>0){
				comments.add(result.getValue());
			}
		}
		Gradebook.record(student,studentScore,comments);
		//System.out.println(student+" "+studentScore+" "+comments.toString());
	}

	public void start(){
		System.out.println("Starting: "+this.thread.getName());
		this.thread.start();
	}
}

public class AutoGrader {
	public static void main(String[] args){
		try {
			Settings.load(args);
			//Gradebook.load();
			SubmissionManager.load();
			TestRunner.load();
			TestRunner.run();
			SubmissionManager.cleanUp();
		} catch (AutoGraderException e){
			Print.warning(e.getMessage());
		} finally {
			Gradebook.save();
		}
	}
}
