/** Holds all Employees in play screen and arranges them
 * Assumptions: The x and y coordinates of the Employees
 * fit the play screen
 * Dependencies: Image files exist
 * Returns:
 */ 

import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.Image;

public class EmployeeList {
	private final String EMPLOYEE_AWAKE_IMG_FILE_NAME = "worker_awake.png";
	private final String EMPLOYEE_ASLEEP_IMG_FILE_NAME = "worker_asleep.png";
	private final double[] EMPLOYEE_X_COORDS =
		{0, 250, 500, 750, 1000};
	private final double[] EMPLOYEE_Y_COORDS =
		{0, 250, 500};
	
	private Group employee_list_group;
	private ArrayList<Employee> employees;
	private Image employee_awake_img;
	private Image employee_asleep_img;
	private int num_employees;
	private ArrayList<Integer> awake_employees;
	
	private Random rand_num_gen;
	
	// initialize grid of employees in Group, all awake
	public EmployeeList() {
		employee_list_group = new Group();
		employees = new ArrayList<Employee>();
		employee_awake_img = getImage(EMPLOYEE_AWAKE_IMG_FILE_NAME);
		employee_asleep_img = getImage(EMPLOYEE_ASLEEP_IMG_FILE_NAME);

		// make a grid of employees
		for (double x_coord: EMPLOYEE_X_COORDS) {
			for (double y_coord: EMPLOYEE_Y_COORDS) {
				addEmployee(x_coord, y_coord);
			}
		}
		
		num_employees = employees.size();
		
		awake_employees = new ArrayList<Integer>();
		for (int i = 0; i < num_employees; i++) {
			awake_employees.add(new Integer(i));
		}
		
		rand_num_gen = new Random();
	}

	public Group getEmployeeListGroup() {
		return employee_list_group;
	}

	public ArrayList<Employee> getEmployees() {
		return employees;
	}
	
	public int getNumEmployees() {
		return num_employees;
	}
	
	// return true if all asleep except 1
	public boolean makeEmployeeSleep() {
		int prev_num_awake = awake_employees.size();

		int pick_index = rand_num_gen.nextInt(prev_num_awake);
		int employee_index = awake_employees.get(pick_index);
		
		Employee e = employees.get(employee_index);
		boolean previouslyAwake = e.putEmployeeToSleep();
		if (!previouslyAwake)
			System.out.println("Error");
		
		awake_employees.remove(pick_index);
		
		int curr_num_awake = awake_employees.size();
		if (curr_num_awake <= 1)
			return true;
		
		return false;
	}
	
	// keep only two awake
	public void makeMostFallAsleep() {
		int num_awake = awake_employees.size();
		if (num_awake > 2) {
			for (int i = num_awake; i > 2; i--) {
				makeEmployeeSleep();
			}
		}
	}
	
	public void wakeEmployee(int index) {
		if (!awake_employees.contains(index)) {
			employees.get(index).wakeEmployee();
			awake_employees.add(index);
		}	
	}
	
	public void wakeAllEmployees() {
		for (int i = 0; i < num_employees; i++) {
			if (!awake_employees.contains(i)) {
				employees.get(i).wakeEmployee();
				awake_employees.add(i);
			}
		}
	}
	
	// add Employee to group, helps in creation of employees
	private void addEmployee(double x_coord, double y_coord) {
		Employee e = new Employee(employee_awake_img, employee_asleep_img);
		employees.add(e);
		Group employee_grp = e.getEmployee();
		employee_list_group.getChildren().add(employee_grp);
		employee_grp.setLayoutX(x_coord);
		employee_grp.setLayoutY(y_coord);
	}
	
	private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
}
