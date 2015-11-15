package aksan.access.rest;
 
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.PUT;
import javax.ws.rs.DELETE;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;
 
@Path("/access")
public class RestServer {
 
	@GET
	@Path("/{violations}")
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Jersey get : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
	@POST
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Jersey post : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
	@PUT
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Jersey put : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
	@DELETE
	public Response getMsg(@PathParam("param") String msg) {
 
		String output = "Jersey delete : " + msg;
 
		return Response.status(200).entity(output).build();
	}
	
}