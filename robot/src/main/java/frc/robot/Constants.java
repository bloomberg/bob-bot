/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

/**
 * Constants for the robot
 * 
 * Includes CAN IDs for devices, as well as tuning parameters for closed-loop
 * control
 */
public class Constants {
    // --- Drivetrain Constants ---
    public static class Drivetrain {
        // We are using CAN based controllers. Each controller has an ID associated with it
        // We use these IDs to directly communicate with a selected device over CAN bus
        // The ids for these CAN devices were assigned via the SparkMax Utility application
        public static final int kLeftLeaderId = 1;
        public static final int kLeftFollowerId = 2;
        public static final int kRightLeaderId = 3;
        public static final int kRightFollowerId = 4;
    }

    public static class Claw {
        public static final int kIntakeId = 5;
        public static final double kCargoIntakeSpeed = -.4;
        public static final double kCargoExhaustSpeed = .4;

        public static final double kHatchIntakeSpeed = .3;
        public static final double kHatchExhaustSpeed = -.3;
    }

    // --- Gamepad Constants ---
    public static class LogitechController {
        public static final int kLeftStickX = 0;
        public static final int kLeftStickY = 1;
        public static final int kLeftTrigger = 2;
        public static final int kRightStickX = 4;
        public static final int kRightStickY = 5;
        public static final int kRightTrigger = 3;

        public static final boolean kInvertMoveSpeed = true;
    }

}
