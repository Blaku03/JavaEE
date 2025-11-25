package com.example.gym.jsf;

import jakarta.enterprise.context.RequestScoped;
import jakarta.faces.application.FacesMessage;
import jakarta.faces.context.ExternalContext;
import jakarta.faces.context.FacesContext;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import jakarta.security.enterprise.AuthenticationStatus;
import jakarta.security.enterprise.SecurityContext;
import jakarta.security.enterprise.authentication.mechanism.http.AuthenticationParameters;
import jakarta.security.enterprise.credential.UsernamePasswordCredential;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.Getter;
import lombok.Setter;

import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

@Named
@RequestScoped
public class LoginController {

    private static final Logger LOGGER = Logger.getLogger(LoginController.class.getName());

    @Inject
    private SecurityContext securityContext;

    @Getter @Setter
    private String username;

    @Getter @Setter
    private String password;

    public void login() throws IOException {
        FacesContext facesContext = FacesContext.getCurrentInstance();
        ExternalContext externalContext = facesContext.getExternalContext();
        HttpServletRequest request = (HttpServletRequest) externalContext.getRequest();
        HttpServletResponse response = (HttpServletResponse) externalContext.getResponse();

        try {
            // First, logout any existing user
            request.logout();
        } catch (ServletException e) {
            LOGGER.log(Level.FINE, "No existing session to logout", e);
        }

        AuthenticationParameters params = AuthenticationParameters.withParams()
                .credential(new UsernamePasswordCredential(username, password))
                .newAuthentication(true)
                .rememberMe(false);

        AuthenticationStatus status = securityContext.authenticate(request, response, params);
        
        LOGGER.info("Authentication status: " + status + " for user: " + username);
        LOGGER.info("Remote user after auth: " + request.getRemoteUser());
        LOGGER.info("User principal: " + request.getUserPrincipal());

        switch (status) {
            case SEND_CONTINUE:
                facesContext.responseComplete();
                break;
            case SEND_FAILURE:
                facesContext.addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, "Błąd logowania", "Niepoprawna nazwa użytkownika lub hasło."));
                break;
            case SUCCESS:
                LOGGER.info("Login successful, redirecting...");
                facesContext.responseComplete();
                externalContext.redirect(externalContext.getRequestContextPath() + "/categories.xhtml");
                break;
            case NOT_DONE:
                facesContext.addMessage(null, 
                    new FacesMessage(FacesMessage.SEVERITY_WARN, "Uwaga", "Uwierzytelnianie nie zostało wykonane."));
                break;
        }
    }
}
