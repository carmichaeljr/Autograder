import java.util.HashSet;
import java.util.HashMap;
import java.util.ArrayList;
import java.io.File;
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

	public Pair<Float,String> gradingErrorFallback(String testType, String student){
		Print.warning(String.format("An error occurred performing a %s on  %'s submission.",
					testType,student));
		return new Pair<Float,String>((float)0.0,
				String.format("An error occurred performing a %s on  %'s submission.",
					testType,student)
				);
	}

	protected String getTestFile(String student){
		if (this.applyTo.equalsIgnoreCase("code") || this.applyTo.equalsIgnoreCase("assembly")){
			return SubmissionManager.getCodeFile(student);
		} else if (this.applyTo.equalsIgnoreCase("readme")){
			return SubmissionManager.getReadmeFile(student);
		}
		return null;
	}

	abstract public Pair<Float,String> run(RarsProcManager rarsProcRef, String student);
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
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student){
		return new Pair<Float,String>(super.points,super.comment);
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
			Pair<Float,String> result=this.tests.get(k).run(this.proc,student);
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
