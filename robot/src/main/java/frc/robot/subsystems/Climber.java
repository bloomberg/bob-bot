/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.Solenoid;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Constants;

public class Climber extends Subsystem {

  // Generally, return a singleton instance of the subsystem
  private static Climber sInstance;

  // Always return the same instance
  public static Climber getInstance() {
    if (sInstance == null) {
      sInstance = new Climber();
    }

    return sInstance;
  }

  private static final double LEG_SPEED = .5f;
  private static final double WHEEL_SPEED = .7f;
  private static final boolean ARM_EXTENDED = true;
  private static final boolean ARM_RETRACTED = !ARM_EXTENDED;

  private VictorSPX legMuscles;
  private VictorSPX armWheels;
  private Solenoid armMuscles;

  private Climber() {
    legMuscles = new VictorSPX(Constants.Climber.kLegMuscleId);
    armWheels = new VictorSPX(Constants.Climber.kArmWheelId);
    armMuscles = new Solenoid(Constants.Climber.kArmMuscleId);

    // TODO: various init things
  }

  public void resetToDefault(){
    spinWheels(false);
    retractArms();
    retractLegs();
  }

  /* Turn on or off the spinning of the arm wheels */
  public void spinWheels(boolean onOff){
    System.out.println("Climber Wheels " + (onOff ? "Spinning" : "Not Spinning"));
    armWheels.set(ControlMode.PercentOutput, onOff ? WHEEL_SPEED : .0f);
  }

  public boolean areWheelsSpinning(){
    return armWheels.getMotorOutputPercent() >= .0f;
  }

  public void extendLegs(){
    System.out.println("Climber Legs Extending");
    legMuscles.set(ControlMode.PercentOutput, LEG_SPEED);
  }

  public void retractLegs(){
    System.out.println("Climber Legs Retracting");
    // TODO: this may be wrong
    legMuscles.set(ControlMode.PercentOutput, 0f);
  }

  public double legHeight(){
    //TODO: read motor control or encoder to get leg height
    return 0f;
  }

  public void extendArms(){
    System.out.println("Climber Arms Extending");
    armMuscles.set(ARM_EXTENDED);
  }

  public void retractArms(){
    System.out.println("Climber Arms Retracting");
    armMuscles.set(ARM_RETRACTED);
  }

  public boolean areArmsExtended(){
    return ARM_EXTENDED == armMuscles.get();
  }


  /**
   * This is currently not used as the mode selections are managed
   * by the default variables and controllers
   */
  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // No default command for this subsystem
  }

}