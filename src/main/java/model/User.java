package model;

public class User {
    private final String userId;
    private final String password;
    private final String name;
    private final String email;

    public User(String userId, String password, String name, String email) {
        this.userId = userId;
        this.password = password;
        this.name = name;
        this.email = email;
    }

    public static Builder builder() {
        return new Builder();
    }

    public String getUserId() {
        return userId;
    }

    public String getPassword() {
        return password;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "User [userId=" + userId + ", password=" + password + ", name=" + name + ", email=" + email + "]";
    }

    public static final class Builder {
        private String userId;
        private String password;
        private String name;
        private String email;

        private Builder() {
        }

        public Builder userId(final String userId) {
            this.userId = userId;
            return this;
        }

        public Builder password(final String password) {
            this.password = password;
            return this;
        }

        public Builder name(final String name) {
            this.name = name;
            return this;
        }

        public Builder email(final String email) {
            this.email = email;
            return this;
        }

        public User build() {
            return new User(this.userId, this.password, this.name, this.email);
        }
    }
}
