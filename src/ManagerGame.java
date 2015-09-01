import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ManagerGame {
	public static final String TITLE = "Manager Game";
	public static final double MANAGER_VELOCITY = 200;
	
	private static final double BANNER_HEIGHT = 100;
	private static final double MIN_TIME_UNTIL_NEXT_SLEEP = .2;
	private static final double MAX_TIME_UNTIL_NEXT_SLEEP = 1;
	private static final double TIME_GIVEN = 60;
	
	private Scene myScene;
	private Manager manager;
	private Group manager_grp;
	private ProjectileList projectile_list;
	private boolean space_being_pressed;
	private EmployeeList employee_list;
	
	private double total_time_left;
	private double remainingTimeUntilNextSleep;
	private Random rand_num_gen;
	private Text timer_label;
	
	private boolean game_over;
	private boolean game_won;
	
	private ArrayList<String> inputs;
	
	/**
     * Returns name of the game.
     */
    public String getTitle () {
        return TITLE;
    }
    
    public Scene init (int width, int height) {
    	
        // Create a scene graph to organize the scene
        Group root = new Group();
        // Create a place to see node elements
        myScene = new Scene(root, width, height + BANNER_HEIGHT, Color.WHITE);
        // Create game screen root
        Group screen_root = new Group();
        screen_root.setLayoutY(BANNER_HEIGHT);
        // Attach game screen root to root
        root.getChildren().add(screen_root);
        
        manager = new Manager();
        manager_grp = manager.getManagerGroup();
        employee_list = new EmployeeList();
        projectile_list = new ProjectileList();

        screen_root.getChildren().add(manager_grp);
        screen_root.getChildren().add(employee_list.getEmployeeListGroup());
        screen_root.getChildren().add(projectile_list.getProjectileListGroup());
        
        
        // respond to keyboard inputs, store in array list
        inputs = new ArrayList<String>();
        
        myScene.setOnKeyPressed(e -> {
			String code = e.getCode().toString();
			if (!inputs.contains(code))
				inputs.add(code);
		});
		
		myScene.setOnKeyReleased(e -> {
			String code = e.getCode().toString();
			inputs.remove(code);
		});

		space_being_pressed = false;

		total_time_left = TIME_GIVEN;
		// Set up timer label
        timer_label = new Text(30, 30, "60");
        Font f = Font.font("Helvetica", FontWeight.BOLD, 24);
        timer_label.setFont(f);
        // Attach timer to root
        root.getChildren().add(timer_label);
        
		remainingTimeUntilNextSleep = 2;
		rand_num_gen = new Random();

		game_over = false;
		game_won = true;
		
        return myScene;
    }
    
    /**
     * Change properties of shapes to animate them
     */
    public void gameStep (double elapsedTime) {
    	if (!game_over) {
    		if (remainingTimeUntilNextSleep <= 0) {
				// make one worker sleep
				boolean allAsleep = employee_list.makeEmployeeSleep();
				if (allAsleep) {
					game_won = false;
					game_over = true;
					System.out.println("Game Lost");
				}
				if(!game_over) {
					// set next time interval until next sleep
					double randNum = rand_num_gen.nextDouble();
					double interval = 
							MAX_TIME_UNTIL_NEXT_SLEEP - MIN_TIME_UNTIL_NEXT_SLEEP;
					remainingTimeUntilNextSleep = 
							MIN_TIME_UNTIL_NEXT_SLEEP + randNum * interval;
				}
			}
			remainingTimeUntilNextSleep -= elapsedTime;
			
			if (!game_over) {
				// Check for collisions
				projectile_list.checkCollisions(employee_list);
		    	arrowMvmnt(elapsedTime);
		    	managerMvmnt(elapsedTime);
				
		    	// fire projectile if necessary
				if (inputs.contains("SPACE")) {
					if (!space_being_pressed) {
						projectile_list.fireProjectile(manager.getArrowAngle(),
								manager_grp.getLayoutX(), manager_grp.getLayoutY());
						space_being_pressed = true;
					}
				} else {
					space_being_pressed = false;
				}
				
				projectile_list.updateProjectiles(elapsedTime);
				
				total_time_left -= elapsedTime;
				
				// update timer
				int seconds_left = ((int) total_time_left / 1) + 1;
				if (seconds_left <= 0) {
					timer_label.setText("0");
					System.out.println("Game Won!");
					game_over = true;
				}
				else	
					timer_label.setText(Integer.toString(seconds_left));
			}
    	}
    }
    
    private void arrowMvmnt(double elapsedTime) {
    	if (!(inputs.contains("LEFT") && inputs.contains("RIGHT"))) {
    		if (inputs.contains("LEFT")) {
    			manager.moveArrowCCW(elapsedTime);
    		}
    		if (inputs.contains("RIGHT")) {
    			manager.moveArrowCW(elapsedTime);
    		}
    	}
    }
    
    private void managerMvmnt(double elapsedTime) {
    	double manager_x_pos = manager_grp.getLayoutX();
    	double manager_y_pos = manager_grp.getLayoutY();
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
    	manager_grp.setLayoutX(manager_x_pos + translate_x);
		manager_grp.setLayoutY(manager_y_pos + translate_y);
    }
}
