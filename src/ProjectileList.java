import java.util.ArrayList;

import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.shape.Shape;

public class ProjectileList {
	private static final String PROJECTILE_IMG_FILE_NAME = "paper_crumple.jpg";
	
	private Group projectile_group;
	private ArrayList<Projectile> projectiles;
	private int num_projectiles;
	private Image projectile_img;
	
	public ProjectileList() {
		projectile_group = new Group();
		projectiles = new ArrayList<Projectile>();
		num_projectiles = 0;
		projectile_img = getImage(PROJECTILE_IMG_FILE_NAME);
	}
	
	public Group getProjectileListGroup() {
		return projectile_group;
	}
	
	public ArrayList<Projectile> getProjectiles() {
		return projectiles;
	}
	
	public int getNumProjectiles() {
		return num_projectiles;
	}
	
	public void fireProjectile(double angle, double manager_x_pos,
			double manager_y_pos) {
		Projectile p = new Projectile(angle, manager_x_pos,
				manager_y_pos, projectile_img);
		projectiles.add(p);
		num_projectiles++;
		projectile_group.getChildren().add(p.getProjectileCircle());
	}
	
	public void clearProjectiles() {
		int current_num_projectiles = num_projectiles;
		for (int i = 0; i < current_num_projectiles; i++) {
			Projectile p = projectiles.get(0);
			removeProjectile(p);
		}	
	}
	
	public void updateProjectiles(double elapsedTime) {
		int num_projectiles = projectiles.size();
		int currentIndex = 0;
		for (int i = 0; i < num_projectiles; i++) {
			Projectile p = projectiles.get(currentIndex);		
			boolean atEdge = p.updatePos(elapsedTime);
			if (atEdge) {
				removeProjectile(p);
			} else {
				currentIndex++;
			}
		}
	}
	
	public void checkCollisions(EmployeeList employee_list) {
		int num_employees = employee_list.getNumEmployees();
		ArrayList<Employee> employees =  employee_list.getEmployees();
		for (int i = 0; i < num_employees; i++) {
			for (int j = 0; j < num_projectiles; j++) {
				Employee e = employees.get(i);
				Projectile p = projectiles.get(j);
				Shape intersect = Shape.intersect(
						e.getHitBox(), p.getProjectileCircle());
		        if (intersect.getBoundsInLocal().getWidth() != -1) {
		            employee_list.wakeEmployee(i);
		            removeProjectile(p);
		        }
			}
		}
	}
	
	private void removeProjectile(Projectile p) {
		projectile_group.getChildren().remove(p.getProjectileCircle());
		projectiles.remove(p);
		num_projectiles--;
	}
	
    private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
}
