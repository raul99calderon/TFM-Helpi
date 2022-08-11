package es.upm.miw.helpiforstaff.models;

public class OrganizationStaffRequestDto extends OrganizationStaffRequest {
    private String key;

    public OrganizationStaffRequestDto() {
        super();
    }

    public OrganizationStaffRequestDto(String organizationEmail, String staffEmail, boolean confirmed, String key) {
        super(organizationEmail, staffEmail, confirmed);
        this.key = key;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }
}
