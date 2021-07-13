import java.util.HashSet;
import java.util.ArrayList;
import java.lang.InterruptedException;
import java.util.concurrent.CountDownLatch;

class TestRunner {
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
			TestRunner.inst.createTestSuiteRunners();
			TestRunner.inst.start();
			TestRunner.inst.latch.await();
		} catch (InterruptedException e) {
			throw new AutoGraderException("An error occurred while grading.");
		}
	}

	private TestRunner(){
		this.studentsToGrade=new HashSet<String>();
		this.threadStudentBreakup=new ArrayList<HashSet<String>>();
		this.testSuiteRunners=new ArrayList<TestSuiteRunner>();
	}

	private void setStudentsToGrade(){
		this.studentsToGrade=Settings.getHWData().getStudents();
		if (this.studentsToGrade==null ||
		    (this.studentsToGrade!=null && this.studentsToGrade.size()==0)){
			this.studentsToGrade=SubmissionManager.getStudents();
		}
		//TODO- filter out students not in submission list
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

