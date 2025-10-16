package com.example.gym.repository;

import com.example.gym.model.User;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.Map;

public class UserRepository {

    private static UserRepository instance;
    private final Map<UUID, User> users = new ConcurrentHashMap<>();

    private UserRepository() {
        initializeTestUsers();
    }

    public static synchronized UserRepository getInstance() {
        if (instance == null) {
            instance = new UserRepository();
        }
        return instance;
    }

    private void initializeTestUsers() {
        User user1 = User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3451")).name("John Doe").email("john.doe@example.com").workoutSessions(new ArrayList<>()).build();
        User user2 = User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3452")).name("Jane Smith").email("jane.smith@example.com").workoutSessions(new ArrayList<>()).build();
        User user3 = User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3453")).name("Peter Jones").email("peter.jones@example.com").workoutSessions(new ArrayList<>()).build();
        User user4 = User.builder().id(UUID.fromString("f8c3de3d-1fea-4d7c-a8b0-29f63c4c3454")).name("Anna Williams").email("anna.williams@example.com").workoutSessions(new ArrayList<>()).build();

        users.put(user1.getId(), user1);
        users.put(user2.getId(), user2);
        users.put(user3.getId(), user3);
        users.put(user4.getId(), user4);

        initializeAvatars();
    }

    private void initializeAvatars() {
        AvatarRepository avatarRepository = new AvatarRepository();
        String[] avatarFiles = {"avatars/img1.jpeg", "avatars/img2.jpeg", "avatars/img3.jpeg", "avatars/img4.jpeg"};
        List<User> userList = new ArrayList<>(users.values());

        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            String avatarResourcePath = avatarFiles[i];

            try (InputStream is = getClass().getClassLoader().getResourceAsStream(avatarResourcePath)) {
                if (is != null) {
                    avatarRepository.save(user.getId(), is);
                } else {
                    System.err.println("ERROR: Could not find avatar resource in classpath: " + avatarResourcePath);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Collection<User> findAll() {
        return users.values();
    }

    public Optional<User> findById(UUID id) {
        return Optional.ofNullable(users.get(id));
    }

    public void save(User user) {
        users.put(user.getId(), user);
    }

    public void delete(User user) {
        users.remove(user.getId());
    }
}