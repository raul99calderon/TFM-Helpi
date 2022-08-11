package es.upm.miw.helpi.models;

public class User {
    private String email;
    private String name;
    private Role role;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.role = Role.USER;
    }

    public User() {
        // empty for framework
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return this.role.name();
    }

    public void fromUserDto(UserDto userDto) {
        this.email = userDto.getEmail();
        this.name  = userDto.getName();
        this.role  = Role.valueOf(userDto.getRole());
    }
}
