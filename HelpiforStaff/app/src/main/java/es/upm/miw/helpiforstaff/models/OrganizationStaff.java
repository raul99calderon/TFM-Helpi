package es.upm.miw.helpiforstaff.models;

public class OrganizationStaff {
    private String organizationEmail, staffEmail;

    public OrganizationStaff() {
    }

    public OrganizationStaff(String organizationEmail, String staffEmail) {
        this.organizationEmail = organizationEmail;
        this.staffEmail = staffEmail;
    }

    public String getOrganizationEmail() {
        return organizationEmail;
    }

    public void setOrganizationEmail(String organizationEmail) {
        this.organizationEmail = organizationEmail;
    }

    public String getStaffEmail() {
        return staffEmail;
    }

    public void setStaffEmail(String staffEmail) {
        this.staffEmail = staffEmail;
    }
}
