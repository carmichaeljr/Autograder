package AutoGrader;

import java.util.HashSet;
import java.util.ArrayList;
import java.nio.file.Files;
import java.nio.file.Paths;

import com.google.gson.annotations.SerializedName;

public class HWData {
	@SerializedName("gradingScript")
	private String gradingScript;
	@SerializedName("zippedSubmissions")
	private String zippedSubmissions;
	@SerializedName("cleanedSubmissionsDir")
	private String cleanedSubmissionsDir;
	@SerializedName("gradebook")
	private String gradebook;
	@SerializedName("students")
	private HashSet<String> students;
	@SerializedName("numThreads")
	private int numThreads;
	@SerializedName("acceptedCodeFiles")
	private ArrayList<String> acceptedCodeFiles;
	@SerializedName("acceptedReadmeFiles")
	private ArrayList<String> acceptedReadmeFiles;
	
	public HWData(String hwName, String zippedSubmissions, String cleanedSubmissionsDir, String gradebook){
		this.zippedSubmissions=zippedSubmissions;
		this.cleanedSubmissionsDir=cleanedSubmissionsDir;
		this.gradebook=gradebook;
	}

	public String getGradingScript(){
		if (this.gradingScript.length()==0){
			return null;
		}
		return this.gradingScript;
	}
	public String getZippedSubmissions(){
		return this.zippedSubmissions;
	}
	public String getCleanedSubmissionsDir(){
		return this.cleanedSubmissionsDir;
	}
	public String getGradebook(){
		return this.gradebook;
	}
	public HashSet<String> getStudents(){
		return this.students;
	}
	public int getNumThreads(){
		return this.numThreads;
	}
	public ArrayList<String> getAcceptedCodeFiles(){
		return this.acceptedCodeFiles;
	}
	public ArrayList<String> getAcceptedReadmeFiles(){
		return this.acceptedReadmeFiles;
	}

	public void verifyInputs() throws AutoGraderException {
		this.numThreads=Math.max(1,Math.min(10,this.numThreads));
		boolean inputsOk=this.requiredInputsProvided();
		if (this.zippedSubmissions!=null){
			inputsOk&=Files.exists(Paths.get(this.zippedSubmissions));
		}
		if (this.gradingScript!=null && this.gradingScript.length()>0){
			inputsOk&=Files.exists(Paths.get(this.gradingScript));
		}
		if (!inputsOk){
			throw new AutoGraderException(
				"The HWData file is incorrectly formatted or the submissions file DNE.");
		}
	}

	private boolean requiredInputsProvided(){
		return (this.zippedSubmissions!=null && 
			this.cleanedSubmissionsDir!=null &&
			this.gradebook!=null &&
			this.acceptedCodeFiles!=null &&
			this.acceptedReadmeFiles!=null &&
			this.numThreads!=0);
	}

	public boolean needsCleaning(){
		return (this.cleanedSubmissionsDir!=null && 
			!Files.isDirectory(Paths.get(this.cleanedSubmissionsDir)));
	}
}
