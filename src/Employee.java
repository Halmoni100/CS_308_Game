import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

public class Employee {
	private final double EMPLOYEE_HEIGHT = 100;
	private final double EMPLOYEE_WIDTH = 100;
	private final double HIT_BOX_HEIGHT = 50;
	private final double HIT_BOX_WIDTH = 50;
	private final double HIT_BOX_X_COORD = 25;
	private final double HIT_BOX_Y_COORD = 15;
	
	
	private Group employee;
	private ImageView employee_img_view;
	private Image employee_awake_img;
	private Image employee_asleep_img;
	private Rectangle hit_box;
	private boolean awake;
	
	public Employee(Image awake_img, Image asleep_img) {
		employee = new Group();
		
		employee_awake_img = awake_img;
		employee_asleep_img = asleep_img;
		
		employee_img_view = new ImageView(employee_awake_img);
		employee_img_view.setFitWidth(EMPLOYEE_WIDTH);
		employee_img_view.setFitHeight(EMPLOYEE_HEIGHT);
		awake = true;
		
		hit_box = new Rectangle(HIT_BOX_WIDTH, HIT_BOX_HEIGHT);
		hit_box.setFill(Color.TRANSPARENT);
		hit_box.setStroke(Color.RED);
		hit_box.setStrokeWidth(2);
		
		employee.getChildren().add(employee_img_view);
		employee.getChildren().add(hit_box);
		
		hit_box.setX(HIT_BOX_X_COORD);
		hit_box.setY(HIT_BOX_Y_COORD);
	}
	
	public Rectangle getHitBox() {
		return hit_box;
	}
	
	public Group getEmployee() {
		return employee;
	}
	
	// return true if previously awake
	public boolean putEmployeeToSleep() {
		if (!awake)
			return false;
		employee_img_view.setImage(employee_asleep_img);
		awake = !awake;
		return true;
	}
	
	// return true if previously asleep
	public boolean wakeEmployee() {
		if (awake)
			return false;
		employee_img_view.setImage(employee_awake_img);
		awake = !awake;
		return true;
	}
}
