package AutoGrader;

public class AutoGrader {
	public static void main(String[] args){
		try {
			Settings.load(args);
			Gradebook.load();
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
