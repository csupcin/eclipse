package personalityTest;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import jakarta.ws.rs.GET;
import jakarta.ws.rs.POST;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;

@Path("/personality")

public class PersonalityService {

	Operations op = new Operations();

	// returning JSONArray to front-end
	@GET
	@Path("/info")
	@Produces(MediaType.APPLICATION_JSON)
	public JSONObject getInfo() {
		return op.readJson();
	}

	// controlling jsonString and giving response
	@POST
	@Path("/save")
	@Produces(MediaType.APPLICATION_JSON)
	public String check(String info) {
		if (op.checkJsonString(info)) {
			return "Your test succesfully completed.";
		} else {
			return "There is an error occured.";
		}
	}
}
