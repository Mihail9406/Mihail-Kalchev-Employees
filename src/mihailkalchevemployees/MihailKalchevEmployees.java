package mihailkalchevemployees;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Scanner;
import java.util.concurrent.TimeUnit;
import javax.swing.JFileChooser;

/**
 *
 * @author User
 */
public class MihailKalchevEmployees {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException {

        DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                .appendOptional(DateTimeFormatter.ofPattern("yyyy-MM-dd"))
                .optionalStart().appendPattern("dd/MMM/yyyy").optionalEnd()
                .optionalStart().appendPattern("dd/MM/yyyy").optionalEnd()
                .optionalStart().appendPattern("dd-MM-yyyy").optionalEnd()
                .optionalStart().appendPattern("dd.MM.yyyy").optionalEnd()
                .optionalStart().appendPattern("dd-MMM-yyyy").optionalEnd()
                .optionalStart().appendPattern("dd.MMM.yyyy").optionalEnd()
                .optionalStart().appendPattern("yyyy/MM/dd").optionalEnd()
                .optionalStart().appendPattern("yyyyMMdd").optionalEnd()
                .optionalStart().appendPattern("yyyy.MM.dd").optionalEnd()
                .optionalStart().appendPattern("yyyy.MMM.dd").optionalEnd()
                .optionalStart().appendPattern("yyyy-MMM-dd").optionalEnd()
                .optionalStart().appendPattern("yyyy/MMM/dd").optionalEnd()
                .optionalStart().appendPattern("dd/MM/yy").optionalEnd()
                .optionalStart().appendPattern("dd-MM-yy").optionalEnd()
                .optionalStart().appendPattern("dd.MM.yy").optionalEnd()
                .optionalStart().appendPattern("dd/MMM/yy").optionalEnd()
                .optionalStart().appendPattern("dd-MMM-yy").optionalEnd()
                .optionalStart().appendPattern("dd.MMM.yy").optionalEnd()
                .toFormatter();

        JFileChooser j = new JFileChooser(); //choose the input file
        j.showSaveDialog(null);
        File f = j.getSelectedFile();
        Scanner sc = new Scanner(f);

        Map<Integer, ArrayList<Project>> workers = new HashMap(); //HashMap that holds every employee along with his projects
        while (sc.hasNextLine()) { //read from the selected file one line at a time

            /*
            Process the data and assign it to required variables for further use
             */
            String line = sc.nextLine().replace(" ", "");
            if (line.equals("")) {
                break;
            }
            String[] arr = line.split(",");
            int EmployeeID = Integer.parseInt(arr[0]);
            int projID = Integer.parseInt(arr[1]);
            String startDate = arr[2];
            String endDate = arr[3];
            Date dateFrom = null;
            Date dateTo = null;

            LocalDate dateF = LocalDate.parse(startDate, formatter);
            dateFrom = Date.from(dateF.atStartOfDay(ZoneId.systemDefault()).toInstant());

            if (endDate.equals("NULL")) {
                dateTo = new Date();
            } else {
                LocalDate dateT = LocalDate.parse(endDate, formatter);
                dateTo = Date.from(dateT.atStartOfDay(ZoneId.systemDefault()).toInstant());
            }

            Project currentProj = new Project(projID, dateFrom, dateTo);

            if (workers.containsKey(EmployeeID)) { //If worker is present in the HashMap assign to him another project
                workers.get(EmployeeID).add(currentProj);
            } else { //If not present, add the worker to the HashMap along with the project
                ArrayList<Project> pList = new ArrayList();
                pList.add(currentProj);
                workers.put(EmployeeID, pList);
            }

        }

        System.out.println(findLongestWorkingPair(workers));
    }

    /*
     Method that takes the Map of workers and iterates over it in order to find 
     the two workers who have worked on the same project for the longest overlaping time period
    @return String containing a message - both workers IDs and days they've worked together
     */
    public static String findLongestWorkingPair(Map<Integer, ArrayList<Project>> workers) {
        String output = "";

        int biggestOverlap = 0;
        int firstWorkerID = 0;
        int secondWorkerID = 0;
        Iterator<Map.Entry<Integer, ArrayList<Project>>> itr = workers.entrySet().iterator();

        while (itr.hasNext()) {
            Map.Entry<Integer, ArrayList<Project>> currentEntry = itr.next();
            ArrayList<Project> currentVal = currentEntry.getValue();
            Iterator<Map.Entry<Integer, ArrayList<Project>>> itr2 = workers.entrySet().iterator();

            while (itr2.hasNext()) {
                int overlapingDays = 0;

                Map.Entry<Integer, ArrayList<Project>> nextEntry = itr2.next();
                ArrayList<Project> nextVal = nextEntry.getValue();
                for (int i = 0; i < nextVal.size(); i++) {
                    for (int j = 0; j < currentVal.size(); j++) {
                        if (nextVal.get(i).getProjectID() == currentVal.get(j).getProjectID()) {

                            int currentEmployeeID = currentEntry.getKey();
                            int nextEmployeeID = nextEntry.getKey();
                            if (currentEmployeeID == nextEmployeeID) {
                                break;
                            }
                            Date currentProjDateFrom = currentVal.get(j).getDateFrom();
                            Date currentProjDateTo = currentVal.get(j).getDateTo();
                            Date nextProjDateFrom = nextVal.get(i).getDateFrom();
                            Date nextProjDateTo = nextVal.get(i).getDateTo();
                            overlapingDays += numOfDaysOverlaping(currentProjDateFrom, currentProjDateTo,
                                    nextProjDateFrom, nextProjDateTo);
                            if (overlapingDays > biggestOverlap) {
                                biggestOverlap = overlapingDays;
                                firstWorkerID = currentEmployeeID;
                                secondWorkerID = nextEmployeeID;
                            }
                        }
                    }
                }
            }
        }
        if (biggestOverlap == 0) {
            output = "No such workers";
        } else {
            output = "Employees: " + firstWorkerID + " and " + secondWorkerID + " have worked together the longest";
            output += " for " + biggestOverlap + " days";
        }
        return output;
    }

    /*
    A method that returns the number of overlaping days based on given start 
    and end dates for both projects
     */
    public static int numOfDaysOverlaping(Date aStart, Date aEnd, Date bStart, Date bEnd) {

        Date latestStart = aStart;
        Date earliestEnd = bEnd;

        if (aStart.before(bStart)) {
            latestStart = bStart;
        }
        if (aEnd.before(bEnd)) {
            earliestEnd = aEnd;
        }

        long diffInMillies = earliestEnd.getTime() - latestStart.getTime(); //intersection period
        if (diffInMillies <= 0) { //if negative => no intersaction between the dates => return 0 days
            return 0;
        } else { //else return the amount of days the projects intersect
            return (int) TimeUnit.DAYS.convert(diffInMillies, TimeUnit.MILLISECONDS) + 1;
        }
    }

}
