package AutoGrader;

import java.util.HashSet;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.concurrent.CountDownLatch;

class TestRunner {
	private int progress;
	private CountDownLatch latch;
	private HashSet<String> studentsToGrade;
	private ArrayList<HashSet<String>> threadStudentBreakup;
	private ArrayList<TestSuiteRunner> testSuiteRunners;
	private static final TestRunner inst=new TestRunner();

	public static void load(){
		TestRunner.inst.setStudentsToGrade();
		TestRunner.inst.setThreadStudentBreakup();
	}

	public static void run() throws AutoGraderException {
		try {
			TestRunner.inst.progress=0;
			TestRunner.inst.createTestSuiteRunners();
			TestRunner.inst.start();
			TestRunner.inst.latch.await();
		} catch (InterruptedException e) {
			throw new AutoGraderException("An error occurred while grading.");
		}
	}

	public static synchronized void incrementProgresBar(){
		TestRunner.inst.progress++;
		Print.progressBar(TestRunner.inst.progress,TestRunner.inst.studentsToGrade.size(),"");
	}

	private TestRunner(){
		this.progress=0;
		this.studentsToGrade=new HashSet<String>();
		this.threadStudentBreakup=new ArrayList<HashSet<String>>();
		this.testSuiteRunners=new ArrayList<TestSuiteRunner>();
	}

	private void setStudentsToGrade(){
		HashSet<String> availableStudents=SubmissionManager.getStudents();
		for (String student: Settings.getHWData().getStudents()){
			if (availableStudents.contains(student)){
				this.studentsToGrade.add(student);
			} else {
				Print.warning(String.format("The student '%s' was not found in the submissions folder.",student));
			}
		}
		if (this.studentsToGrade==null ||
		    (this.studentsToGrade!=null && this.studentsToGrade.size()==0)){
			this.studentsToGrade=SubmissionManager.getStudents();
		}
		//System.out.println(this.studentsToGrade.toString());
	}

	private void setThreadStudentBreakup(){
		int i=0, numThreads=Settings.getHWData().getNumThreads();
		for (String student: this.studentsToGrade){
			if (this.threadStudentBreakup.size()<i%numThreads+1){
				this.threadStudentBreakup.add(new HashSet<String>());
			}
			this.threadStudentBreakup.get(i%numThreads).add(student);
			i++;
		}
		this.latch=new CountDownLatch(Settings.getHWData().getNumThreads());
		//System.out.println(this.threadStudentBreakup.size());
		//System.out.println(this.threadStudentBreakup.toString());
	}

	private void createTestSuiteRunners(){
		for (int i=0; i<this.threadStudentBreakup.size(); i++){
			TestSuiteRunner t=new TestSuiteRunner(
						this.latch,
						Settings.getTestSuite(),
						this.threadStudentBreakup.get(i),i);
			this.testSuiteRunners.add(t);
		}
	}

	private void start(){
		for (int i=0; i<this.testSuiteRunners.size(); i++){
			this.testSuiteRunners.get(i).start();
		}
	}
}

