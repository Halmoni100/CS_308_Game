import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.image.Image;

public class EmployeeList {
	private static final String EMPLOYEE_AWAKE_IMG_FILE_NAME = "black_square.png";
	private static final String EMPLOYEE_ASLEEP_IMG_FILE_NAME = "red_square.png";
	private static final double[] EMPLOYEE_X_COORDS =
		{0, 200, 400, 600, 800, 1000};
	private static final double[] EMPLOYEE_Y_COORDS =
		{0, 150, 300, 450, 600};
	
	private Group employee_list_group;
	private ArrayList<Employee> employees;
	private Image employee_awake_img;
	private Image employee_asleep_img;
	private int num_employees;
	private ArrayList<Integer> awake_employees;
	
	private Random rand_num_gen;
	
	public EmployeeList() {
		employee_list_group = new Group();
		employees = new ArrayList<Employee>();
		employee_awake_img = getImage(EMPLOYEE_AWAKE_IMG_FILE_NAME);
		employee_asleep_img = getImage(EMPLOYEE_ASLEEP_IMG_FILE_NAME);
		
		// create employees
		int num_x_coords = EMPLOYEE_X_COORDS.length;
		int num_y_coords = EMPLOYEE_Y_COORDS.length;
		// make top row
		double y_coord = EMPLOYEE_Y_COORDS[0];
		for (double x_coord: EMPLOYEE_X_COORDS) {
			addEmployee(x_coord, y_coord);
		}
		// make bottom row
		y_coord = EMPLOYEE_Y_COORDS[num_y_coords - 1];
		for (double x_coord: EMPLOYEE_X_COORDS) {
			addEmployee(x_coord, y_coord);
		}
		// make left column
		double x_coord = EMPLOYEE_X_COORDS[0];
		for (int i = 1; i < num_y_coords - 1; i++) {
			y_coord = EMPLOYEE_Y_COORDS[i];
			addEmployee(x_coord, y_coord);
		}
		// make right column
		x_coord = EMPLOYEE_X_COORDS[num_x_coords - 1];
		for (int i = 1; i < num_x_coords - 1; i++) {
			y_coord = EMPLOYEE_Y_COORDS[i];
			addEmployee(x_coord, y_coord);
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
	
	// return true if all asleep
	public boolean makeEmployeeSleep() {
		int num_awake = awake_employees.size();
		if (num_awake == 0)
			return true;
		int pick_index = rand_num_gen.nextInt(num_awake);
		int employee_index = awake_employees.get(pick_index);
		
		Employee e = employees.get(employee_index);
		boolean previouslyAwake = e.putEmployeeToSleep();
		if (!previouslyAwake)
			System.out.println("Error");
		
		awake_employees.remove(pick_index);
		return false;
	}
	
	public void wakeEmployee(int index) {
		if (!awake_employees.contains(index)) {
			employees.get(index).wakeEmployee();
			awake_employees.add(index);
		}	
	}
	
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
