import java.util.ArrayList;

/**
 * Class to provide input to Stable Matching algorithms
 */
public class Preferences {
    /** Number of professors. */
    private int numberOfProfessors;

    /** Number of students. */
    private int numberOfStudents;

    /** A list containing each professor's preference list. */
    private ArrayList<ArrayList<Integer>> professors_preference;

    /** A list containing each student's preference list. */
    private ArrayList<ArrayList<Integer>> students_preference;


    public Preferences(int numberOfProfessors, int numberOfStudents,
                       ArrayList<ArrayList<Integer>> professors_preference,
                       ArrayList<ArrayList<Integer>> students_preference) {
        this.numberOfProfessors = numberOfProfessors;
        this.numberOfStudents = numberOfStudents;
        this.professors_preference = professors_preference;
        this.students_preference = students_preference;
    }

    public int getNumberOfProfessors() {
        return numberOfProfessors;
    }

    public void setNumberOfProfessors(int numberOfProfessors) {
        this.numberOfProfessors = numberOfProfessors;
    }

    public int getNumberOfStudents() {
        return numberOfStudents;
    }

    public void setNumberOfStudents(int numberOfStudents) {
        this.numberOfStudents = numberOfStudents;
    }

    public ArrayList<ArrayList<Integer>> getProfessors_preference() {
        return professors_preference;
    }

    public void setProfessors_preference(ArrayList<ArrayList<Integer>> professors_preference) {
        this.professors_preference = professors_preference;
    }

    public ArrayList<ArrayList<Integer>> getStudents_preference() {
        return students_preference;
    }

    public void setStudents_preference(ArrayList<ArrayList<Integer>> students_preference) {
        this.students_preference = students_preference;
    }
}
