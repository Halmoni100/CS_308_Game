import java.util.ArrayList;
import java.util.Random;

import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.Text;

public class ManagerGame {
	public static final String TITLE = "Manager Game";
	
	private double game_screen_width;
	private double game_screen_height;
	
	// file names, node dimensions
	private final String SPLASH_SCREEN_IMG_FILE_NAME = "splash_screen.png";
	private final String BACKGROUND_IMG_FILE_NAME = "background.jpg";
	private final double BANNER_HEIGHT = 100;
	private final double TIMER_TEXT_X_POS = 30;
	private final double TIMER_TEXT_Y_POS = 30;
	private final double RESULT_TEXT_X_POS = 300;
	private final double RESULT_TEXT_Y_POS = 30;
	
	private double MIN_TIME_UNTIL_NEXT_SLEEP = 4;
	private double MAX_TIME_UNTIL_NEXT_SLEEP = 5;
	private double TIME_GIVEN = 60;
	
	// manager mvmnt parameters, normal mode
	private double MANAGER_VELOCITY = 200;
	// manager mvmnt parameters, advanced mode
	private double manager_velocity_x = 0;
	private double manager_velocity_y = 0;
	private double MANAGER_ACCELERATION = 500;
	
	private ImageView splash_screen_img_view;
	private Group root;
	private Group game_root;
	private Scene myScene;
	private Manager manager;
	private Group manager_grp;
	private ProjectileList projectile_list;
	private boolean space_being_pressed;
	private EmployeeList employee_list;
	
	private double total_time_left;
	private double remainingTimeUntilNextSleep;
	private Random rand_num_gen;
	private Group banner_grp;
	private Text timer_label;
	private Text game_result_label;
	
	private boolean game_started;
	private boolean game_over;
	
	private ArrayList<String> inputs;
	
	/**
     * Returns name of the game.
     */
    public String getTitle () {
        return TITLE;
    }
    
    public Scene init (int width, int height) {
    	game_screen_width = width;
    	game_screen_height = height;
    	
        root = new Group();
        game_root = new Group();
        // Create a place to see node elements
        myScene = new Scene(root, game_screen_width,
        		game_screen_height + BANNER_HEIGHT, Color.WHITE);
        // Create game screen root
        Group play_screen_root = new Group();
        play_screen_root.setLayoutY(BANNER_HEIGHT);
        // Add background
        Image background_img = getImage(BACKGROUND_IMG_FILE_NAME);
        ImageView background_img_view = new ImageView(background_img);
        background_img_view.setFitWidth(game_screen_width);
        background_img_view.setFitHeight(game_screen_height);
        play_screen_root.getChildren().add(background_img_view);
        
        manager = new Manager();
        manager_grp = manager.getManagerGroup();
        employee_list = new EmployeeList();
        projectile_list = new ProjectileList();

        play_screen_root.getChildren().add(manager_grp);
        play_screen_root.getChildren().add(employee_list.getEmployeeListGroup());
        play_screen_root.getChildren().add(projectile_list.getProjectileListGroup());
        game_root.getChildren().add(play_screen_root);
        manager_grp.setLayoutX(30);
        manager_grp.setLayoutY(30);
        
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
        timer_label = new Text(TIMER_TEXT_X_POS, TIMER_TEXT_Y_POS, "60");
        Font f = Font.font("Helvetica", FontWeight.BOLD, 24);
        timer_label.setFont(f);
        // Set up game result label
        game_result_label = new Text(RESULT_TEXT_X_POS, RESULT_TEXT_Y_POS, "");
        game_result_label.setFont(f);
        // Attach timer and game result labels to banner group
        banner_grp = new Group();
        banner_grp.getChildren().add(timer_label);
        banner_grp.getChildren().add(game_result_label);
        // Attach banner group to root
        game_root.getChildren().add(banner_grp);
        
		remainingTimeUntilNextSleep = 2;
		rand_num_gen = new Random();

		game_started = false;
		game_over = false;
		
		// Attach splash screen
		Image splash_screen_img = getImage(SPLASH_SCREEN_IMG_FILE_NAME);
		splash_screen_img_view = new ImageView(splash_screen_img);
		splash_screen_img_view.setFitHeight(height);
		splash_screen_img_view.setFitWidth(width);
		root.getChildren().add(splash_screen_img_view);
		
        return myScene;
    }
    
    /**
     * Change properties of shapes to animate them
     */
    public void step (double elapsedTime) {
    	if (!game_started) {
    		if (inputs.contains("ENTER")) {
    			root.getChildren().remove(splash_screen_img_view);
    			root.getChildren().add(game_root);
    			game_started = true;
    		}
    	} else {
    		doGameStep(elapsedTime);
    	}
    }
    
    private void doGameStep(double elapsedTime) {
    	if (!game_over) {
    		if (remainingTimeUntilNextSleep <= 0) {
				// make one worker sleep
				boolean allAsleep = employee_list.makeEmployeeSleep();
				if (allAsleep) {
					game_result_label.setText("Game Lost");
					game_over = true;
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
					timer_label.setText("Time left: 0s");
					game_result_label.setText("Game won!");
					game_over = true;
				}
				else {
					String timer_text =
							"Time left: " + Integer.toString(seconds_left) + "s";
					timer_label.setText(timer_text);
				}
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
    
    private void managerMvmntAdvanced(double elapsedTime) {
    	double manager_x_pos = manager_grp.getLayoutX();
    	double manager_y_pos = manager_grp.getLayoutY();
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
    	manager_grp.setLayoutX(manager_x_pos + translate_x);
		manager_grp.setLayoutY(manager_y_pos + translate_y);
    }
    
    private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
}
