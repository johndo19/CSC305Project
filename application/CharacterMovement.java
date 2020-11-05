package application;



import javafx.animation.AnimationTimer;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.*;
import javafx.scene.image.*;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 * Hold down an arrow key to have your car drive around the screen. Make sure to avoid the other car!
 * Hold down the shift key to have the driver step on the gas.
 */
public class CharacterMovement extends Application {

    private static final double W = 956, H = 740;

    private static final String CAR_IMAGE_LOC =
            "https://i.imgur.com/3rROvF9.png";
    
    private static final String CAR_IMAGE_LOC_2 =
            "https://i.imgur.com/ljYvkE4.png";
    
    private static final String SCENE_IMAGE_LOC = 
    		"https://i.imgur.com/qGoTeED.png";
    
    private static final String PAUSE_LOC = 
    		"https://i.imgur.com/n7HbHdB.png";
    
    private Image carImage;
    private Node car;
    
    private Image car2Image;
    private Node car2;
    
    private Image raceImage;
    private Node race;

    private Image pauseImage;
    private Node pause;
    int counter = 0;
    boolean running, goNorth, goSouth, goEast, goWest;

    @Override
    public void start(Stage stage) throws Exception {
        carImage = new Image(CAR_IMAGE_LOC);
        car = new ImageView(carImage);
        
        
        car2Image = new Image(CAR_IMAGE_LOC_2);
        car2 = new ImageView(car2Image);
        
        raceImage = new Image(SCENE_IMAGE_LOC);
        race = new ImageView(raceImage);
        
        pauseImage = new Image(PAUSE_LOC);
        pause = new ImageView(pauseImage);
        
        Group game = new Group(race, car, car2, pause);
      
        moveCarTo(W / 1.3, H / 2);
        moveCarTo2(W / 6, H / 2);
        Scene scene = new Scene(game, W, H);
        
        scene.setOnKeyPressed(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    goNorth = true; break;
                    case DOWN:  goSouth = true; break;
                    case LEFT:  goWest  = true; break;
                    case RIGHT: goEast  = true; break;
                    case SHIFT: running = true; break;
                }
            }
        });

        scene.setOnKeyReleased(new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                switch (event.getCode()) {
                    case UP:    goNorth = false; break;
                    case DOWN:  goSouth = false; break;
                    case LEFT:  goWest  = false; break;
                    case RIGHT: goEast  = false; break;
                    case SHIFT: running = false; break;
                }
            }
        });

        stage.setScene(scene);
        stage.show();

        AnimationTimer timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                int dx = 0, dy = 0;
                
                if (goNorth) dy -= 1;
                if (goSouth) dy += 1;
                if (goEast)  dx += 1;
                if (goWest)  dx -= 1;
                if (running) { dx *= 3; dy *= 3; }

                moveCarBy(dx, dy);
                double distance = giveChase();
                if (distance <= 75.0){
                	counter++;
                }
                if (counter >= 5000) {
                	
                }
//                if (isTouching(car, car2)) {
//                	car.relocate((W / 1.3),  (H / 2));
//                }
                
            }
        };
        
        timer.start();
        
    }

    private void moveCarBy(int dx, int dy) {
        if (dx == 0 && dy == 0) return;

        final double cx = car.getBoundsInLocal().getWidth()  / 2;
        final double cy = car.getBoundsInLocal().getHeight() / 2;

        double x = cx + car.getLayoutX() + dx;
        double y = cy + car.getLayoutY() + dy;

        moveCarTo(x, y);
    }
    


    private void moveCarTo(double x, double y) {
        final double cx = car.getBoundsInLocal().getWidth()  / 2;
        final double cy = car.getBoundsInLocal().getHeight() / 2;

        if (x - cx >= 0 &&
            x + cx <= W &&
            y - cy >= 0 &&
            y + cy <= H) {
            car.relocate(x - cx, y - cy);
        }
    }
    
    private void moveCarTo2(double x, double y) {
        final double cx = car2.getBoundsInLocal().getWidth()  / 2;
        final double cy = car2.getBoundsInLocal().getHeight() / 2;

        if (x - cx >= 0 &&
            x + cx <= W &&
            y - cy >= 0 &&
            y + cy <= H) {
            car2.relocate(x - cx, y - cy);
        }
    }
    //Method used to calculate how far the second car needs to move to chase the player
    //Method then relocates second car accordingly. Returns the direct distance between the center points as well
    private double giveChase() {
    	double C2x = car2.getLayoutX();
    	double C2y = car2.getLayoutY();
    	double C1x = car.getLayoutX();
    	double C1y = car.getLayoutY();
    	double xDistance = C1x - C2x;
    	double yDistance = C1y - C2y;
    	double compDistance = Math.sqrt((xDistance * xDistance) + (yDistance * yDistance));
    	car2.relocate(C2x + (xDistance/ 250), C2y + (yDistance / 250));
    	return compDistance;
    }
    //Helper method for isTouching(), which determines the minX, minY, maxX, and maxY
    //of the specified car, and inserts them in an array for easy use by isTouching()
    private double[] getBounds(Node car) {
    	double[] boundsArray = new double[3];
    	double halfX = car.getBoundsInLocal().getWidth() / 2;
    	double halfY = car.getBoundsInLocal().getHeight() / 2;
    	boundsArray[0] = car.getLayoutX() - halfX;
    	boundsArray[1] = car.getLayoutY() - halfY;
    	boundsArray[2] = car.getLayoutX() + halfX;
    	boundsArray[3] = car.getLayoutY() + halfY;
    	return boundsArray;
    	
    }
    //Method designed to determine if the two cars are touching. 
    //if touching return true, else return false
     private boolean isTouching(Node car, Node car2) {
    	 double[] carBounds = getBounds(car);
    	 double[] car2Bounds = getBounds(car2);
    	 if(carBounds[3] < car2Bounds[0] || carBounds[0] > car2Bounds[3]) {
    		 if(carBounds[4] < car2Bounds[1] || carBounds[1] >  car2Bounds[4]) {
    			 return false;
    		 }
    	 }
    	 return true;
     }

    public static void main(String[] args) { launch(args); }
}