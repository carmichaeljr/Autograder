import java.util.Scanner;
import java.util.ArrayList;

import rars.ErrorList;
import rars.api.Options;
import rars.api.Program;
import rars.AssemblyException;
import rars.SimulationException;
import rars.simulator.Simulator;

public class RarsProc {
	private Options opts;
	private Program prog;
	private String progOutput;
	private ArrayList<String> inputs;
	private static final int MAX_NUM_INSTRS=1000000;
	private static final String ASSEMBLY_ERROR="Error: Assembly";
	private static final String SIMULATION_ERROR="Error: Simulation";
	private static final String INFINITE_LOOP="\nError: InfiniteLoop";
	private static final RarsProc inst=new RarsProc();

	public static void start(){
		Scanner scanner=new Scanner(System.in);
		for (boolean cont=true; cont; cont=RarsProc.inst.procNeeded(), RarsProc.inst.inputs.clear()){
			while (!RarsProc.inst.inputsEnded() &&
			       RarsProc.inst.procNeeded()){
				RarsProc.inst.inputs.add(scanner.nextLine());
			}
			if (RarsProc.inst.procNeeded()){
				RarsProc.inst.runSimulation();			
				RarsProc.inst.printResults();
			}
		}
		scanner.close();
	}

	private RarsProc(){
		this.opts=new Options();
		this.opts.pseudo=true;
		this.opts.warningsAreErrors=false;
		this.opts.startAtMain=false;
		this.opts.selfModifyingCode=false;
		this.opts.maxSteps=RarsProc.MAX_NUM_INSTRS;
		this.prog=new Program(this.opts);
		this.progOutput="";
		this.inputs=new ArrayList<String>();
	}

	private boolean procNeeded(){
		if (this.inputs.size()>0){
			return !this.inputs.get(this.inputs.size()-1).equalsIgnoreCase("done");
		}
		return true;
	}
	private boolean inputsEnded(){
		return (this.inputs.size()>0 && this.inputs.get(this.inputs.size()-1).equalsIgnoreCase("end"));
	}

	private void runSimulation(){
		this.progOutput="";
		if (this.assembleProgram()){
			this.prog.setup(new ArrayList<String>(),this.getSTDINFromInput());
			this.executeProgram();
		}
	}

	private boolean assembleProgram(){
		boolean rv=true;
		ArrayList<String> files=this.getFilesFromInput();
		try {
			this.prog.assemble(files,files.get(0));
		} catch (AssemblyException e){
			this.progOutput=RarsProc.ASSEMBLY_ERROR;
			rv=false;
		}
		return rv;
	}

	private void executeProgram(){
		Simulator.Reason reason=null;
		try {
			reason=this.prog.simulate();
		} catch (SimulationException e){
			this.progOutput=RarsProc.SIMULATION_ERROR;
		}
		this.progOutput=this.prog.getSTDOUT();
		if (reason!=null && reason==Simulator.Reason.MAX_STEPS){
			this.progOutput+=RarsProc.INFINITE_LOOP;
		}
	}

	private void printResults(){
		System.out.println(this.progOutput);
		this.printRegisters();
	}

	private void printRegisters(){
		ArrayList<String> regs=this.getRegistersFromInput();
		for (int i=0; i<regs.size(); i++){
			try {
				int regVal=this.prog.getRegisterValue(regs.get(i));
				System.out.println(String.format("\nREG_VALUE:%s:%s",regs.get(i),regVal));
			} catch (NullPointerException e){
				
			}
		}
	}

	private ArrayList<String> getFilesFromInput(){
		return this.separateInputs("Files");
	}
	private ArrayList<String> getRegistersFromInput(){
		return this.separateInputs("Registers");
	}
	private String getSTDINFromInput(){
		ArrayList<String> temp= this.separateInputs("STDIN");
		String rv=temp.get(0);
		for (int i=1; i<temp.size(); i++){
			rv=String.format("%s\n%s",rv,(temp.get(i).length()>0)? temp.get(i): "\n");
		}
		return rv;
	}
	private ArrayList<String> separateInputs(String flagName){
		String start=String.format("begin%s",flagName);
		String end=String.format("end%s",flagName);
		ArrayList<String> rv=new ArrayList<String>();
		int i=0;
		for (; i<this.inputs.size() && !this.inputs.get(i).equalsIgnoreCase(start); i++);
		for (i++; i<this.inputs.size() && !this.inputs.get(i).equalsIgnoreCase(end); rv.add(this.inputs.get(i)), i++);
		return rv;
	}

	public static void main(String[] args){
		RarsProc.start();
	}
}
