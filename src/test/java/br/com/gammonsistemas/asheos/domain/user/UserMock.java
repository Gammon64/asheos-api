package br.com.gammonsistemas.asheos.domain.user;

public class UserMock {
    public static final String USER_NAME = "John Doe";
    public static final String USER_EMAIL = "johndoe@test.com";
    public static final String USER_PASSWORD = "strongPassword123";

    public static final User USER_JOHN_DOE() {
        final User user = new User();
        user.setName(USER_NAME);
        user.setEmail(USER_EMAIL);
        user.setPassword(USER_PASSWORD);

        return user;
    }
}
