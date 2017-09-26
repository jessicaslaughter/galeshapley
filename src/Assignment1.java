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
		int profList[] = {1, 3, 0, 2, 4, 2, 0, 4, 3, 1, 1, 2, 0, 4, 3, 4, 3, 0, 2, 1, 4, 0, 2, 1, 3};
		int studentList[] = {1, 0, 3, 2, 4, 3, 2, 4, 0, 1, 4, 0, 3, 2, 1, 1, 0, 3, 4, 2, 4, 2, 3, 1, 0};
		Preferences preferences = createPreferenceList(profList, studentList, profList.length);
		ArrayList<Integer> matching = stableMatchGaleShapley(preferences);
		System.out.println(matching);
		preferences = createPreferenceList(profList, studentList, profList.length);
		ArrayList<Cost> cost1 = stableMatchCosts(preferences);
		for (Cost cost : cost1) {
			System.out.println(cost.toString());
		}
		preferences = createPreferenceList(profList, studentList, profList.length);
		ArrayList<Cost> cost2 = stableMatchCostsStudent(preferences);
		for (Cost cost : cost2) {
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
    			System.out.println("found a stable matching!");
    			System.out.println(permutation);
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
    	ArrayList<ArrayList<Integer>> profsList = invert(preferences.getProfessors_preference());
    	ArrayList<ArrayList<Integer>> studentsList = invert(preferences.getStudents_preference());
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
    	ArrayList<ArrayList<Integer>> profsList = invert(preferences.getProfessors_preference());
    	ArrayList<ArrayList<Integer>> studentsList = invert(preferences.getStudents_preference());
    	Preferences reversePrefs = new Preferences(preferences.getNumberOfStudents(), preferences.getNumberOfProfessors(), preferences.getStudents_preference(), preferences.getProfessors_preference());
    	ArrayList<Integer> matching = stableMatchGaleShapley(reversePrefs);
    	for (int i = 0; i < matching.size(); i++) {
    		int profCost, studentCost;
    		ArrayList<Integer> profList = profsList.get(i);
    		ArrayList<Integer> studentList = studentsList.get(matching.get(i));
    		profCost = profList.get(matching.get(i));
    		studentCost = studentList.get(i);
    		Cost currentCost = new Cost(matching.get(i), i, studentCost, profCost);
    		cost.add(currentCost);
    	}
    	return cost;
    }
    
    // Create Preference List for testing
 	public static Preferences createPreferenceList(int[] profList, int[] studentList, int num) {
 		ArrayList<ArrayList<Integer>> professors_preference = new ArrayList<ArrayList<Integer>>();
 		ArrayList<ArrayList<Integer>> students_preference = new ArrayList<ArrayList<Integer>>();
 		int count = 0;
 		for (int i = 0; i < Math.sqrt(num); i++) {
 			ArrayList<Integer> temp = new ArrayList<Integer>();
 			for (int j = 0; j < Math.sqrt(num); j++) {
 				temp.add(profList[count]);
 				count++;
 			}
 			professors_preference.add(temp);
 		}
 		
 		count = 0;
 		for (int i = 0; i < Math.sqrt(num); i++) {
 			ArrayList<Integer> temp = new ArrayList<Integer>();
 			for (int j = 0; j < Math.sqrt(num); j++) {
 				temp.add(studentList[count]);
 				count++;
 			}
 			students_preference.add(temp);
 		}
 		
 		Preferences preferences = new Preferences((int)Math.sqrt(num), (int)Math.sqrt(num), professors_preference, students_preference);
 		return preferences;
 	}
}
