import java.util.HashMap;
import java.util.ArrayList;

import com.google.gson.annotations.SerializedName;

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
	private static final String ASSEMBLY_ERROR="Error: Assembly";
	private static final String SIMULATION_ERROR="Error: Simulation";
	private static final String INFINITE_LOOP="\nError: InfiniteLoop";
	//TODO - move common strings for process talk to separate class

	public ExecuteTest(float points, boolean removeWhiteSpace, String outputConditional){
		super(points,"");
		this.removeWhiteSpace=removeWhiteSpace;
		this.outputConditional=outputConditional;
		this.inputs=new ArrayList<String>();
		this.outputs=new ArrayList<String>();
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student){
		String rv=this.runProgram(rarsProcRef,super.getTestFile(student));
		Pair<Float,String> rv=this.checkForErrorCodes(rv);
		rv=(rv!=null)? this.checkForOutputs(rv): rv;
		return rv;
	}

	private String runProgram(RarsProcManager rarsProcRef, String submissionFile){
		String rv="";
		if (submissionFile!=null){
			ArrayList<String> files=new ArrayList<String>();
			//TODO - check for grading script file
			files.add(submissionFile);
			rv=rarsProcRef.pipe(files,this.inputs,this.regVals.keySet());
			System.out.println(rv);
		}
		return rv;
	}

	private Pair<Float,String> checkForErrorCodes(String progOutput){
		if (rv.contains(ExecuteTest.ASSEMBLY_ERROR)){
			return new Pair<Float,String>((float)0.0,"Code did not assemble.");
		} else if (rv.contains(ExecuteTest.SIMULATION_ERROR)){
			return new Pair<Float,String>((float)0.0,"Code had an error during execution.");
		} else if (rv.contains(ExecuteTest.INFINITE_LOOP)){
			return new Pair<Float,String>((float)0.0,"Code had an infinite loop during execution.");
		}
		return null;
	}
}

