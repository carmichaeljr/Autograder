package AutoGrader;

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

	abstract public Pair<Float,String> run(RarsProcManager rarsProcRef, String student) throws AutoGraderException;
}
