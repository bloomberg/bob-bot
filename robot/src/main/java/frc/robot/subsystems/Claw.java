/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.CANifier;
import com.ctre.phoenix.CANifier.GeneralPin;
import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkMax.IdleMode;
import com.revrobotics.CANSparkMaxLowLevel.MotorType;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.Robot;
import frc.robot.commands.ClawDefaultCommand;

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
        STOP,
        HOLD,
    }

    // Determine the current control mode of the claw. If in AUTO
    // mode, the claw intake wheels will spin/not spin depending on 
    // the presence of a game item
    public static enum ControlMode {
        AUTO,
        MANUAL
    }

    // Motor controller declarations
    private CANSparkMax mIntakeControl;

    // CANifier for the onboard sensors
    private CANifier mSensors;

    private Solenoid mClawSolenoid;

    // Start in the open position
    private TargetMode mTargetMode = TargetMode.CARGO;

    // Automatic spin mode
    private SpinMode mSpinMode = SpinMode.STOP;

    private ControlMode mControlMode = ControlMode.AUTO;

    private Claw() {
        mIntakeControl = new CANSparkMax(Constants.Claw.kIntakeId, MotorType.kBrushless);
        mIntakeControl.setIdleMode(IdleMode.kBrake);
        mSensors = new CANifier(0);
        mClawSolenoid = new Solenoid(Constants.Claw.kClawSolenoidId);

        this.ensureMode();
    }

    @Override
    public void initDefaultCommand() {
        setDefaultCommand(new ClawDefaultCommand());
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

    public void setControlMode(ControlMode mode) {
        this.mControlMode = mode;
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
            switch (mSpinMode) {
                case INTAKE:
                    speed = Constants.Claw.kCargoIntakeSpeed;
                    break;
                case EXHAUST:
                    speed = Constants.Claw.kCargoExhaustSpeed;
                    break;
                case HOLD:
                    speed = Constants.Claw.kCargoHoldSpeed;
                    break;
                default: 
                    speed = 0;
            }
            
        } else if (mTargetMode == TargetMode.HATCH) {
            switch (mSpinMode) {
                case INTAKE:
                    speed = Constants.Claw.kHatchIntakeSpeed;
                    break;
                case EXHAUST:
                    speed = Constants.Claw.kHatchExhaustSpeed;
                    break;
                case HOLD:
                    speed = Robot.m_oi.getQuickTurn() ? Constants.Claw.kHatchQuickHoldSpeed : Constants.Claw.kHatchHoldSpeed;
                    break;
                default: 
                    speed = 0;
            }
        } else {
            System.out.println("Invalid mode");
        }

        if (mSpinMode == SpinMode.STOP) {
            speed = 0;
        }
        
        this.setRawIntakeSpeed(speed);

        // Handle the solenoid
        if (mTargetMode == TargetMode.HATCH && !mClawSolenoid.get()) {
            mClawSolenoid.set(true);
        }
        else if (mTargetMode == TargetMode.CARGO && mClawSolenoid.get()) {
            mClawSolenoid.set(false);
        }
    }

    /**
     * Stop the collection wheels from spinning
     */
    public void stop() {
        this.setSpinMode(SpinMode.STOP);
        this.ensureMode();
    }

    public TargetMode getTargetMode() {
        return this.mTargetMode;
    }

    public SpinMode getSpinMode() {
        return this.mSpinMode;
    }

    public ControlMode getControlMode() {
        return this.mControlMode;
    }

    public boolean hatchLeftPresent() {
        return mSensors.getGeneralInput(GeneralPin.LIMF);
    }

    public boolean hatchRightPresent() {
        return mSensors.getGeneralInput(GeneralPin.LIMR);
    }

    public boolean isHatch() {
        return hatchLeftPresent() && hatchRightPresent();
    }

    public boolean cargoLeftPresent() {
        // Flipped because the distance sensor is HIGH normally
        return !mSensors.getGeneralInput(GeneralPin.QUAD_A);
    }

    public boolean cargoRightPresent() {
        return !mSensors.getGeneralInput(GeneralPin.QUAD_B);
    }

    public void updateDashboard() {
        SmartDashboard.putString("Claw Control Mode", getControlMode().toString());
        SmartDashboard.putString("Claw Target Mode", getTargetMode().toString());
        SmartDashboard.putString("Claw Spin Mode", getSpinMode().toString());

        SmartDashboard.putBoolean("Claw Left Hatch Sensor", hatchLeftPresent());
        SmartDashboard.putBoolean("Claw Right Hatch Sensor", hatchRightPresent());
        SmartDashboard.putBoolean("Claw Left Cargo Sensor", cargoLeftPresent());
        SmartDashboard.putBoolean("Claw Right Cargo Sensor", cargoRightPresent());
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
                                                         
                                                         