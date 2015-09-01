import javafx.scene.image.Image;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Shape;

public class Projectile {
	public static final double PROJECTILE_RADIUS = 25;
	
	private static final double PROJECTILE_VELOCITY = 300;
	
	private Circle projectile_circle;
	private double x_velocity;
	private double y_velocity;
	
	public Projectile(double angle, double manager_x_pos, double manager_y_pos,
			Image projectile_img) {
		projectile_circle = new Circle(PROJECTILE_RADIUS);
		projectile_circle.setFill(new ImagePattern(projectile_img));
		
		setInitialPos(angle, manager_x_pos, manager_y_pos);
		
		x_velocity = PROJECTILE_VELOCITY * Math.cos(degToRad(90 - angle));
		y_velocity = PROJECTILE_VELOCITY * Math.sin(degToRad(90 - angle));
	}
	
	public Circle getProjectileCircle() {
		return projectile_circle;
	}
	
	public boolean updatePos(double elapsedTime) {
		// return true if at edge
		double x_pos = projectile_circle.getCenterX();
		double y_pos = projectile_circle.getCenterY();
		double next_x_pos = x_pos + elapsedTime * x_velocity;
		double next_y_pos = y_pos - elapsedTime * y_velocity;
		if (next_x_pos < 0 || next_x_pos > Main.SIZE_WIDTH
				|| next_y_pos < 0 || next_y_pos > Main.SIZE_HEIGHT) {
			return true;
		}
		projectile_circle.setCenterX(next_x_pos);
		projectile_circle.setCenterY(next_y_pos);

		return false;
	}
	
	public boolean checkHit(Shape hit_box) {
		Shape intersect = Shape.intersect(hit_box, projectile_circle);
		if (intersect.getBoundsInLocal().getWidth() != -1)
			return true;
		return false;
	}
	
	private void setInitialPos(double angle, double manager_x_pos,
			double manager_y_pos) {
		double l = Manager.MANAGER_GROUP_LENGTH / 2 + PROJECTILE_RADIUS;
		double x = l * Math.cos(degToRad(90 - angle));
		double y = l * Math.sin(degToRad(90 - angle));
		
		double init_x_pos = manager_x_pos + Manager.MANAGER_GROUP_LENGTH / 2 + x;
		double init_y_pos = manager_y_pos + Manager.MANAGER_GROUP_LENGTH / 2 - y;
		
		projectile_circle.setCenterX(init_x_pos);
		projectile_circle.setCenterY(init_y_pos);
	}
	
	private double degToRad(double degrees) {
		return degrees * Math.PI / 180;
	}

}
