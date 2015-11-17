package aksan.access.rest;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import javax.ws.rs.*;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

@Path("/violations")
public class RestServer {
 
	@GET
	@Path("/{param}")
	public Response getViolations(@PathParam("param") String msg) throws UnknownHostException {

        MongoClient mongo = new MongoClient("localhost", 27017);
        DB db = mongo.getDB("access");
        DBCollection collection = db.getCollection("violations");

        DBCursor results = collection.find(new BasicDBObject("reporter", msg));

        //DBCursor cursor = collection.find(new BasicDBObject("_id", msg));

		//return Response.status(200).entity(Integer.toString(results.size())).build();
        return Response.status(200).entity(results.next().toString()).build();
	}

	@POST
	public Response postViolations(@PathParam("param") String msg) {
        String output = "POST";
        try {
            MongoClient mongo = new MongoClient("localhost", 27017);
            DB db = mongo.getDB("access");
            DBCollection collection = db.getCollection("violations");

            FileReader reader = new FileReader("C:\\Users\\baris\\Desktop\\swe573\\swe573\\tomcat_server\\AccessibilityViolationsReporter\\src\\main\\java\\aksan\\access\\rest\\violations.json");
            final JSONParser parser = new JSONParser();
            final JSONObject json = (JSONObject) parser.parse(reader);

            DBObject dbo = (DBObject) JSON.parse(json.toString());
            List<DBObject> list = new ArrayList<DBObject>();
            list.add(dbo);
            collection.insert(list);

        } catch (UnknownHostException e) {
            e.printStackTrace();
            output = "ERROR";
        } catch (MongoException e) {
            e.printStackTrace();
            output = "ERROR";
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            output = "ERROR";
        } catch (ParseException e) {
            e.printStackTrace();
            output = "ERROR";
        } catch (IOException e) {
            e.printStackTrace();
            output = "ERROR";
        }

        return Response.status(200).entity(output).build();
    }
	
	@PUT
	public Response putViolations(@PathParam("param") String msg) {
 
		String output = "Jersey put : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
	@DELETE
	public Response deleteViolations(@PathParam("param") String msg) {
 
		String output = "Jersey delete : " + msg;
 
		return Response.status(200).entity(output).build();
	}
}