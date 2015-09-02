import java.util.ArrayList;

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
	
	// manager mvmnt parameters, normal mode
	private double MANAGER_VELOCITY = 200;
	// manager mvmnt parameters, advanced mode
	private double manager_velocity_x = 0;
	private double manager_velocity_y = 0;
	private double MANAGER_ACCELERATION = 500;
	// arrow
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
	
	public double getArrowAngle() {
		return arrow_angle;
	}
	
    public void moveManager(double elapsedTime, ArrayList<String> inputs) {
    	double manager_x_pos = manager.getLayoutX();
    	double manager_y_pos = manager.getLayoutY();
    	double translate_dist = elapsedTime * MANAGER_VELOCITY;
    	double translate_x = 0;
    	double translate_y = 0;
    	for (String i: inputs) {
    		switch(i) {
    			case "W": // up
    				translate_y -= translate_dist;
    				break;
    			case "A": // left
    				translate_x -= translate_dist;
    				break;
    			case "S": // down
    				translate_y += translate_dist;
    				break;
    			case "D": // right
    				translate_x += translate_dist;
    				break;
    		}
    	}
    	manager.setLayoutX(manager_x_pos + translate_x);
		manager.setLayoutY(manager_y_pos + translate_y);
    }
    
    public void moveManagerAdvanced(double elapsedTime,
    		ArrayList<String> inputs, double game_screen_width,
    		double game_screen_height) {
    	double manager_x_pos = manager.getLayoutX();
    	double manager_y_pos = manager.getLayoutY();
    	double delta_velocity = elapsedTime * MANAGER_ACCELERATION;
    	// change velocity based on input
    	for (String i: inputs) {
    		switch(i) {
    			case "W": // up
    				manager_velocity_y -= delta_velocity;
    				break;
    			case "A": // left
    				manager_velocity_x -= delta_velocity;
    				break;
    			case "S": // down
    				manager_velocity_y += delta_velocity;
    				break;
    			case "D": // right
    				manager_velocity_x += delta_velocity;
    				break;
    		}
    	}
    	// check for collision with edge
    	if (manager_x_pos <= 0 || manager_x_pos + Manager.MANAGER_GROUP_LENGTH
    			>= game_screen_width) { // left and right edge
    		manager_velocity_x = -manager_velocity_x;
    	}
    	if (manager_y_pos <= 0 || manager_y_pos + Manager.MANAGER_GROUP_LENGTH
    			>= game_screen_height) { // top and bottom edge
    		manager_velocity_y = -manager_velocity_y;
    	}

    	double translate_x = elapsedTime * manager_velocity_x;
    	double translate_y = elapsedTime * manager_velocity_y;
    	manager.setLayoutX(manager_x_pos + translate_x);
		manager.setLayoutY(manager_y_pos + translate_y);
    }
    
    public void moveArrow(double elapsedTime, ArrayList<String> inputs) {
    	if (!(inputs.contains("LEFT") && inputs.contains("RIGHT"))) {
    		if (inputs.contains("LEFT")) {
    			moveArrowCCW(elapsedTime);
    		}
    		if (inputs.contains("RIGHT")) {
    			moveArrowCW(elapsedTime);
    		}
    	}
    }
    
	
	private void moveArrowCW(double elapsedTime) {
		arrow_angle += ARROW_VELOCITY * elapsedTime;
		arrow_angle = arrow_angle % 360;
		updateArrow();
	}
	
	private void moveArrowCCW(double elapsedTime) {
		arrow_angle -= ARROW_VELOCITY * elapsedTime;
		arrow_angle = arrow_angle % 360;
		// keep angle positive
		if (arrow_angle < 0) {
			arrow_angle = 360 + arrow_angle;
		}
		updateArrow();
	}
	
	private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
	
	private void updateArrow() {
		arrow_rotate_transform.setAngle(arrow_angle);
	}
}
