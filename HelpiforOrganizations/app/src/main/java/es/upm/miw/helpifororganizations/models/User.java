package es.upm.miw.helpifororganizations.models;

public class User {
    private String email;
    private String name;
    private Role role;

    public User(String email, String name, Role role) {
        this.email = email;
        this.name = name;
        this.role = role;
    }

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.role = Role.ORGANIZATION;
    }

    public User() {
        // empty for framework
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setRole(String role) {
        this.role = Role.valueOf(role);
    }

    public String getEmail() {
        return email;
    }

    public String getName() {
        return name;
    }

    public String getRole() {
        return this.role.toString();
    }

    public boolean isOrganization() {
        return this.role.equals(Role.ORGANIZATION);
    }

    public void fromUserDto(UserDto userDto) {
        this.email = userDto.getEmail();
        this.name  = userDto.getName();
        this.role  = Role.valueOf(userDto.getRole());
    }
}
