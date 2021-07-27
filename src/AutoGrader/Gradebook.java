package AutoGrader;

import java.util.Map;
import java.util.Arrays;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.Collections;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.StringBuilder;

class Gradebook {
	private Map<String,Pair<Float,ArrayList<String>>> grades;
	private static final Gradebook inst=new Gradebook();
	
	public static void load(){
		if (Files.exists(Paths.get(Settings.getHWData().getGradebook()))){
			Gradebook.inst.parseCSVFile();
			//for (String iter: Gradebook.inst.grades.keySet()){
			//	System.out.println(iter+": "+Gradebook.inst.grades.get(iter));
			//}
		}
	}

	public static void record(String name, Pair<Float,ArrayList<String>> data){
		Gradebook.inst.grades.put(name,data);
	}
	public static void record(String name, float score, ArrayList<String> comments){
		Gradebook.record(name,new Pair<Float,ArrayList<String>>(score,comments));
	}

	public static void save(){
		//for (String iter: Gradebook.inst.grades.keySet()){
		//	System.out.println(iter+": "+Gradebook.inst.grades.get(iter));
		//}
		Gradebook.inst.saveCSVFile();
	}

	private Gradebook(){
		this.grades=Collections.synchronizedMap(new HashMap<String,Pair<Float,ArrayList<String>>>());
	}

	private void parseCSVFile(){
		File file=new File(Settings.getHWData().getGradebook());
		try (FileReader fr=new FileReader(file)){
			boolean inQuotes=false;
			StringBuilder builder=new StringBuilder();
			ArrayList<String> separations=new ArrayList<String>();
			for (int content=0; (content=fr.read())!=-1; ){
				char iterChar=(char)content;
				if (!inQuotes && builder.length()>0 &&
				    (iterChar==',' || iterChar=='\n')){
					separations.add(builder.toString());	
					builder.setLength(0);
				} else if (iterChar=='\"'){
					inQuotes=!inQuotes;
					builder.append(iterChar);
				} else {
					builder.append(iterChar);
				}
			}
			this.populateGrades(separations);
		} catch (IOException e){
			Print.warning("An error occurred parsing the supplied gradebook.");
		}
	}

	private void populateGrades(ArrayList<String> separations){
		for (int i=0, j=0; i<separations.size(); i++, j++, j%=3){
			separations.set(i,separations.get(i).replaceAll("^\"+|\"+$",""));
			if (j==2){
				Float points=new Float(separations.get(i-1));
				ArrayList<String> comments=new ArrayList<String>(Arrays.asList(separations.get(i).split("\n")));
				this.grades.put(separations.get(i-2),
						new Pair<Float,ArrayList<String>>(points,comments));
			}
		}
	}

	private void saveCSVFile(){
		try (FileWriter fw=new FileWriter(Settings.getHWData().getGradebook())){
			for (String iter: Gradebook.inst.grades.keySet()){
				fw.write(this.generateLine(iter));
			}
		} catch (IOException e){
			Print.warning("An error occurred saving the students grades.");
		}
	}

	private String generateLine(String student){
		StringBuilder builder=new StringBuilder(student);
		builder.append(",");
		Pair<Float,ArrayList<String>> val=this.grades.get(student);
		builder.append(val.getKey().toString());
		builder.append(",\"");
		for (int i=0; i<val.getValue().size(); i++){
			builder.append(val.getValue().get(i));
			builder.append("\n");
		}
		builder.append("\"\n");
		return builder.toString();
	}
}
