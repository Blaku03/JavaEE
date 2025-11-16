package com.example.gym.rest;

import com.example.gym.dto.CreateUserRequest;
import com.example.gym.dto.GetUserResponse;
import com.example.gym.dto.GetUsersResponse;
import com.example.gym.dto.UpdateUserRequest;
import com.example.gym.model.User;
import com.example.gym.service.UserService;
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
import java.util.stream.Collectors;

@Path("/users")
@RequestScoped
@Produces(MediaType.APPLICATION_JSON)
@Consumes(MediaType.APPLICATION_JSON)
public class UserResource {

    @Inject
    private UserService userService;

    @Context
    private UriInfo uriInfo;

    @GET
    public Response getAllUsers() {
        List<User> users = userService.findAll();
        List<GetUsersResponse.User> userDtos = users.stream()
                .map(user -> GetUsersResponse.User.builder()
                        .id(user.getId())
                        .name(user.getUsername())
                        .build())
                .collect(Collectors.toList());
        return Response.ok(new GetUsersResponse(userDtos)).build();
    }

    @GET
    @Path("/{id}")
    public Response getUserById(@PathParam("id") UUID id) {
        Optional<User> userOptional = userService.findById(id);

        if (userOptional.isPresent()) {
            User user = userOptional.get();
            GetUserResponse userDto = GetUserResponse.builder()
                    .id(user.getId())
                    .name(user.getUsername())
                    .email(user.getEmail())
                    .build();
            return Response.ok(userDto).build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }

    @POST
    public Response createUser(CreateUserRequest request) {
        if (request.getName() == null || request.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name and email are required")
                    .build();
        }

        User newUser = userService.createUser(request);

        URI location = uriInfo.getAbsolutePathBuilder()
                .path(newUser.getId().toString())
                .build();

        GetUserResponse userDto = GetUserResponse.builder()
                .id(newUser.getId())
                .name(newUser.getUsername())
                .email(newUser.getEmail())
                .build();

        return Response.created(location).entity(userDto).build();
    }

    @PUT
    @Path("/{id}")
    public Response updateUser(@PathParam("id") UUID id, UpdateUserRequest request) {
        if (request.getName() == null || request.getEmail() == null) {
            return Response.status(Response.Status.BAD_REQUEST)
                    .entity("Name and email are required")
                    .build();
        }

        Optional<User> updatedUser = userService.updateUser(id, request);

        return updatedUser.map(user -> Response.noContent().build())
                .orElse(Response.status(Response.Status.NOT_FOUND).build());
    }

    @DELETE
    @Path("/{id}")
    public Response deleteUser(@PathParam("id") UUID id) {
        if (userService.deleteUser(id)) {
            return Response.noContent().build();
        }

        return Response.status(Response.Status.NOT_FOUND).build();
    }
}
