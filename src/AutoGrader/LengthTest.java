package AutoGrader;

import java.nio.file.Files;
import java.nio.file.Paths;
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
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student){
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
			rv=Files.lines(Paths.get(file)).count();
		}
		return rv;
	}
}

