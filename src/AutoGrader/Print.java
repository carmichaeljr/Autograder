package AutoGrader;

public class Print {
	private static final int LINE_LENGTH=120;
	private static final int PROGRESS_BAR_LENGTH=50;
	private static final String WARNING_PROMPT="!Warning!: ";

	public static void line(String line, char end){
		System.out.print(line);
		Print.fillLine(Print.LINE_LENGTH-line.length());
		System.out.print(end);
	}
	public static void line(String line){
		Print.line(line,'\n');
	}

	public static void warning(String message){
		System.out.print(Print.WARNING_PROMPT+message);
		Print.fillLine(Print.LINE_LENGTH-Print.WARNING_PROMPT.length()-message.length());
		System.out.println();
	}

	public static void progressBar(int iter, int maxIter, String desc){
		int fullBar=(int)(((float)iter)/maxIter*Print.PROGRESS_BAR_LENGTH);
		int blankBar=50-fullBar;
		System.out.print(String.format("<%s%s> %3.2f%% | %s",
					"#".repeat(fullBar),
					"=".repeat(blankBar),
					((float)iter)/maxIter*100,
					desc));
		Print.fillLine(Print.LINE_LENGTH-PROGRESS_BAR_LENGTH-desc.length()-13);
		System.out.print('\r');
	}

	private static void fillLineWith(int numChars, char _char){
		for (int i=0; i<numChars; System.out.print(_char), i++);
	}
	private static void fillLine(int numChars){
		Print.fillLineWith(numChars, '.');
	}
}
