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
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
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
     * Get requested X-axis movement speed from the controller Based on a Constants
     * value, we may negate this to ensure it gives a positive value when we want to
     * go forward
     * 
     * @return requested speed
     */
    public double getDriveSpeed() {
        // left stick, Y axis
        return (Constants.LogitechController.kInvertMoveSpeed ? -1 : 1) * mDriveController.getLeftStickY();
    }

    /**
     * Get requested Z-axis rotation speed from the controller
     * 
     * @return requested rotation speed
     */
    public double getTurnSpeed() {
        // right stick, x axis
        return mDriveController.getRightStickX();
    }

    /**
     * Get quick turn state from the controller
     * 
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

    public boolean useSmartDashboardMM() {
        return SmartDashboard.getBoolean("Use SD Motion Magic", false);
    }

    public double getSDDesiredMotionMagicPosition(double defaultValue) {
        return SmartDashboard.getNumber("Desired Motion Magic Position", defaultValue);
    }

    public OI() {
        this.mOperatorControoler.buttonX.whenPressed(new SetClawTargetMode(Claw.TargetMode.CARGO));
        this.mOperatorControoler.buttonB.whenPressed(new SetClawTargetMode(Claw.TargetMode.HATCH));
        this.mOperatorControoler.buttonA.whileHeld(new SetClawSpinMode(Claw.SpinMode.INTAKE));
        this.mOperatorControoler.buttonY.whileHeld(new SetClawSpinMode(Claw.SpinMode.EXHAUST));

        createSmartDashboardBoolean("Use SD Motion Magic", false);
        createSmartDashboardNumber("Desired Motion Magic Position", 0);
    }

    /**
     * Initialize value on SmartDashboard for user input, but leave old value if
     * already present.
     *
     * @param key      The SmartDashboard key to associate with the value.
     * @param defValue The default value to assign if not already on dashboard.
     *
     * @return The current value that appears on the dashboard.
     */
    public static double createSmartDashboardNumber(String key, double defValue) {

        // See if already on dashboard, and if so, fetch current value
        double value = SmartDashboard.getNumber(key, defValue);

        // Make sure value is on dashboard, puts back current value if already set
        // otherwise puts back default value
        SmartDashboard.putNumber(key, value);

        return value;
    }

    public static boolean createSmartDashboardBoolean(String key, boolean defValue) {

        // See if already on dashboard, and if so, fetch current value
        boolean value = SmartDashboard.getBoolean(key, defValue);

        // Make sure value is on dashboard, puts back current value if already set
        // otherwise puts back default value
        SmartDashboard.putBoolean(key, value);

        return value;
    }
}
