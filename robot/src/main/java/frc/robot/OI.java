/*----------------------------------------------------------------------------*/
/* Copyright (c) 2017-2018 FIRST. All Rights Reserved.                        */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import frc.robot.controllers.XboxController;
import frc.robot.subsystems.Claw;
import frc.robot.commands.SetClawTargetMode;
import frc.robot.commands.SetClawSpinMode;


/**
 * This class is the glue that binds the controls on the physical operator
 * interface to the commands and command groups that allow control of the robot.
 */
public class OI {
    // Assuming we use an XBOX controller (the logitechs should map similarly)
    // The drive controller should be the very first one listed in the DriverStation
    private XboxController mDriveController = new XboxController(0);
    private XboxController mOperatorControoler = new XboxController(1);

    /**
     * Get requested X-axis movement speed from the controller
     * Based on a Constants value, we may negate this to ensure
     * it gives a positive value when we want to go forward
     * @return requested speed
     */
    public double getDriveSpeed() {
        // left stick, Y axis
        return (Constants.LogitechController.kInvertMoveSpeed ? -1 : 1) * mDriveController.getLeftStickY(); 
    }

    /**
     * Get requested Z-axis rotation speed from the controller
     * @return requested rotation speed
     */ 
    public double getTurnSpeed() {
        // right stick, x axis
        return mDriveController.getRightStickX();
    }

    /**
     * Get quick turn state from the controller
     * @return true if we are in quick turn more
     */
    public boolean getQuickTurn() {
        // Left Shoulder button
        return mDriveController.leftBumper.get();
    }

    public double getManualArmSpeed() {

        double raw = (Constants.LogitechController.kInvertMoveSpeed ? -1 : 1) * mOperatorControoler.getLeftStickY();
        if (Math.abs(raw) < .2) {
            return 0;
        }
        return raw;
    }

    public OI() {
        this.mOperatorControoler.buttonX.whenPressed(new SetClawTargetMode(Claw.TargetMode.CARGO));
        this.mOperatorControoler.buttonB.whenPressed(new SetClawTargetMode(Claw.TargetMode.HATCH));
        this.mOperatorControoler.buttonA.whileHeld(new SetClawSpinMode(Claw.SpinMode.INTAKE));
        this.mOperatorControoler.buttonY.whileHeld(new SetClawSpinMode(Claw.SpinMode.EXHAUST));
    }
}
