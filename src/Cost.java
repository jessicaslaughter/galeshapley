/**
 * Cost object for Assignment 1 - Part 3
 * This object helps you to return 4 fields at once.
 * The meaning of cost is described in the assignment.
 */
public class Cost {
    private int indexOfProfessor;
    private int indexOfStudent;
    private int costToProfessor;
    private int costToStuent;

    public Cost(int indexOfProfessor, int indexOfStudent, int costToProfessor, int costToStuent) {

        this.indexOfProfessor = indexOfProfessor;
        this.indexOfStudent = indexOfStudent;
        this.costToProfessor = costToProfessor;
        this.costToStuent = costToStuent;
    }

    public int getIndexOfProfessor() {
        return indexOfProfessor;
    }

    public void setIndexOfProfessor(int indexOfProfessor) {
        this.indexOfProfessor = indexOfProfessor;
    }

    public int getIndexOfStudent() {
        return indexOfStudent;
    }

    public void setIndexOfStudent(int indexOfStudent) {
        this.indexOfStudent = indexOfStudent;
    }

    public int getCostToProfessor() {
        return costToProfessor;
    }

    public void setCostToProfessor(int costToProfessor) {
        this.costToProfessor = costToProfessor;
    }

    public int getCostToStuent() {
        return costToStuent;
    }

    public void setCostToStuent(int costToStuent) {
        this.costToStuent = costToStuent;
    }
    
    public String toString() {
    	return "(" + this.indexOfProfessor + "," + this.indexOfStudent + "," + this.costToProfessor + "," + this.costToStuent + ")";
    }
}
