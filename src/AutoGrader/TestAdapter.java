package AutoGrader;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonElement;
import com.google.gson.TypeAdapter;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

class TestAdapter extends TypeAdapter<Test> {
	@Override
	public Test read(JsonReader reader) throws IOException {
		Test rv=null;
		Gson gson=new Gson();
		JsonParser jp=new JsonParser();
		JsonObject jo=(JsonObject)jp.parse(reader);
		JsonElement je=null;
		if ((je=jo.get("lengthTest"))!=null){
			rv=gson.fromJson(je,LengthTest.class);
		} else if ((je=jo.get("commentTest"))!=null){
			rv=gson.fromJson(je,CommentTest.class);
		} else if ((je=jo.get("keywordTest"))!=null){
			rv=gson.fromJson(je,KeywordTest.class);
		} else if ((je=jo.get("executeTest"))!=null){
			rv=gson.fromJson(je,ExecuteTest.class);
		}
		return rv;
	}

	@Override
	public void write(JsonWriter out, Test item) throws IOException {
		throw new IllegalArgumentException("TypeAdapter.write method not implemented!");
	}
}
