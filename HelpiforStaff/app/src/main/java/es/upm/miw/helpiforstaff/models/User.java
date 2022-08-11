package es.upm.miw.helpiforstaff.models;

public class User {
    private String email;
    private String name;
    private Role role;

    public User(String email, String name) {
        this.email = email;
        this.name = name;
        this.role = Role.STAFF;
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

    public boolean isStaff() {
        return this.role.equals(Role.STAFF);
    }

    public void fromUserDto(UserDto userDto) {
        this.email = userDto.getEmail();
        this.name  = userDto.getName();
        this.role  = Role.valueOf(userDto.getRole());
    }
}
