package AutoGrader;

import java.util.HashMap;
import java.util.ArrayList;
import java.lang.StringBuilder;

import com.google.gson.annotations.SerializedName;

import Common.ProcessCommunication;

class ExecuteTest extends Test {
	@SerializedName("removeWhitespace")
	private boolean removeWhiteSpace;
	@SerializedName("matchCase")
	private boolean matchCase;
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
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student) throws AutoGraderException {
		String progOutput=this.runProgram(rarsProcRef,super.getTestFiles(student));
		if (progOutput!=null){
			//Print.line("PROG OUTPUT: "+progOutput);
			Pair<Float,String> rv=this.checkForErrorCodes(progOutput);
			rv=(rv==null)? this.checkForOutputs(progOutput): rv;
			return rv;
		} else {
			//Print.line("No output");
			return new Pair<Float,String>((float)0.0,"Code did not have any output.");
		}
	}

	private String runProgram(RarsProcManager rarsProcRef, ArrayList<String> submissionFiles) throws AutoGraderException {
		String rv="";
		if (submissionFiles!=null && submissionFiles.size()>0){
			ArrayList<String> files=new ArrayList<String>();
			if (Settings.getHWData().getGradingScript()!=null){
				files.add(Settings.getHWData().getGradingScript());
			}
			for (int i=0; i<submissionFiles.size(); i++){
				files.add(submissionFiles.get(i));
			}
			//System.out.println("Submission Files: ");
			//for (int i=0; i<files.size(); i++){
			//	System.out.println(files.get(i));
			//}
			rv=rarsProcRef.pipe(files,this.inputs,this.regVals.keySet());
		}
		return rv;
	}

	private Pair<Float,String> checkForErrorCodes(String progOutput){
		if (progOutput.contains(ProcessCommunication.ASSEMBLY_ERROR)){
			return new Pair<Float,String>((float)0.0,"Code did not assemble.");
		} else if (progOutput.contains(ProcessCommunication.SIMULATION_ERROR)){
			return new Pair<Float,String>((float)0.0,"Code had an error during execution.");
		} else if (progOutput.contains(ProcessCommunication.INFINITE_LOOP)){
			return new Pair<Float,String>((float)0.0,"Code had an infinite loop during execution.");
		}
		return null;
	}

	private Pair<Float,String> checkForOutputs(String progOutput){
		//TODO - implement register checking
		if (this.removeWhiteSpace){	
			progOutput=progOutput.replaceAll("\\s+","");
		}
		if (!this.matchCase){
			progOutput=progOutput.toLowerCase();
		}
		boolean someOutputs=this.outputConditional.equalsIgnoreCase("or");
		boolean result=!someOutputs;
		for (int i=0; i<this.outputs.size(); i++){
			String expected=(this.matchCase)? this.outputs.get(i): this.outputs.get(i).toLowerCase();
			boolean iterResult=(someOutputs)? progOutput.contains(expected): !progOutput.contains(expected);
			if (iterResult){
				result=(someOutputs)? (iterResult || true): (iterResult && false);
			}
		}
		if (result){
			return new Pair<Float,String>(super.points,"");
		}
		return new Pair<Float,String>((float)0.0,this.formatComment());
	}

	private String formatComment(){
		StringBuilder builder=new StringBuilder("Program did not output ");
		this.generateMessagePart(builder,this.outputs,this.outputConditional);
		builder.append(" given inputs of ");
		this.generateMessagePart(builder,this.inputs,"and");
		builder.append(".");
		return builder.toString();
	}

	private void generateMessagePart(StringBuilder builder, ArrayList<String> vals, String conditional){
		for (int i=0; i<vals.size(); i++){
			if (vals.get(i).equalsIgnoreCase("\n")){
				builder.append("\\n");
			} else {
				builder.append(vals.get(i));
			}
			if (i==vals.size()-2){
				builder.append(", ");
				builder.append(conditional);
				builder.append(" ");
			} else if (i<vals.size()-2){
				builder.append(", ");
			}
		}
	}
}
