package com.example.gym.rest;

import com.example.gym.dto.WorkoutSessionDto;
import com.example.gym.model.WorkoutSession;
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

@Path("/categories/{typeId}/sessions")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class WorkoutSessionResource {

    @Inject
    private WorkoutService workoutService;

    @Context
    private UriInfo uriInfo;


    @GET
    public Response getAllSessionsForType(@PathParam("typeId") UUID typeId) {
        if (workoutService.findTypeById(typeId).isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).entity("Category not found").build();
        }

        List<WorkoutSession> sessions = workoutService.findSessionsByTypeId(typeId);
        return Response.ok(sessions).build();
    }


    @GET
    @Path("/{sessionId}")
    public Response getSessionById(@PathParam("typeId") UUID typeId,
                                   @PathParam("sessionId") UUID sessionId) {

        Optional<WorkoutSession> sessionOpt = workoutService.findSessionById(sessionId);

        if (sessionOpt.isPresent() && sessionOpt.get().getWorkoutType().getId().equals(typeId)) {
            return Response.ok(sessionOpt.get()).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }


    @POST
    public Response createSession(@PathParam("typeId") UUID typeId, WorkoutSessionDto dto) {
        try {
            WorkoutSession newSession = workoutService.createWorkoutSession(typeId, dto);

            URI location = uriInfo.getAbsolutePathBuilder().path(newSession.getId().toString()).build();
            return Response.created(location).entity(newSession).build();

        } catch (IllegalArgumentException e) {
            return Response.status(Response.Status.NOT_FOUND).entity(e.getMessage()).build();
        }
    }


    @PUT
    @Path("/{sessionId}")
    public Response updateSession(@PathParam("typeId") UUID typeId,
                                  @PathParam("sessionId") UUID sessionId,
                                  WorkoutSessionDto dto) {

        Optional<WorkoutSession> sessionOpt = workoutService.findSessionById(sessionId);
        if (sessionOpt.isEmpty()) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        if (!sessionOpt.get().getWorkoutType().getId().equals(typeId)) {
            return Response.status(Response.Status.CONFLICT).entity("Session does not belong to this category.").build();
        }

        WorkoutSession updatedSession = workoutService.updateWorkoutSession(sessionId, dto).get();
        return Response.ok(updatedSession).build();
    }


    @DELETE
    @Path("/{sessionId}")
    public Response deleteSession(@PathParam("typeId") UUID typeId,
                                  @PathParam("sessionId") UUID sessionId) {

        Optional<WorkoutSession> sessionOpt = workoutService.findSessionById(sessionId);
        if (sessionOpt.isEmpty() || !sessionOpt.get().getWorkoutType().getId().equals(typeId)) {
            return Response.status(Response.Status.NOT_FOUND).build();
        }

        workoutService.deleteWorkoutSession(sessionId);
        return Response.noContent().build();
    }
}