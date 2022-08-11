package es.upm.miw.helpiadmin.models;

public class ConfirmationUserDto extends UserDto {
    private boolean confirmed;

    public ConfirmationUserDto() {
        super();
    }

    public ConfirmationUserDto(String email, String name, String role, boolean confirmed) {
        super(email, name, role);
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
