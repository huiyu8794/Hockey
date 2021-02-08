package hockey;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.media.AudioClip;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Rectangle;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.event.EventHandler;
import javafx.event.ActionEvent;
import javafx.animation.Timeline;
import javafx.animation.KeyFrame;
import javafx.util.Duration;

/**
 *
 * @author Leo Huang
 */
public class Hockey extends Application {
  Canvas canvas;
  GraphicsContext gc;
  
  public int location;
  int score1=0, score2=0; 
  
  Ball ball;  
  Paddle player, computer; 
  
  Image imgRScore, imgBScore;
  Image imgDigit[];

  AudioClip goClip, loseClip;    

  boolean blnRun = false;
  
  Group root = null;  
  
  Timeline timeline;
  
  @Override
  public void start(Stage primaryStage) {
    canvas = new Canvas(500, 400);

    gc = canvas.getGraphicsContext2D();

    gc.setFill(Color.BLACK);
    gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

    setGameBoard();
    
    paintGameBoard();     

    Stop[] stops = {new Stop(0.0, Color.WHITE), new Stop(0.3, Color.ORANGERED), new Stop(1.0, Color.DARKORANGE)};
    ball = new Ball(10);
    ball.relocate(240, 165);
    ball.setFill(new RadialGradient(0, 0.1, ball.getCenterX() - 4, ball.getCenterY() - 4, 20, false, CycleMethod.NO_CYCLE, stops));
    
    player = new Paddle(false, 15, 175, 10, 50);
    player.setFill(Color.RED);

    computer = new Paddle(true, 475, 175, 10, 50);
    computer.setFill(Color.rgb(0, 255, 255));
    
    root = new Group();  
    root.getChildren().add(canvas);
    root.getChildren().add(player);  
    root.getChildren().add(computer);  
    root.getChildren().add(ball);  
    
    Scene scene = new Scene(root, 500, 400);
    scene.setCursor(Cursor.CROSSHAIR);
    
    initTimeline();    
   
    canvas.addEventHandler(MouseEvent.MOUSE_MOVED, new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent e) {
        int y = (int)e.getY();
        
        if (y > 25 & y < 325)  {
          player.movePaddle(y);
          
          int playerY = player.getPlayerY();

          if (playerY<=151 && playerY>=148)
            playerY = 150;

          int PosY = playerY-175;
  
          player.setLayoutY(PosY);
        }
      }
    });    
    
    canvas.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>() {
      @Override public void handle(MouseEvent e) {
        if (blnRun){
          timeline.pause();
          
          blnRun = false;
        }
        else {
          timeline.play();
          blnRun = true;
        }      
      }
    });        
    
    // Set the title of Stage
    primaryStage.setTitle("Hockey");
    primaryStage.setScene(scene);
    primaryStage.setResizable(false);
    
    // Show Stage
    primaryStage.show();
  }

  private void setGameBoard() {
    score1 = 0;
    score2 = 0;    

    imgDigit = new Image[10];

    for(int i = 0; i < 10; i++) {
      imgDigit[i] = new Image(getClass().getResourceAsStream("images/d" + i + ".gif"));
    }

    imgRScore = new Image(getClass().getResourceAsStream("images/rscore.gif"));
    imgBScore = new Image(getClass().getResourceAsStream("images/bscore.gif"));

    try {
      goClip = new AudioClip(getClass().getResource("audio/go.wav").toString());
      loseClip = new AudioClip(getClass().getResource("audio/lose.wav").toString());
    }
    catch (Exception ex) {
      ex.toString();
    }
  }
  
  private void paintGameBoard() {
    gc.setFill(Color.YELLOW);
    gc.fillRect(0, 0, 500, 8);    
    gc.fillRect(0, 342, 500, 8);  
    gc.fillRect(0, 0, 8, 75);    
    gc.fillRect(0, 275, 8, 75);  
    gc.fillRect(492, 0, 8, 75);  
    gc.fillRect(492, 275, 8, 75);  
    gc.fillOval(180, 105, 140, 140);    

    gc.setFill(Color.BLACK);
    gc.fillOval(184, 109, 132, 132);    

    gc.setFill(Color.YELLOW);
    gc.fillRect(248, 0, 4, 342);    

    // Player Score
    gc.drawImage(imgRScore, 30, 365);
      
    String rScore = String.valueOf(score2) ;

    for (int i=0; i < rScore.length() ; i++) {
      int j = Integer.parseInt(rScore.substring(i, i+1));
      gc.drawImage(imgDigit[j], 80 + 15 * i, 361);
    }

    // Computer Score
    gc.drawImage(imgBScore, 390, 365);
    
    String bScore = String.valueOf(score1) ;
    
    for (int i=0; i < bScore.length() ; i++) {
      int j = Integer.parseInt(bScore.substring(i, i+1));
      gc.drawImage(imgDigit[j], 440 + 15 * i, 361);
    }
  }  
  
  private void initTimeline() {
    timeline = new Timeline(new KeyFrame(Duration.millis(3), new EventHandler<ActionEvent>() {
      double deltaX = 1, deltaY = 1;

      @Override
      public void handle(final ActionEvent e) {
        ball.moveBall();
        ball.setLayoutX(ball.getPosX());
        ball.setLayoutY(ball.getPosY());

        computer.movePaddle(ball);
        
        int computerY = computer.getComputerY();
        
        if (computerY<=151 && computerY>=148)
          computerY = 150;
        
        int PosY = computerY-175;
        
        computer.setLayoutY(PosY);

        location = ball.getLocation();        
        
        if (location == 1) {
          score1 += 1;
          loseClip.play();
          ball.posX = 250 - 10;
          ball.posY = 175 - 10;
          ball.vX = 4;
          ball.vY = -4;
        }
        else if (location == 2)  {
          score2 += 1;
          loseClip.play();
          ball.posX = 250 - 10;
          ball.posY = 175 - 10;
          ball.vX = 4;
          ball.vY = 4;
        }

        if (ball.vx < 0) {
          ball.touchPlayerPaddle(player, goClip);
        }
        else if (ball.vx > 0) {
          ball.touchComputerPaddle(computer, goClip);
        }        

        // Player Score
        gc.drawImage(imgRScore, 30, 365);

        String rScore = String.valueOf(score2) ;

        for (int i=0; i < rScore.length() ; i++) {
          int j = Integer.parseInt(rScore.substring(i, i+1));
          gc.drawImage(imgDigit[j], 80 + 15 * i, 361);
        }
        
        // Computer Score
        gc.drawImage(imgBScore, 390, 365);

        String bScore = String.valueOf(score1) ;

        for (int i=0; i < bScore.length() ; i++) {
          int j = Integer.parseInt(bScore.substring(i, i+1));
          gc.drawImage(imgDigit[j], 440 + 15 * i, 361);
        }
      }
    }));

    timeline.setCycleCount(Timeline.INDEFINITE);        
  }

  class Ball extends Circle {  
    public double vy;  
    public double vx;  
    
    public int posX = 250, posY = 175;
    public int vX = 0, vY = 0;
    public int radius = 10;

    private int ballTop, ballBottom, ballLeft, ballRight;
    private int playerTop, playerBottom; 
    private int computerTop, computerBottom; 

    private final int maxVx = 5, maxVy = 5;  
    
    public Ball() {  
      setVel();  
    }  
    
    public Ball(double radius) {  
      super(radius);  
      setVel();  

      this.radius = (int)radius;
    }  
    
    public Ball(double radius, Paint fill) {  
      super(radius, fill);  
      setVel();  
      
      this.radius = (int)radius;
    }  
    
    public Ball(double centerX, double centerY, double radius) {  
      super(centerX, centerY, radius);  
      setVel();  

      this.radius = (int)radius;
      this.posX = (int)(centerX-radius);
      this.posY = (int)(centerY-radius);
    }  
    
    public Ball(double centerX, double centerY, double radius, Paint fill) {  
      super(centerX, centerY, radius, fill);  
      setVel();  

      this.radius = (int)radius;
      this.posX = (int)(centerX-radius);
      this.posY = (int)(centerY-radius);
    }  
    
    public void moveBall ()  {
      if (vx > maxVx) 
        vx = maxVx;
      else if (vx < -maxVx) 
        vx = -maxVx;
      else if (vy > maxVy) 
        vy = maxVy;
      else if (vy < -maxVy) 
        vy = -maxVy;
      else if (vx == 0) 
        vx = 1;

      posX += vx;
      posY += vy;
    }
    
    public int getPosX() {
      return posX;
    }

    public int getPosY() {
      return posY;
    }

    public int getLocation() {
      if (vy > 0)  {
        if (posY > 330) {
          vy = -vy;
          return 0;
        }
      }
      else if (vy < 0) {
        if (posY < 20)  {
          vy = -vy;
          return 0;
        }
      }

      if (vx < 0)  {
        if (posX < 15 && posY < 75)  {
          vx = -vx;
          return 0;
        }
        else if (posX < 15 && posY > 275) {
          vx = -vx;
          return 0;
        }
        else if (posX < 15) {
          vx = -vx;
          return 1;
        }
        else 
          return 0;
      }
      else if (vx > 0) {
        if (posX > 485 && posY < 75) {
          vx = -vx;
          return 0;
        }
        else if (posX > 485 && posY > 275) {
          vx = -vx;
          return 0;
        }
        else if (posX > 485)  {
          vx = -vx;
          return 2;
        }
        else 
          return 0;
      }
      else 
        return 0;
    }    
    
    public void touchPlayerPaddle(Paddle paddle, AudioClip goClip) {
      ballTop = posY - radius;
      ballBottom = posY + radius;
      ballLeft = posX - radius;
      ballRight = posX + radius;

      playerTop = paddle.posY2;
      playerBottom = paddle.posY2 + paddle.sizeY;

      if ((ballTop >= playerTop - 10) && (ballBottom <= playerBottom + 10))  {
        if (ballLeft <= 25){
          goClip.play();

          vx = - vx;

          if (paddle.vy2 < 0)  {
            vy --;
          }
        }
      }
    }    
    
    public void touchComputerPaddle(Paddle paddle, AudioClip goClip) {
      ballTop = posY - radius;
      ballBottom = posY + radius;
      ballLeft = posX - radius;
      ballRight = posX + radius;

      computerTop = paddle.posY1;
      computerBottom = paddle.posY1 + paddle.sizeY;

      if ((ballTop >= computerTop - 10) && (ballBottom <= computerBottom + 10))  {
        if (ballRight >= 475) {
          goClip.play();

          vx = - vx;

          if (paddle.vy1 < 0)  {
            vy --;
          }
        }
      }
    }    
    
    public double getVx() {  
      return vx;  
    }  
    
    public void setVx(int vx) {  
      this.vx = vx;  
    }  
    
    public double getVy() {  
      return vy;  
    }  
    
    public void setVy(int vy) {  
      this.vy = vy;  
    }  
    
    private void setVel() {  
      vx = -1;  
      vy = -1;  
    }  
  }    

  class Paddle extends Rectangle {  
    public double vx, vy;  
    
    public int posX = 0;
    public int posY1, posY2; 
    public int vy1 = 3, vy2 = 0;        
    public int realY = 0;
    public int sizeX = 10, sizeY = 50;
    
    public Paddle() {  
      setVel();  
    }  

    public Paddle(boolean blnComputer, double width, double height) {  
      super(width, height);  

      setVel();  
    }  
    
    public Paddle(boolean blnComputer, double width, double height, Paint fill) {  
      super(width, height, fill);  

      setVel();  
    }  
    
    public Paddle(boolean blnComputer, double x, double y, double width, double height) {  
      super(x, y, width, height);  
      
      setVel();  

      if (blnComputer) { 
        this.posY1 = (int)y;
        vy1 = 3;
      }
      else {
        this.posY2 = (int)y;
        vy2 = 0;
      }
      
      this.posX = (int)x;
    }  
    
    // Move Player Paddle
    public void movePaddle(int mouseY)  {
      int newY;

      newY = mouseY - (sizeY / 2);

      if (newY < posY2) 
        vy2 = (posY2 - newY) / 2;
      else 
        vy2 = (newY - posY2) / 2;

      posY2 = newY;
    }

    // Move Computer Paddle
    public void movePaddle(Ball ball)  {
      realY = posY1 + (sizeY / 2);
      
      if (ball.vx < 0)  {
        if (realY < 175)  {
          posY1 += vy1;
        }
        else if (realY > 175) {
          posY1 -= vy1;
        }
      }
      else if (ball.vx > 0) {
        if ( realY != ball.posY)  {
          if (ball.posY < realY)  {
            posY1 -= vy1;
          }
          else if (ball.posY > realY) {
            posY1 += vy1;
          }
        }
      }
    }   

    public int getComputerY() {  
      return posY1;  
    }  
    
    public int getPlayerY() {  
      return posY2;  
    }  

    public double getVx() {  
      return vx;  
    }  
    
    public void setVx(int vx) {  
      this.vx = vx;  
    }  
    
    public double getVy() {  
      return vy;  
    }  
    
    public void setVy(int vy) {  
      this.vy = vy;  
    }  
    
    private void setVel() {  
      vx = -1;  
      vy = -1;  
    }  
  }     
  
  /**
   * The main() method is ignored in correctly deployed JavaFX application.
   * main() serves only as fallback in case the application can not be launched
   * through deployment artifacts, e.g., in IDEs with limited FX support.
   * NetBeans ignores main().
   *
   * @param args the command line arguments
   */
  public static void main(String[] args) {
    launch(args);
  }
}
