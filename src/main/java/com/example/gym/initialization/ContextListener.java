package com.example.gym.initialization;

import com.example.gym.datastore.DataStore;
import com.example.gym.model.User;
import com.example.gym.repository.AvatarRepository;
import jakarta.servlet.ServletContextEvent;
import jakarta.servlet.ServletContextListener;
import jakarta.servlet.annotation.WebListener;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@WebListener
public class ContextListener implements ServletContextListener {

    @Override
    public void contextInitialized(ServletContextEvent sce) {
        // Tworzenie i inicjalizacja DataStore
        DataStore dataStore = new DataStore();
        List<User> users = initializeUsers();
        dataStore.setUsers(users);

        // Zapisanie DataStore w kontekście aplikacji
        sce.getServletContext().setAttribute("dataStore", dataStore);

        // Inicjalizacja avatarów dla użytkowników
        initializeAvatars(users);
    }

    private List<User> initializeUsers() {
        List<User> users = new ArrayList<>();
        users.add(User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3451")).name("John Doe").email("john.doe@example.com").workoutSessions(new ArrayList<>()).build());
        users.add(User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3452")).name("Jane Smith").email("jane.smith@example.com").workoutSessions(new ArrayList<>()).build());
        users.add(User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3453")).name("Peter Jones").email("peter.jones@example.com").workoutSessions(new ArrayList<>()).build());
        users.add(User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")).name("Anna Williams").email("anna.williams@example.com").workoutSessions(new ArrayList<>()).build());
        return users;
    }

    private void initializeAvatars(List<User> users) {
        AvatarRepository avatarRepository = new AvatarRepository();
        String[] avatarFiles = {"avatars/calvian.png", "avatars/eloise.png", "avatars/sigrid.png", "avatars/zereni.png"};

        for (int i = 0; i < users.size(); i++) {
            User user = users.get(i);
            String avatarFile = avatarFiles[i];

            try (InputStream is = getClass().getClassLoader().getResourceAsStream(avatarFile)) {
                if (is != null) {
                    avatarRepository.save(user.getId(), is);
                } else {
                    System.err.println("Could not find avatar file: " + avatarFile);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent sce) {
        // Można tu dodać logikę czyszczenia zasobów
    }
}