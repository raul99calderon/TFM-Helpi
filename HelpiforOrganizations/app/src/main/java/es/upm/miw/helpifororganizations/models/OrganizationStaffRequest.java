package es.upm.miw.helpifororganizations.models;

public class OrganizationStaffRequest extends OrganizationStaff {
    private boolean confirmed;

    public OrganizationStaffRequest() {
        super();
    }

    public OrganizationStaffRequest(String organizationEmail, String staffEmail, boolean confirmed) {
        super(organizationEmail, staffEmail);
        this.confirmed = confirmed;
    }

    public boolean isConfirmed() {
        return confirmed;
    }

    public void setConfirmed(boolean confirmed) {
        this.confirmed = confirmed;
    }
}
