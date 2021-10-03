package AutoGrader;

import java.util.HashSet;
import java.io.FileReader;
import java.io.IOException;
import java.io.BufferedReader;

import com.google.gson.annotations.SerializedName;

class KeywordTest extends Test {
	@SerializedName("takeAwayPoints")
	private boolean takeAwayPoints;
	@SerializedName("onePointPer")
	private boolean onePointPer;
	@SerializedName("minNumOccurances")
	private int minNumOccurances;
	@SerializedName("words")
	private HashSet<String> words;

	public KeywordTest(float points, String comment){
		super(points,comment);
		this.takeAwayPoints=false;
		this.onePointPer=false;
		this.words=new HashSet<String>();
	}

	@Override
	public Pair<Float,String> run(RarsProcManager rarsProcRef, String student) throws AutoGraderException{
		try {
			return this.awardPoints(this.getNumOccurances(super.getTestFile(student)));
		} catch (IOException e){
			return super.gradingErrorFallback("KeywordTest",student);
		}
	}

	private int getNumOccurances(String file) throws IOException {
		int rv=0;
		if (file!=null){
			try (BufferedReader br=new BufferedReader(new FileReader(file))){
				for (String line; (line=br.readLine())!=null;){
					rv+=this.getNumOccurancesInLine(line);
				}
			} catch (IOException e){
				throw e;
			}
		}
		return rv;
	}

	private int getNumOccurancesInLine(String line){
		int rv=0;
		String[] splitLine=line.split(" |\\?|,|;|:|\"|'|\\(|\\)|\\[|\\]|\\{|\\}|;");
		for (int i=0; i<splitLine.length; i++){
			rv=(this.words.contains(splitLine[i].toLowerCase()))? ++rv: rv;
		}
		return rv;
	}

	private Pair<Float,String> awardPoints(int numOccurances){
		if (this.minNumOccurances<=numOccurances){
			if (this.onePointPer && this.takeAwayPoints){
				//Min num keywords met, take one point away per keyword
				float points=Math.max(super.points-numOccurances,0);
				String comment=(points==super.points)? "": super.comment;
				return new Pair<Float,String>(points,comment);
			} else if (this.onePointPer && !this.takeAwayPoints){
				//Min num keywords met, give one point per keyword
				return new Pair<Float,String>(Math.min(super.points,numOccurances),"");
			} else if (!this.onePointPer && !this.takeAwayPoints){
				//Min num keywords met, give full points
				return new Pair<Float,String>(super.points,"");
			} else if (!this.onePointPer && this.takeAwayPoints){
				//Min num keywords met, give no points
				return new Pair<Float,String>((float)0.0,super.comment);
			}
		}
		//Min num keywords not met, give no points
		return new Pair<Float,String>((float)0.0,super.comment);
	}
}

