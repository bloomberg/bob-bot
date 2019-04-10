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
        public static final int kLeftLeaderId = 0;
        public static final int kLeftFollowerId = 1;
        public static final int kRightLeaderId = 2;
        public static final int kRightFollowerId = 3;
    }

    // --- Gamepad Constants ---
    public static class LogitechController {
        public static final int kLeftStickX = 0;
        public static final int kLeftStickY = 1;
        public static final int kLeftTrigger = 2;
        public static final int kRightStickX = 4;
        public static final int kRightStickY = 5;
        public static final int kRightTrigger = 3;

    }

}
