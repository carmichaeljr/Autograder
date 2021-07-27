package AutoGrader;

import java.util.Set;
import java.util.ArrayList;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.lang.StringBuilder;

import Common.ProcessCommunication;

class RarsProcManager {
	private Process proc;
	private BufferedReader in;
	private BufferedWriter out;

	public RarsProcManager(){
		try {
			this.proc=Runtime.getRuntime().exec(ProcessCommunication.PROC_START);
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

	public String pipe(String message){
		String rv=null;
		try {
			this.out.write(message);
			this.out.flush();
			StringBuilder builder=new StringBuilder();
			for (String line; 
				(line=this.in.readLine())!=null && !line.equalsIgnoreCase(ProcessCommunication.SIM_COMPLETE);
				builder.append(line));
			rv=builder.toString();
		} catch (IOException e){
			Print.warning("An error occurred writing to a RARS process.");
		}
		return rv;
	}
	public String pipe(ArrayList<String> files, ArrayList<String> stdin, Set<String> registers) {
		StringBuilder builder=new StringBuilder();
		this.generateMessagePart(builder,files,"Files");
		this.generateMessagePart(builder,stdin,"STDIN");
		this.generateMessagePart(builder,registers,"Registers");
		builder.append("end\n");
		return this.pipe(builder.toString());
	}

	private void generateMessagePart(StringBuilder dest, ArrayList<String> vals, String desc){
		dest.append("begin");
		dest.append(desc);
		dest.append("\n");
		for (int i=0; i<vals.size(); i++){
			dest.append(vals.get(i));
			dest.append("\n");
		}
		dest.append("end");
		dest.append(desc);
		dest.append("\n");
	}
	private void generateMessagePart(StringBuilder dest, Set<String> vals, String desc){
		dest.append("begin");
		dest.append(desc);
		dest.append("\n");
		for (String iterVal: vals){
			dest.append(iterVal);
			dest.append("\n");
		}
		dest.append("end");
		dest.append(desc);
		dest.append("\n");
	}

	public void destroy(){
		this.pipeNoResponse("done");
		try {
			this.in.close();
			this.out.close();
		} catch (IOException e){
			System.out.println(e);
		}
		this.proc.destroy();
	}
}

