package crushers.models;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;


public class Clerk extends User {
    
    protected String staffType;

    @JsonIgnoreProperties({ "details", "manager" })
    private Bank worksAt;

    public Clerk() {
        this.staffType = "clerk"; // empty for Jackson
    }

    public Clerk(
        String emailAddress,
        String firstName, 
        String lastName,
        String address, 
        String password, 
        String[] securityQuestions,
        Bank worksAt
    ){
        super(emailAddress, firstName, lastName, address, password, securityQuestions);
        this.staffType = "clerk";
        this.worksAt = worksAt;
    }

    public int getId() {
        return super.getId();
    }

    public void setId(int id) {
        super.setId(id);
    }
    
    public Bank getWorksAt() {
        return worksAt;
    }

    public void setWorksAt(Bank worksAt) {
        this.worksAt = worksAt;
    }

    public String getStaffType() {
        return staffType;
    }

}