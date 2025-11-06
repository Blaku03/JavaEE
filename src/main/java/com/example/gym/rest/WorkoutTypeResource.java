package com.example.gym.rest;

import com.example.gym.dto.WorkoutTypeDto;
import com.example.gym.model.WorkoutType;
import com.example.gym.service.WorkoutService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.Context;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.core.UriInfo;

import java.net.URI;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Path("/categories") 
@RequestScoped 
@Produces(MediaType.APPLICATION_JSON) 
@Consumes(MediaType.APPLICATION_JSON) 
public class WorkoutTypeResource {

    @Inject
    private WorkoutService workoutService;

    @Context
    private UriInfo uriInfo; 

    @GET
    public Response getAllTypes() {
        List<WorkoutType> types = workoutService.findAllTypes();
        return Response.ok(types).build();
    }

    @GET
    @Path("/{id}")
    public Response getTypeById(@PathParam("id") UUID id) {
        Optional<WorkoutType> type = workoutService.findTypeById(id);

        return type.map(Response::ok) 
                .orElse(Response.status(Response.Status.NOT_FOUND)) 
                .build();
    }

    @POST
    public Response createType(WorkoutTypeDto dto) {
        
        WorkoutType newType = workoutService.createWorkoutType(dto.getName(), dto.getDescription());

        URI location = uriInfo.getAbsolutePathBuilder().path(newType.getId().toString()).build();
        return Response.created(location).entity(newType).build(); 
    }

    @PUT
    @Path("/{id}")
    public Response updateType(@PathParam("id") UUID id, WorkoutTypeDto dto) {
        Optional<WorkoutType> updatedType = workoutService.updateWorkoutType(id, dto);

        return updatedType.map(Response::ok) 
                .orElse(Response.status(Response.Status.NOT_FOUND)) 
                .build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteType(@PathParam("id") UUID id) {
        
        if (workoutService.findTypeById(id).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        workoutService.deleteWorkoutType(id);
        return Response.noContent().build(); 
    }
}