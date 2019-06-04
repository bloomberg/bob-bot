/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.can.VictorSPX;
import edu.wpi.first.wpilibj.command.Subsystem;

import frc.robot.Constants;

/*
 * Originally there was supposed to be a climbing arm to reach hab2 or hab3. However
 * since the claw is not able to grab the cargo from the floor we are repurposing that 
 * climbing arm so that it is now an intake arm. (That's right, the robot will not climb; maybe 
 * next year).
 * 
 * Each intake arm (two of the, one on right side and one on left side) will have wheels
 * controlled by one motor (VictorSPX controller). So this subsystem simply needs to 
 * be able to handling proper spinning of those wheels.
 */

public class Intake extends Subsystem {

    // Generally, return a singleton instance of the subsystem
    private static Intake sInstance;

    // Always return the same instance
    public static Intake getInstance() {
        if (sInstance == null) {
            sInstance = new Intake();
        }

        return sInstance;
    }

    private static final double WHEEL_SPEED = .7f;

    private VictorSPX armWheels; // A Victor on Channel 2

    private Intake() {
        armWheels = new VictorSPX(Constants.Intake.kArmWheelId);
    }

    public void resetToDefault() {
        spinWheels(false);
    }

    /* Turn on or off the spinning of the arm wheels */
    public void spinWheels(boolean onOff) {
        System.out.println("Intake Wheels " + (onOff ? "Spinning" : "Not Spinning"));
        armWheels.set(ControlMode.PercentOutput, onOff ? WHEEL_SPEED : .0f);
    }

    /* Return true if the arm wheels are spinning, false otherwise */
    public boolean areWheelsSpinning() {
        return armWheels.getMotorOutputPercent() >= .0f;
    }

    /**
     * This is currently not used as the mode selections are managed by the default
     * variables and controllers
     */
    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // No default command for this subsystem
    }

}