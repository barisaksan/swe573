package aksan.access.rest;

import com.mongodb.*;
import com.mongodb.util.JSON;
import org.bson.types.ObjectId;
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

@Path("/")
public class RestServer {
    MongoClient mongo;
    DB db;

    public RestServer() throws UnknownHostException {
        mongo = new MongoClient("localhost", 27017);
        db = mongo.getDB("access");
    }

    @GET
	@Path("/violations")
	public Response getViolations() throws UnknownHostException {

        DBCollection collection = db.getCollection("violations");
        DBCursor results = collection.find();
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
	}

    @GET
    @Path("/violations/{id}")
    public Response getViolations(@PathParam("id") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("violations");
        DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId(msg)));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/violations/{id}/comments")
    public Response getCommentsByViolation(@PathParam("id") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find(new BasicDBObject("violation_id", msg));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/violations/{id}/ratings")
    public Response getRatingsByViolation(@PathParam("id") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find(new BasicDBObject("violation_id", msg));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

	@POST
    @Path("/violations/{id}")
    public Response postViolations(@PathParam("id") String msg) {
        String output = "POST";
        try {
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
    @Path("/violations/{id}")
    public Response putViolations(@PathParam("id") String msg) {
 
		String output = "Jersey put : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
	@DELETE
    @Path("/violations/{id}")
    public Response deleteViolations(@PathParam("id") String msg) {
 
		String output = "Jersey delete : " + msg;
 
		return Response.status(200).entity(output).build();
	}

    @GET
    @Path("/users")
    public Response getUsers() throws UnknownHostException {

        DBCollection collection = db.getCollection("users");
        DBCursor results = collection.find();
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/users/{username}")
    public Response getUsers(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("users");
        DBCursor results = collection.find(new BasicDBObject("username", msg));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/comments")
    public Response getComments() throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find();
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/comments/{username}")
    public Response getComments(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find(new BasicDBObject("user", msg));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/ratings")
    public Response getRatings() throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find();
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/ratings/{username}")
    public Response getRatings(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find(new BasicDBObject("user", msg));
        JSON json = new JSON();
        String serialize = json.serialize(results);
        return Response.status(200).entity(serialize).build();    }
}