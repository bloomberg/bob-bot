/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Constants;

/**
 * The Claw represents the subsystem responsible for collecting, holding, and 
 * exhausting game pieces. In regards to the Hatch panel, the Claw will be closed
 * and for the Cargo, the Claw will be open. 
 * 
 * The subsystem contains a single Spark Max for the intake/exhaust, and a single
 * pneumatic piston controlled by a solenoid
 */
public class Claw extends Subsystem {

  // Generally, return a singleton instance of the subsystem
  private static Claw sInstance;

  // Always return the same instance
  public static Claw getInstance() {
    if (sInstance == null) {
      sInstance = new Claw();
    }

    return sInstance;
  }

  // TargetMode tracks what the current target type is, to 
  // determine if the claw should be open or closed
  public static enum TargetMode {
    HATCH,
    CARGO
  }

  // SpinMode tracks what the current action of the collection
  // wheels should be, based on the mTargetMode variable
  public static enum SpinMode {
    INTAKE,
    EXHAUST,
    STOP
  }

  // Motor controller declarations
  private CANSparkMax mIntakeControl;

  // Start in the open position
  private TargetMode mTargetMode = TargetMode.CARGO;

  // Start with the collection wheels not moving
  private SpinMode mSpinMode = SpinMode.STOP;

  private Claw() {
    mIntakeControl = new CANSparkMax(Constants.Claw.kIntakeId, MotorType.kBrushless);
    this.ensureMode();
  }

  /**
   * This is currently not used as the mode selections are managed
   * by the default variables and controllers
   */
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }

  /**
   * Reset the built in encoders on the SPARK Max-s
   * 
   * Resetting the encoders is useful when you're about to perform an autonomous
   * driving routine, as errors could have built up between now and the last time
   * you reset them.
   */
  public void resetEncoders() {
    // To reset the encoders, we just set their current positions to 0
    mIntakeControl.getEncoder().setPosition(0.0);
  }

  /**
   * Set the speed of the intake motors, between [-1.0, 1.0]
   * Negative speed results in inverse direction
   *
   * @param speed the speed to set the motors at
   */
  public void setRawIntakeSpeed(double speed) {
    mIntakeControl.set(speed);
  }

  /**
   * Set the target tracking mode, which will 
   * adjust the position of the claw
   * @param mode
   */
  public void setTargetMode(TargetMode mode) {
    this.mTargetMode = mode;
  }

  /**
   * Set the spin mode, which will adjust the speed of 
   * the collection wheels based on the targetMode
   * @param mode
   */
  public void setSpinMode(SpinMode mode) {
    this.mSpinMode = mode;
  }

  /**
   * ensureMode sets the claw and the collection wheels to the 
   * proper orientation based on the target and spin modes.
   * 
   * It should be called once every time an update is made to 
   * one of the modes.
   */
  public void ensureMode() {
    double speed = 0;
    if (mTargetMode == TargetMode.CARGO) {
      speed = mSpinMode == SpinMode.INTAKE ? Constants.Claw.kCargoIntakeSpeed : Constants.Claw.kCargoExhaustSpeed;
    } else if (mTargetMode == TargetMode.HATCH) {
      speed = mSpinMode == SpinMode.INTAKE ? Constants.Claw.kHatchIntakeSpeed : Constants.Claw.kHatchExhaustSpeed;
    } else {
      System.out.println("Invalid mode");
    }
    if (mSpinMode == SpinMode.STOP) {
      speed = 0;
    }
    System.out.println(speed);
    this.setRawIntakeSpeed(speed);
  }

  /**
   * Stop the collection wheels from spinning
   */
  public void stop() {
    this.setSpinMode(SpinMode.STOP);
    this.ensureMode();
  }
}

//                               hMMMMM-                                               
//                               yMMMMM+`                                              
//                             -hNNMMMMMNh-                                            
//                             oNNNNNNNNNNs                                            
//                             +NNNNNNNNNNy                                            
//                             :NNNNNNNNNNh                                            
//                             -NNNNNNNNNNm`                                           
//                            .omNNNNNNNNmm-                                           
//                          `+dmmmmmmmmmmmm/                                           
//                          ommmmmmmmmmmmmm+                                           
//                         /dmmmmmmmmmmmmmmh`                                          
//                        .ddmdddmmmmmmmmmdd-                                          
//                        odddoydddddddddhddy`                                         
//                `-:/+ooohdddy-.:+ddddddydddhyso/.                                    
//             .:oyhdmmmmmdddddhs//ddddddddddddmddh+`                                  
//          `:shdmNmhy+:-/yhhdNNmmdddddddddddmmsmmdhs-                                 
//         -yhmNmo-.`     ````.:+hMhhhddhhhhdN- `omdhyo.                               
//        /ymNm/.               -mdhhhhhdddhhN+   -mmhhy.                              
//       /ymMs`                 .yyhhhhhy-yyhNd    .yNhys`                             
//      -ydN/`                   ```````` -yhmN`    `hdhy+`                            
//     `sdM+                               sydM`     -Nhhy-                            
//     :yMm`                               syhN`     `mdyy/                            
//     +hM+                                ssdN`      ydyys`                           
//     +hN`                                osdm       /Nyyy.                           
//     +ym`                                ssNh       /Nyyy.                           
//     oym-                               .syN/       :Nyss`                           
//     osms                               :osm`       :Nys+                            
//     :shN.                              /oyd        +Nys:                            
//     .ssNo                              +oNy        ydss.                            
//      /sym`                             +yMy       .Nyso                             
//      `/sd.                             .sh:       smss-                             
//       `.`                                        .Nhs+`                             
//                                                  ydso:                              
//                                 `.`              oo+/`                              
//                                 .++              ```                                
//                                 `+.                                                 
//                                  +`                                                 
//                                 `o                                                  
//             `            ``````.:o.````                                             
//             -:`     `..--::/++::+/::///:-.``         `                              
//             :o/`  .:/-`-yo`/++..mm. :++./o:-:.`    `//                              
//             -s++-:+++-`:hy/++++/++:/+++:dm: -+/-``.+o/                              
//             `so+++++++++++++++++++++++++++///+++//+os.                              
//              :so+++++++++++++++++++++++++++++++++osy/                               
//              `/o+o++++++++++++++++++++++++++ooossyy+`                               
//                ``+ssssssssooooooooosssssssssyyyyo+/`       ```                      
//                  .hdhhhhhhhhhhhhhhhhhyyyyyhhhhhy`  `.`  `.://-                      
//   ````    `...--:sdhhyyhhhhhhhhhhhhhyyyyyhhyyhhdo:-syo-./++++.```                   
//   /++/:-.:ohhhhhhhhhddddddddddddddddddddddddddhhhyyhhys++++++++o-                   
//   .:+++++shhhsssssssyyyyhhhhhhhhhhhhhhhhhyyyyssssssshhys++oooo+-`                   
// `-:/+++++shhhssssssssssssssssssssssssssssssssssssssshhyyssss-`                      
// `/+oooooosyyo::--+ssssssssssssssssssssssssssssso-/+oo//++/.`                        
//    ````....`     +ssssssssssssssssssssssssssssss`                                   
//                  /ssssssssssssssssssssssssssssss`                                   
//                  .ssssssssssssssssssssssssssssso                                    
//                  `sssssssssssssssssssssssssssss-                                    
//                   -sssssssssssssssssssssssssss/                                     
//                   `+sssssssssssssssssssssssssh-`                                    
//                 `-oyssssssssssssssssssssssssssys:`                                  
//               `:shyyyyyyyyyyyyyyyyyyyyyyyyyyyyyhhh+`                                
//               :yhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhhh/                                
//                 ``......``````    ```....--::::--.`   
//    _______ _    _ ______    _____ _          __          __
//   |__   __| |  | |  ____|  / ____| |        /\ \        / /
//      | |  | |__| | |__    | |    | |       /  \ \  /\  / / 
//      | |  |  __  |  __|   | |    | |      / /\ \ \/  \/ /  
//      | |  | |  | | |____  | |____| |____ / ____ \  /\  /   
//      |_|  |_|  |_|______|  \_____|______/_/    \_\/  \/    
                                                         
                                                         