import javafx.scene.Group;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Rotate;

public class Manager {
	/** this is public so that Projectile as well as
	 *  ManagerGame (for advanced movement of manager)
	 *  can use the size of the manager
	 */
	public static final double MANAGER_GROUP_LENGTH = 200;
	
	private final String MANAGER_IMG_FILE_NAME = "manager.png";
	private final String ARROW_IMG_FILE_NAME = "arrow.png";
	private final double MANAGER_HEIGHT = 100;
	private final double MANAGER_WIDTH = 50;
	private final double ARROW_HEIGHT = 20;
	private final double ARROW_WIDTH = 20;
	
	private double ARROW_VELOCITY = 150;
	
	private Group manager;
	private ImageView manager_img_view;
	private ImageView arrow_img_view;
	private double arrow_angle;
	private Rotate arrow_rotate_transform;
	
	public Manager() {
		Image manager_img = getImage(MANAGER_IMG_FILE_NAME);
		Image arrow_img = getImage(ARROW_IMG_FILE_NAME);
		
		manager_img_view = new ImageView(manager_img);
		arrow_img_view = new ImageView(arrow_img);
		
		manager_img_view.setFitHeight(MANAGER_HEIGHT);
		manager_img_view.setFitWidth(MANAGER_WIDTH);
		arrow_img_view.setFitHeight(ARROW_HEIGHT);
		arrow_img_view.setFitWidth(ARROW_WIDTH);
		
		manager_img_view.setX(MANAGER_GROUP_LENGTH / 2 - MANAGER_WIDTH / 2);
		manager_img_view.setY(MANAGER_GROUP_LENGTH / 2 - MANAGER_HEIGHT / 2);
		arrow_img_view.setX(MANAGER_GROUP_LENGTH / 2 - ARROW_WIDTH / 2);
		arrow_img_view.setY(0);
		
		arrow_angle = 0;
		arrow_rotate_transform = new Rotate(arrow_angle, MANAGER_GROUP_LENGTH / 2,
				MANAGER_GROUP_LENGTH / 2);
		arrow_img_view.getTransforms().add(arrow_rotate_transform);
		
		manager = new Group();
		manager.getChildren().add(manager_img_view);
		manager.getChildren().add(arrow_img_view);
	}
	
	public Group getManagerGroup() {
		return manager;
	}
	
	public void moveArrowCW(double elapsedTime) {
		arrow_angle += ARROW_VELOCITY * elapsedTime;
		arrow_angle = arrow_angle % 360;
		updateArrow();
	}
	
	public void moveArrowCCW(double elapsedTime) {
		arrow_angle -= ARROW_VELOCITY * elapsedTime;
		arrow_angle = arrow_angle % 360;
		// keep angle positive
		if (arrow_angle < 0) {
			arrow_angle = 360 + arrow_angle;
		}
		updateArrow();
	}
	
	public double getArrowAngle() {
		return arrow_angle;
	}
	
	private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
	
	private void updateArrow() {
		arrow_rotate_transform.setAngle(arrow_angle);
	}
}
