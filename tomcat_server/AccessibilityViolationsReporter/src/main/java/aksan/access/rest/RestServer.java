package aksan.access.rest;

import com.mongodb.*;
import com.mongodb.gridfs.GridFS;
import com.mongodb.gridfs.GridFSInputFile;
import com.mongodb.util.JSON;
import com.sun.jersey.core.header.FormDataContentDisposition;
import com.sun.jersey.multipart.FormDataParam;
import org.apache.commons.configuration.ConfigurationException;
import org.apache.commons.configuration.HierarchicalINIConfiguration;
import org.bson.types.ObjectId;
import org.json.simple.JSONArray;

import javax.ws.rs.*;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.*;
import java.net.UnknownHostException;
import java.util.ArrayList;
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
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
	}

    @GET
    @Path("/violations/{id}")
    public Response getViolations(@PathParam("id") String id) throws UnknownHostException {

        try {
            DBCollection collection = db.getCollection("violations");
            DBCursor results = collection.find(new BasicDBObject("_id", new ObjectId(id)));
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
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

    @GET
    @Path("/violations/{id}/ratings")
    public Response getRatingsByViolation(@PathParam("id") String id) throws UnknownHostException {

        DBCollection collection = db.getCollection("ratings");
        DBCursor results = collection.find(new BasicDBObject("violation_id", id));
        String serialize = JSON.serialize(results);
        return Response.status(200).entity(serialize).build();
    }

	@POST
    @Path("/violations")
    @Consumes(MediaType.APPLICATION_JSON)
    public Response postViolations(String newViolation) {
        String output = "POST";
        try {
            DBCollection collection = db.getCollection("violations");
            DBObject dbo = (DBObject) JSON.parse(newViolation.toString());
            List<DBObject> list = new ArrayList<DBObject>();
            list.add(dbo);
            collection.insert(list);
            String result = "Violation saved : " + newViolation;
            return Response.status(201).entity(result).build();
        } catch (MongoException e) {
            e.printStackTrace();
            output = "ERROR";
        }

        return Response.status(200).entity(output).build();
    }

    @POST
    @Path("/violations/{id}/new_photo")
    @Consumes(MediaType.MULTIPART_FORM_DATA)
    public Response postViolationsImage(
            @FormDataParam("file") InputStream uploadedInputStream,
            @FormDataParam("file") FormDataContentDisposition fileDetail,
            @PathParam("id") String id) {
        GridFS gfsPhoto = new GridFS(db, "photo");
        GridFSInputFile gfsFile = gfsPhoto.createFile(uploadedInputStream);
        gfsFile.setFilename(id);
        gfsFile.save();

        String output = "File uploaded to database : " + "photo/" + id;
        return Response.status(200).entity(output).build();
    }

    // save uploaded file to new location
    private void writeToFile(InputStream uploadedInputStream,
                             String uploadedFileLocation) {
        try {
            OutputStream out = new FileOutputStream(new File(
                    uploadedFileLocation));
            int read = 0;
            byte[] bytes = new byte[1024];

            out = new FileOutputStream(new File(uploadedFileLocation));
            while ((read = uploadedInputStream.read(bytes)) != -1) {
                out.write(bytes, 0, read);
            }
            out.flush();
            out.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
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