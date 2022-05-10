package AutoGrader;

import java.util.HashSet;
import java.util.ArrayList;
import java.lang.Thread;
import java.lang.Runnable;
import java.util.concurrent.CountDownLatch;

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
		//System.out.println("Running: "+this.thread.getName());
		for (String student: this.students) {
			//System.out.println("Grading: "+student);
			if (SubmissionManager.prepareForGrading(student)){
				this.runTestsOnStudent(student);
				SubmissionManager.cleanUpAfterGrading(student);
			}
		}

		this.latch.countDown();
		//System.out.println("Ending: "+this.thread.getName());
		this.proc.destroy();
	}

	public void runTestsOnStudent(String student){
		//Print.line(String.format("Student: %s========================================",student));
		float studentScore=0;
		ArrayList<String> comments=new ArrayList<String>();
		for (int k=0; k<this.tests.size(); k++){
			Pair<Float,String> result=null;
			try {
				result=this.tests.get(k).run(this.proc,student);
			} catch (AutoGraderException e){
				result=new Pair<Float,String>((float)0.0,"An error occurred running tests.");
				this.proc.destroy();
				this.proc=new RarsProcManager();
			}
			studentScore+=result.getKey();
			if (result.getValue()!=null && result.getValue().length()>0){
				comments.add(result.getValue());
			}
		}
		Gradebook.record(student,studentScore,comments);
		TestRunner.incrementProgresBar();
		//System.out.println(student+" "+studentScore+" "+comments.toString());
	}

	public void start(){
		this.thread.start();
	}
}
