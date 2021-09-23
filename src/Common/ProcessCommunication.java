package Common;

import java.io.File;

public class ProcessCommunication {
	public static final String ASSEMBLY_ERROR="Error: Assembly";
	public static final String SIMULATION_ERROR="Error: Simulation";
	public static final String INFINITE_LOOP="Error: InfiniteLoop";
	public static final String SIM_COMPLETE="SimulationComplete";
	public static final String PROC_START=String.format(
			"java -cp \"%1$s/dependencies/RarsProc.jar;%1$s/dependencies/rars.jar\" RarsProc.RarsProc",
			new File(ClassLoader.getSystemClassLoader().getResource(".").getPath()).getAbsolutePath());
}
