package org.materialhub.controllers;

import io.quarkus.qute.Template;
import io.quarkus.qute.TemplateInstance;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;

@Path("/auth")
public class LoginController { 

    @Inject
    Template login ; 

    @GET
    @Path("/login") 
    public TemplateInstance getLogin() { 
        return login.instance(); 
    } 
}
