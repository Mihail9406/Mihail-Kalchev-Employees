package mihailkalchevemployees;

import java.util.Date;

/**
 * 
 * @author User
 */
public class Project {
    
    private final int projectID;
    private final Date dateFrom;
    private final Date dateTo;

    public Project(int projectID, Date dateFrom, Date dateTo) {
        this.projectID = projectID;
        this.dateFrom = dateFrom;
        this.dateTo = dateTo;
    }

    @Override
    public String toString() {
        return "Project " + projectID + ", dateFrom: " + dateFrom + ", dateTo: " + dateTo;
    }

    public int getProjectID() {
        return projectID;
    }

    public Date getDateFrom() {
        return dateFrom;
    }

    public Date getDateTo() {
        return dateTo;
    }
    
    
}
