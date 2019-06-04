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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.commands.DefaultIntakeCommand;

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

    private VictorSPX mIntakeMotor; // A Victor on Channel 2

    private Intake() {
        mIntakeMotor = new VictorSPX(Constants.Intake.kIntakeMotorId);
    }

    public void resetToDefault() {
        spinWheels(false);
    }

    /* Turn on or off the spinning of the arm wheels */
    public void spinWheels(boolean intake) {
        mIntakeMotor.set(ControlMode.PercentOutput, (intake ? 1 : -1) * Constants.Intake.kSpinSpeed);
    }

    /* Return true if the arm wheels are spinning, false otherwise */
    public boolean areWheelsSpinning() {
        return Math.abs((int) (100 * mIntakeMotor.getMotorOutputPercent()))  >= .0f;
    }

    public void stop() {
        mIntakeMotor.set(ControlMode.PercentOutput, 0);
    }

    /**
     * This is currently not used as the mode selections are managed by the default
     * variables and controllers
     */
    @Override
    public void initDefaultCommand() {
        // Set the default command for a subsystem here.
        // No default command for this subsystem
        setDefaultCommand(new DefaultIntakeCommand());
    }

    public void updateDashboard() {
        SmartDashboard.putBoolean("Intake Spinning", areWheelsSpinning());
    }

}