/**
 * Class to implement Stable Matching algorithms
 * Jessica Slaughter
 * jts3329
 */

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

public class Assignment1 {
	
	// Set up Preference lists, call Stable Matching functions
	public static void main(String args[]) {
		ArrayList<Integer> BFMatching = stableMatchBruteForce(createPreferenceList());
		ArrayList<Integer> GSMatching = stableMatchGaleShapley(createPreferenceList());
		System.out.println(BFMatching);
		System.out.println(GSMatching);
		ArrayList<Cost> profCosts = stableMatchCosts(createPreferenceList());
		ArrayList<Cost> studCosts = stableMatchCostsStudent(createPreferenceList());
		for (Cost cost : profCosts) {
			System.out.println(cost.toString());
		}
		for (Cost cost : studCosts) {
			System.out.println(cost.toString());
		}
		return;
	}
	
	
	// Invert preference list
	public static ArrayList<ArrayList<Integer>> invert(ArrayList<ArrayList<Integer>> list) {
		ArrayList<ArrayList<Integer>> inverse = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> arr : list) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			while (temp.size() < arr.size()) {
				temp.add(0); // fill temp array up
			}
			for (int i = 0; i < arr.size(); i++) {
				temp.set(arr.get(i), i);
			}
			inverse.add(temp);
		}
		return inverse;
	}
	
	// Invert and fix indexing of a preference list
	public static ArrayList<ArrayList<Integer>> invertAndIndex(ArrayList<ArrayList<Integer>> list) {
		ArrayList<ArrayList<Integer>> inverse = new ArrayList<ArrayList<Integer>>();
		for (ArrayList<Integer> arr : list) {
			ArrayList<Integer> temp = new ArrayList<Integer>();
			while (temp.size() < arr.size()) {
				temp.add(0); // fill temp array up
			}
			for (int i = 0; i < arr.size(); i++) {
				temp.set(arr.get(i) - 1, i);
			}
			inverse.add(temp);
		}
		return inverse;
	}
	// Invert matching 
	public static ArrayList<Integer> invertMatching(ArrayList<Integer> arr) {
		ArrayList<Integer> temp = new ArrayList<Integer>();
		while (temp.size() < arr.size()) {
			temp.add(0); // fill temp array up
		}
		for (int i = 0; i < arr.size(); i++) {
			temp.set(arr.get(i), i);
		}
		return temp;
	}
	
	// Format indexing 
	public static void formatIndex(Preferences preferences) {
		for (ArrayList<Integer> list : preferences.getProfessors_preference()) {
			for (int i = 0; i < list.size(); i++) {
				int val = list.get(i);
				val = val - 1;
				list.set(i, val);
			}
		}
		for (ArrayList<Integer> list : preferences.getStudents_preference()) {
			for (int i = 0; i < list.size(); i++) {
				int val = list.get(i);
				val = val - 1;
				list.set(i, val--);
			}
		}
		return;
	}
	
	// Determine if a matching is stable
	public static boolean isStableMatching(ArrayList<Integer> matching, Preferences preferences) {
		ArrayList<ArrayList<Integer>> profPrefs = invert(preferences.getProfessors_preference()); // get inverse of profs pref list
		ArrayList<ArrayList<Integer>> studentPrefs = invert(preferences.getStudents_preference()); // get inverse of student pref list
		ArrayList<Integer> inverseMatching = invertMatching(matching);
		
		for (int s = 0; s < studentPrefs.size(); s++) { // go through student pref list
    		ArrayList<Integer> student = studentPrefs.get(s); // student s's pref list
    		int studentMatch = inverseMatching.get(s); // get prof that s matched with
    		for (int p = 0; p < student.size(); p++) {
    			if (student.get(p) < student.get(studentMatch)) { // found a prof p the student prefers more
    				ArrayList<Integer> prof = profPrefs.get(p); // prof p's pref list
    				int profMatch = matching.get(p); // get prof p's match
    				if (prof.get(s) < prof.get(profMatch)) { // prof p prefers s to their current match
    					return false; // not a stable matching
    				}
    			}
    		}
    	}
		return true; // found a stable matching
	}
	
	// Get all permutations for the brute force algorithm
    public static void getPermutations(ArrayList<Integer> arr, int n, int k, ArrayList<ArrayList<Integer>> allPermutations) {
    	
    	// base case
    	if (k == 0) {
    		ArrayList<Integer> permutation = new ArrayList<Integer>();
	        for (int i = n; i < arr.size(); i++){
	        	permutation.add(arr.get(i));
	        }
	        allPermutations.add(permutation);
	        return;
	    }
	
	    for (int i = 0; i < n; i++) {
	    	swap(arr, i, n-1);
	        getPermutations(arr, n-1, k-1, allPermutations);
	        swap(arr, i, n-1);
	    }

    }

    // Permutation helper function
    public static void swap(ArrayList<Integer> arr, int i, int j) {
    	Integer temp = arr.get(i);
        arr.set(i, arr.get(j));
        arr.set(j, temp);
    }
	
    // Part1: Implement a Brute Force Solution
    public static ArrayList<Integer> stableMatchBruteForce(Preferences preferences) {
    	formatIndex(preferences);
    	ArrayList<Integer> matching = new ArrayList<Integer>();
    	int num = preferences.getNumberOfProfessors();
    	
    	// set up matching array with all possible values
    	for (int i = 0; i < num; i++) {
    		matching.add(i);
    	}
    	
    	ArrayList<ArrayList<Integer>> allPermutations = new ArrayList<ArrayList<Integer>>();
    	getPermutations(matching, num, num, allPermutations);
    	for (ArrayList<Integer> permutation : allPermutations) {
    		if (isStableMatching(permutation, preferences)) {
    			return permutation; // found stable matching
    		}
    	}
     	
    	// no stable matching found
    	matching.clear();
    	matching.add(-1);
    	return matching;
    }

    // Part2: Implement Gale-Shapley Algorithm
    public static ArrayList<Integer> stableMatchGaleShapley(Preferences preferences) {
    	formatIndex(preferences);
    	ArrayList<Integer> matching = new ArrayList<Integer>();
    	int[] professors = new int[preferences.getNumberOfProfessors()];
    	int[] students = new int[preferences.getNumberOfStudents()];
    	int[] count = new int[preferences.getNumberOfProfessors()];
    	ArrayList<ArrayList<Integer>> profsList = preferences.getProfessors_preference();
    	ArrayList<ArrayList<Integer>> studentsList = invert(preferences.getStudents_preference());
    	Queue<Integer> q = new LinkedList<Integer>();
    	
    	// initialize arrays for matches
    	for (int i = 0; i < preferences.getNumberOfProfessors(); i++) {
    		professors[i] = -1;
    		students[i] = -1;
    	}
    	
    	// add all professors to the queue
    	for (int i = 0; i < preferences.getNumberOfProfessors(); i++) {
    		q.add(i);
    	}
    	
    	while (!q.isEmpty() && profsList.get(q.peek()).size() != 0) { // while there is a free prof and they haven't proposed to every student
    		int p = q.remove();
    		ArrayList<Integer> pList = profsList.get(p);
    		int s = pList.get(0); // highest ranking student that p hasn't proposed to
    		if (students[s] == -1) { // student is free
    			count[p]++;
    			pList.remove(0);
    			students[s] = p;
    			professors[p] = s;
    		}
    		else {
    			ArrayList<Integer> sList = studentsList.get(s);
    			if (sList.get(students[s]) < sList.get(p)) { // s prefers current match to p
    				q.add(p); // p remains free
    				pList.remove(0);
    			}
    			else {
    				q.add(students[s]);
    				count[p]++;
    				pList.remove(0);
    				students[s] = p;
    				professors[p] = s;
    			}
    		}
    	}
    	
    	for (int i : professors) {
    		matching.add(i);
    	}
    	return matching;
    }

    // Part3: Matching with Costs
    public static ArrayList<Cost> stableMatchCosts(Preferences preferences) {
    	ArrayList<Cost> cost = new ArrayList<Cost>();
    	ArrayList<ArrayList<Integer>> profsList = invertAndIndex(preferences.getProfessors_preference());
    	ArrayList<ArrayList<Integer>> studentsList = invertAndIndex(preferences.getStudents_preference());
    	ArrayList<Integer> matching = stableMatchGaleShapley(preferences);
    	for (int i = 0; i < matching.size(); i++) {
    		int profCost, studentCost;
    		ArrayList<Integer> profList = profsList.get(i);
    		ArrayList<Integer> studentList = studentsList.get(matching.get(i));
    		profCost = profList.get(matching.get(i));
    		studentCost = studentList.get(i);
    		Cost currentCost = new Cost(i, matching.get(i), profCost, studentCost);
    		cost.add(currentCost);
    	}
    	return cost;
    }
    
    public static ArrayList<Cost> stableMatchCostsStudent(Preferences preferences) {
    	ArrayList<Cost> cost = new ArrayList<Cost>();
    	ArrayList<ArrayList<Integer>> profsList = invertAndIndex(preferences.getProfessors_preference());
    	ArrayList<ArrayList<Integer>> studentsList = invertAndIndex(preferences.getStudents_preference());
    	Preferences reversePrefs = new Preferences(preferences.getNumberOfStudents(), preferences.getNumberOfProfessors(), preferences.getStudents_preference(), preferences.getProfessors_preference());
    	ArrayList<Integer> matching = stableMatchGaleShapley(reversePrefs);
    	for (int i = 0; i < matching.size(); i++) {
    		int profCost, studentCost;
    		ArrayList<Integer> profList = profsList.get(matching.get(i));
    		ArrayList<Integer> studentList = studentsList.get(i);
    		profCost = profList.get(i);
    		studentCost = studentList.get(matching.get(i));
    		Cost currentCost = new Cost(i, matching.get(i), studentCost, profCost);
    		cost.add(currentCost);
    	}
    	return cost;
    }
    
    // Create Preference List for testing
 	public static Preferences createPreferenceList() {
 		ArrayList<ArrayList<Integer>> professors_preference = new ArrayList<ArrayList<Integer>>();
 		ArrayList<ArrayList<Integer>> students_preference = new ArrayList<ArrayList<Integer>>();

 		ArrayList<Integer> prof1 = new ArrayList<Integer>();
 		prof1.add(3);
 		prof1.add(4);
 		prof1.add(2);
 		prof1.add(1);
 		prof1.add(6);
 		prof1.add(7);
 		prof1.add(5);

 		ArrayList<Integer> prof2 = new ArrayList<Integer>();
 		prof2.add(6);
 		prof2.add(4);
 		prof2.add(2);
 		prof2.add(3);
 		prof2.add(5);
 		prof2.add(1);
 		prof2.add(7);
 		
 		ArrayList<Integer> prof3 = new ArrayList<Integer>();
 		prof3.add(6);
 		prof3.add(3);
 		prof3.add(5);
 		prof3.add(7);
 		prof3.add(2);
 		prof3.add(4);
 		prof3.add(1);
 		
 		ArrayList<Integer> prof4 = new ArrayList<Integer>();
 		prof4.add(1);
 		prof4.add(6);
 		prof4.add(3);
 		prof4.add(2);
 		prof4.add(4);
 		prof4.add(7);
 		prof4.add(5);

 		ArrayList<Integer> prof5 = new ArrayList<Integer>();
 		prof5.add(1);
 		prof5.add(6);
 		prof5.add(5);
 		prof5.add(3);
 		prof5.add(4);
 		prof5.add(7);
 		prof5.add(2);

 		ArrayList<Integer> prof6 = new ArrayList<Integer>();
 		prof6.add(1);
 		prof6.add(7);
 		prof6.add(3);
 		prof6.add(4);
 		prof6.add(5);
 		prof6.add(6);
 		prof6.add(2);

 		ArrayList<Integer> prof7 = new ArrayList<Integer>();
 		prof7.add(5);
 		prof7.add(6);
 		prof7.add(2);
 		prof7.add(4);
 		prof7.add(3);
 		prof7.add(7);
 		prof7.add(1);
 		
 		professors_preference.add(prof1);
 		professors_preference.add(prof2);
 		professors_preference.add(prof3);
 		professors_preference.add(prof4);
 		professors_preference.add(prof5);
 		professors_preference.add(prof6);
 		professors_preference.add(prof7);

 		ArrayList<Integer> stud1 = new ArrayList<Integer>();
 		stud1.add(4);
 		stud1.add(5);
 		stud1.add(3);
 		stud1.add(7);
 		stud1.add(2);
 		stud1.add(6);
 		stud1.add(1);

 		ArrayList<Integer> stud2 = new ArrayList<Integer>();
 		stud2.add(5);
 		stud2.add(6);
 		stud2.add(4);
 		stud2.add(7);
 		stud2.add(3);
 		stud2.add(2);
 		stud2.add(1);

 		ArrayList<Integer> stud3 = new ArrayList<Integer>();
 		stud3.add(1);
 		stud3.add(6);
 		stud3.add(5);
 		stud3.add(4);
 		stud3.add(3);
 		stud3.add(7);
 		stud3.add(2);

 		ArrayList<Integer> stud4 = new ArrayList<Integer>();
 		stud4.add(3);
 		stud4.add(5);
 		stud4.add(6);
 		stud4.add(7);
 		stud4.add(2);
 		stud4.add(4);
 		stud4.add(1);

 		ArrayList<Integer> stud5 = new ArrayList<Integer>();
 		stud5.add(1);
 		stud5.add(7);
 		stud5.add(6);
 		stud5.add(4);
 		stud5.add(3);
 		stud5.add(5);
 		stud5.add(2);

 		ArrayList<Integer> stud6 = new ArrayList<Integer>();
 		stud6.add(6);
 		stud6.add(3);
 		stud6.add(7);
 		stud6.add(5);
 		stud6.add(2);
 		stud6.add(4);
 		stud6.add(1);

 		ArrayList<Integer> stud7 = new ArrayList<Integer>();
 		stud7.add(1);
 		stud7.add(7);
 		stud7.add(4);
 		stud7.add(2);
 		stud7.add(6);
 		stud7.add(5);
 		stud7.add(3);
 		
 		students_preference.add(stud1);
 		students_preference.add(stud2);
 		students_preference.add(stud3);
 		students_preference.add(stud4);
 		students_preference.add(stud5);
 		students_preference.add(stud6);
 		students_preference.add(stud7);

 		Preferences preferences = new Preferences(7, 7, professors_preference, students_preference);
 		return preferences;
 	}
}
