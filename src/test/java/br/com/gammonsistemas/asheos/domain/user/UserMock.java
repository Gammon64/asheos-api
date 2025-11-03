package br.com.gammonsistemas.asheos.domain.user;

import java.util.UUID;

public class UserMock {
    public static final UUID USER_ID = UUID.randomUUID();
    public static final String USER_NAME = "John Doe";
    public static final String USER_EMAIL = "johndoe@test.com";
    public static final String USER_PASSWORD = "strongPassword123";

    public static final User USER_JOHN_DOE() {
        final User user = new User();
        user.setId(USER_ID);
        user.setName(USER_NAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);

        return user;
    }
}
