/** Note to self
 * 	Make method to deal with single key presses!!
 */


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

	private final String SPLASH_SCREEN_IMG_FILE_NAME = "splash_screen.png";
	private final String RESET_SCREEN_IMG_FILE_NAME = "reset_screen.png";
	private final String BACKGROUND_IMG_FILE_NAME = "background_linoleum.png";
	private final String BANNER_BACKGROUND_IMG_FILE_NAME = "banner_background.png";
	private final double MODE_TEXT_X_POS = 100;
	private final double MODE_TEXT_Y_POS = 700;
	private final double BANNER_HEIGHT = 200;
	private final double TIMER_TEXT_X_POS = 30;
	private final double TIMER_TEXT_Y_POS = 30;
	private final double RESULT_TEXT_X_POS = 250;
	private final double RESULT_TEXT_Y_POS = 30;
	private final double cheat_label_X_POS = 500;
	private final double cheat_label_Y_POS = 30;
	private final double MANAGER_START_X = 30;
	private final double MANAGER_START_Y = 30;
	private final double CHEAT_LABEL_TIME = 2;
	
	private double MIN_TIME_UNTIL_NEXT_SLEEP = 1;
	private double MAX_TIME_UNTIL_NEXT_SLEEP = 3;
	private double TIME_GIVEN = 50;
	
	private Group splash_screen_grp;
	private Group reset_screen_grp;
	private Text mode_label;
	private Group root;
	private Group game_root;
	private Scene myScene;
	private Manager manager;
	private Group manager_grp;
	private ProjectileList projectile_list;
	private EmployeeList employee_list;
	
	private ArrayList<String> keys_being_pressed;
	
	private double total_time_left;
	private double remainingTimeUntilNextSleep;
	private Random rand_num_gen;
	private Group banner_grp;
	private Text timer_label;
	private Text game_result_label;
	private Text cheat_label;
	private double cheat_label_time_left;
	
	private boolean manager_mvmnt_advanced;
	private boolean game_started;
	private boolean game_over;
	private boolean reset_mode;
	private boolean cheat_label_activated;
	
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
        
        manager_mvmnt_advanced = false;
        // Attach splash screen
 		Image splash_screen_img = getImage(SPLASH_SCREEN_IMG_FILE_NAME);
 		ImageView splash_screen_img_view = new ImageView(splash_screen_img);
 		splash_screen_img_view.setFitHeight(height + BANNER_HEIGHT);
 		splash_screen_img_view.setFitWidth(width);
 		splash_screen_grp = new Group();
 		mode_label = new Text(MODE_TEXT_X_POS, MODE_TEXT_Y_POS,
 				"Movement Mode (Press M to change): Basic");
 		Font f = Font.font("Helvetica", FontWeight.BOLD, 24);
 		mode_label.setFont(f);
 		splash_screen_grp.getChildren().add(splash_screen_img_view);
 		splash_screen_grp.getChildren().add(mode_label);
 		root.getChildren().add(splash_screen_grp);
 		
        game_root = new Group();
        myScene = new Scene(root, game_screen_width,
        		game_screen_height + BANNER_HEIGHT, Color.WHITE);

        Group play_screen_root = new Group();
        play_screen_root.setLayoutY(BANNER_HEIGHT);

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
        manager_grp.setLayoutX(MANAGER_START_X);
        manager_grp.setLayoutY(MANAGER_START_Y);
        
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

        timer_label = new Text(TIMER_TEXT_X_POS, TIMER_TEXT_Y_POS, "");
        timer_label.setFont(f);

        game_result_label = new Text(RESULT_TEXT_X_POS, RESULT_TEXT_Y_POS, "");
        game_result_label.setFont(f);
        cheat_label = new Text(cheat_label_X_POS, cheat_label_Y_POS, "");
        cheat_label.setFont(f);

        Image banner_background_img = getImage(BANNER_BACKGROUND_IMG_FILE_NAME);
        ImageView banner_background_img_view =
        		new ImageView(banner_background_img);
        banner_background_img_view.setFitWidth(width);
        banner_background_img_view.setFitHeight(BANNER_HEIGHT);
        banner_grp = new Group();
        banner_grp.getChildren().add(banner_background_img_view);
        banner_grp.getChildren().add(timer_label);
        banner_grp.getChildren().add(game_result_label);
        banner_grp.getChildren().add(cheat_label);
        game_root.getChildren().add(banner_grp);

		rand_num_gen = new Random();

		// Set up reset screen
		Image reset_screen_img = getImage(RESET_SCREEN_IMG_FILE_NAME);
		ImageView reset_screen_img_view = new ImageView(reset_screen_img);
		reset_screen_img_view.setFitHeight(height + BANNER_HEIGHT);
		reset_screen_img_view.setFitWidth(width);
		reset_screen_grp = new Group();
		reset_screen_grp.getChildren().add(reset_screen_img_view);
		
		keys_being_pressed = new ArrayList<String>();
		setupGame();
		reset_mode = false;
		cheat_label_activated = false;

        return myScene;
    }
    
    private void setupGame() {
		total_time_left = TIME_GIVEN;
		updateTimerLabel();
		
		remainingTimeUntilNextSleep = MAX_TIME_UNTIL_NEXT_SLEEP;
		
		game_started = false;
		game_over = false;
    }
    
    private void resetGame() {
    	setupGame();
    	employee_list.wakeAllEmployees();
    	projectile_list.clearProjectiles();
    	manager_grp.setLayoutX(MANAGER_START_X);
        manager_grp.setLayoutY(MANAGER_START_Y);
        game_result_label.setText("");
        cheat_label.setText("");
    }
    
    /**
     * Change properties of shapes to animate them
     */
    public void step (double elapsedTime) {
    	if (!game_started) {
    		if (keyPressed("ENTER")) {
    			if (reset_mode) {
    				root.getChildren().remove(reset_screen_grp);
    				reset_mode = false;
    			} else {
    				splash_screen_grp.getChildren().remove(mode_label);
    				reset_screen_grp.getChildren().add(mode_label);
    				root.getChildren().remove(splash_screen_grp);
    			}
    			root.getChildren().add(game_root);
    			game_started = true;
    		} else if (keyPressed("M")) {
    			manager_mvmnt_advanced = !manager_mvmnt_advanced;
    			if (!manager_mvmnt_advanced) {
    				mode_label.setText("Movement Mode (Press M to change): Basic");
    			} else {
    				mode_label.setText("Movement Mode (Press M to change): Advanced");
    			}
    		}
    	} else {
    		if (keyPressed("R")) {
    			reset_mode = true;
    			root.getChildren().remove(game_root);
    			root.getChildren().add(reset_screen_grp);
    			resetGame();
    		} else {
    			if (keyPressed("P")) {
    				employee_list.wakeAllEmployees();
    				cheat_label_activated = true;
    				cheat_label_time_left = CHEAT_LABEL_TIME;
    				cheat_label.setText("Cheat 'P' used: All employees wakened");
    			}
    			if (keyPressed("O")) {
    				employee_list.makeMostFallAsleep();
    				cheat_label_activated = true;
    				cheat_label_time_left = CHEAT_LABEL_TIME;
    				cheat_label.setText("Cheat 'O' used: Most employees sent to sleep");
    			}
    			if (cheat_label_activated) {
    				if (cheat_label_time_left <= 0) {
    					cheat_label_activated = false;
    					cheat_label.setText("");
    				} else {
    					cheat_label_time_left -= elapsedTime;
    				}
    			}
    			doGameStep(elapsedTime);
    		}
    	}
    	checkKeysBeingPressed();
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
				
				// Update manager
		    	manager.moveArrow(elapsedTime, inputs);
		    	if (!manager_mvmnt_advanced) {
		    		manager.moveManager(elapsedTime, inputs);
		    	} else {
		    		manager.moveManagerAdvanced(elapsedTime, inputs,
		    				game_screen_width, game_screen_height);
		    	}
				
		    	// fire projectile if necessary
		    	if (keyPressed("SPACE")) {
		    		projectile_list.fireProjectile(manager.getArrowAngle(),
							manager_grp.getLayoutX(), manager_grp.getLayoutY());
		    	}

				// update projectiles
				projectile_list.updateProjectiles(elapsedTime);

				// update timer
				int seconds_left = getSecondsLeft();
				if (seconds_left <= 0) {
					timer_label.setText("Time left: 0s");
					game_result_label.setText("Game won!");
					game_over = true;
				}
				else {
					updateTimerLabel();
				}
				
				total_time_left -= elapsedTime;
			}
    	}
    }
    
    private int getSecondsLeft() {
    	return ((int) total_time_left / 1) + 1;
    }
    
    private void updateTimerLabel() {
    	int seconds_left = getSecondsLeft();
    	String timer_text =
				"Time left: " + Integer.toString(seconds_left) + "s";
		timer_label.setText(timer_text);
    }
    
    private void checkKeysBeingPressed() {
    	int num_keys_being_pressed = keys_being_pressed.size();
    	int current_index = 0;
    	for (int i = 0; i < num_keys_being_pressed; i++) {
    		String key_pressed = keys_being_pressed.get(current_index);
    		if (!inputs.contains(key_pressed))
    			keys_being_pressed.remove(current_index);
    		else
    			current_index++;
    	}
    	for (String i: inputs) {
    		if (!keys_being_pressed.contains(i))
    			keys_being_pressed.add(i);
    	}
    }
    
    private boolean keyPressed(String key_code) {
    	return inputs.contains(key_code) && !keys_being_pressed.contains(key_code);
    }
    
    private Image getImage(String file_name) {
		return new Image(getClass().getClassLoader().getResourceAsStream(file_name));
	}
}
