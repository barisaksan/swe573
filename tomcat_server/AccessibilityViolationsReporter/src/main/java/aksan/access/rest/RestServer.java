package aksan.access.rest;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSDBFile;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.bson.types.ObjectId;
import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;
import org.json.simple.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.StreamingOutput;
import java.io.IOException;
import java.io.OutputStream;
import java.net.UnknownHostException;
import java.util.List;

@Path("/")
public class RestServer {
    MongoClient mongo;
    DB db;
    HierarchicalINIConfiguration config;

    public RestServer() throws UnknownHostException, ConfigurationException {
        config = new HierarchicalINIConfiguration("config.ini");
        String dbHost = config.getSection("database").getString("host");
        int dbPort = config.getSection("database").getInt("port");
        String dbName = config.getSection("database").getString("name");

        mongo = new MongoClient(dbHost, dbPort);
        db = mongo.getDB(dbName);
    }

    @GET
	@Path("/violations")
	public Response getViolations() throws UnknownHostException {

        DBCollection collection = db.getCollection("violations");
        DBCursor results = collection.find();
        results.sort(new BasicDBObject("uploadDate", 1));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
	}

    @GET
    @Path("/violations/{id}")
    public Response getViolations(@PathParam("id") String id) throws UnknownHostException {

        try {
            DBCollection collection = db.getCollection("violations");
            DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId(id)));
            results.sort(new BasicDBObject("uploadDate", 1));
            String serialize = JSON.serialize(results);
            return Response.status(200).entity(serialize).build();
        }
        catch (Exception e) {
            return Response.status(200).entity(new JSONArray().toString()).build();
        }
    }

    @GET
    @Path("/violations/top")
    public Response getViolationsSearch() throws UnknownHostException {

        try {
            DBCollection collection = db.getCollection("violations");
            DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId("top")));
            results.sort(new BasicDBObject("uploadDate", 1));
            String serialize = JSON.serialize(results);
            return Response.status(200).entity(serialize).build();
        }
        catch (Exception e) {
            return Response.status(200).entity(new JSONArray().toString()).build();
        }
    }

    @GET
    @Path("/violations/search/{keyword}")
    public Response getViolationsSearch(@PathParam("keyword") String keyword) throws UnknownHostException {

        try {
            DBCollection collection = db.getCollection("violations");
            DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId(keyword)));
            results.sort(new BasicDBObject("uploadDate", 1));
            String serialize = JSON.serialize(results);
            return Response.status(200).entity(serialize).build();
        }
        catch (Exception e) {
            return Response.status(200).entity(new JSONArray().toString()).build();
        }
    }

    @GET
    @Path("/violations/nearby/{location}")
    public Response getViolationsNearby(@PathParam("location") String location) throws UnknownHostException {

        try {
            DBCollection collection = db.getCollection("violations");
            DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId(location)));
            results.sort(new BasicDBObject("uploadDate", 1));
            String serialize = JSON.serialize(results);
            return Response.status(200).entity(serialize).build();
        }
        catch (Exception e) {
            return Response.status(200).entity(new JSONArray().toString()).build();
        }
    }

    @GET
    @Path("/violations/{id}/comments")
    public Response getCommentsByViolation(@PathParam("id") String id) throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find(new BasicDBObject("violation_id", id));
        results.sort(new BasicDBObject("uploadDate", 1));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/violations/{id}/ratings")
    public Response getRatingsByViolation(@PathParam("id") String id) throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find(new BasicDBObject("violation_id", id));
        results.sort(new BasicDBObject("uploadDate", 1));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/violations/{id}/photos")
    @Produces("image/jpeg")
    public Response getPhotosByViolation(@PathParam("id") String violationId) throws UnknownHostException {
        GridFS gfsPhoto = new GridFS(db, "photos");
        final List<GridFSDBFile> photos = gfsPhoto.find(violationId);
        StreamingOutput streamingOutput = new StreamingOutput() {
            public void write(OutputStream output) throws IOException, WebApplicationException {
                try {
                    photos.get(0).writeTo(output);
                } catch (Exception e) {
                    throw new WebApplicationException(e);
                }
            }
        };

        return Response.status(200).entity(streamingOutput).build();
    }

    @POST
    @Path("/violations/{id}/new_photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postViolationsImage(
            //@FormDataParam("file") InputStream uploadedInputStream,
            //@FormDataParam("file") FormDataContentDisposition fileDetail
            @PathParam("id") String violationId,
            String photo) {
        GridFS gfsPhoto = new GridFS(db, "photos");
        GridFSInputFile gfsFile = gfsPhoto.createFile(photo.getBytes());
        gfsFile.setFilename(violationId);
        gfsFile.save();

        String output = "File uploaded to database : " + "photo/" + violationId;
        return Response.status(200).entity(output).build();
    }

	@POST
    @Path("/violations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postViolations(Violation newViolation) {
        try {
            DBCollection collection = db.getCollection("violations");
            ObjectMapper mapper = new ObjectMapper();
            DBObject dbo = (DBObject) JSON.parse(mapper.writeValueAsString(newViolation));
            collection.insert(dbo);
            DBObject o = db.getCollection("violations").findOne(dbo);
            return Response.status(201).entity(o.get("_id").toString()).build();
        } catch (MongoException e) {
            e.printStackTrace();
        } catch (JsonGenerationException e) {
            e.printStackTrace();
        } catch (JsonMappingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return Response.status(200).entity("ERROR").build();
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
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/users/{username}")
    public Response getUsers(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("users");
        DBCursor results = collection.find(new BasicDBObject("username", msg));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/comments")
    public Response getComments() throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find();
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/comments/{username}")
    public Response getComments(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("comments");
        DBCursor results = collection.find(new BasicDBObject("user", msg));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/ratings")
    public Response getRatings() throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find();
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/ratings/{username}")
    public Response getRatings(@PathParam("username") String msg) throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find(new BasicDBObject("user", msg));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();    }
}