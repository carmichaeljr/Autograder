package AutoGrader;

import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import com.google.gson.annotations.SerializedName;

class CommentTest extends Test {
	@SerializedName("percentage")
	private float percentage;
	@SerializedName("symbol")
	private String symbol;

	public CommentTest(float points, String comment, float percentage, String symbol){
		super(points,comment);
		this.percentage=percentage;
		this.symbol=symbol;
	}

	public float getPercentage(){
		return this.percentage;
	}
	public String getSymbol(){
		return this.symbol;
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student) throws AutoGraderException{
		try {
			float percentage=this.getCommentPercentage(super.getTestFile(student));
			return (percentage>=this.percentage)? new Pair<Float,String>(super.points,""):
						  new Pair<Float,String>((float)0.0,super.comment);
		} catch (IOException e){
			return super.gradingErrorFallback("CommentTest",student);
		}
	}

	private float getCommentPercentage(String file) throws IOException {
		int comments=0, lines=0;
		if (file!=null){
			try (BufferedReader br=new BufferedReader(new FileReader(file))){
				for (String line; (line=br.readLine())!=null;){
					if (line.length()>0){
						comments=(line.contains(this.symbol))? ++comments: comments;
						lines++;
					}
				}
			} catch (IOException e){
				throw e;
			}
		}
		return (((float)comments)/lines)*100;
	}
}

