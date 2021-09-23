package AutoGrader;

//import java.nio.file.Files;
//import java.nio.file.Paths;
//import java.nio.charset.Charset;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import com.google.gson.annotations.SerializedName;

class LengthTest extends Test {
	@SerializedName("minLines")
	private long minLines;

	public LengthTest(float points, String comment){
		super(points,comment);
		this.minLines=0;
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student) throws AutoGraderException{
		try {
			long lines=this.countLines(super.getTestFile(student));
			return (lines>=this.minLines)? new Pair<Float,String>(super.points,""):
						new Pair<Float,String>((float)0.0,super.comment);
		} catch (IOException e){
			return super.gradingErrorFallback("LengthTest",student);
		}
	}

	private long countLines(String file) throws IOException {
		long rv=-1;
		if (file!=null){
			rv=0;
			BufferedReader reader=new BufferedReader(new FileReader(file));
			while (reader.readLine()!=null) rv++;
			reader.close();
			//rv=Files.lines(Paths.get(file),Charset.forName("Cp1252")).count();
		}
		return rv;
	}
}

