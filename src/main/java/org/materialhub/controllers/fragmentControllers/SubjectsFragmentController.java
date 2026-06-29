package org.materialhub.controllers.fragmentControllers;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/{collegeCode}/year/{yearId}/specialization/{specializationID}")
public class SubjectsFragmentController {

    @Path("/subjects") 
    @GET 
    public void subjects(String collegeCode , Long yearId , Long specializationID ){

    }

}
