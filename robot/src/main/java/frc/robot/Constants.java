/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot;

import java.util.EnumMap;

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
        public static final int kClawSolenoidId = 0;

        public static final double kCargoIntakeSpeed = -.4;
        public static final double kCargoExhaustSpeed = .4;

        public static final double kHatchIntakeSpeed = .3;
        public static final double kHatchExhaustSpeed = -.3;

        public static final double kCargoHoldSpeed = -.1;
        public static final double kHatchHoldSpeed = .1;
    }

    public static class Arm {
        public static final int kMasterId = 0;
        public static final int kSlaveId = 1;

        public static final int kBasePulseWidth = 0;

        public static final boolean kInvertArmMotor = false;
        public static final boolean kInvertSensorPhase = false;
        public static final boolean kMotorBrakeModeOn = true;

        public static final int kTimeout = 30;
        public static final int kPIDLoopIdx = 0;
        public static final int kRaiseSlotIdx = 0;

        public static final int kMotionVelocityUp = 400;
        public static final int kMotionAccelerationUp = 400;
        public static final int kMotionVelocityDown = 100;
        public static final int kMotionAccelerationDown = 100;

        public static final int kMinPosition = 0;
        public static final int kMaxPosition = 1150;

        public static class PID {
            public static final double F = 0;
            public static final double P = 2;
            public static final double I = 0.0002;
            public static final double D = 10;
            public static final int IZ = 0;
        }
    }

    // --- Gamepad Constants ---
    public static class LogitechController {
        public static final boolean kInvertMoveSpeed = true;
    }

    public static enum TargetHeight {
        GROUND,
        HOLD,
        HALF,
        MAX,
    }

    private static EnumMap<TargetHeight, Integer> targetHeightMap = new EnumMap<TargetHeight, Integer>(TargetHeight.class);

    public static void initTargetHeights() {
        targetHeightMap.put(TargetHeight.GROUND, Arm.kMinPosition);
        targetHeightMap.put(TargetHeight.HOLD, 140);
        targetHeightMap.put(TargetHeight.HALF, 575);
        targetHeightMap.put(TargetHeight.MAX, Arm.kMaxPosition);
    }

    public static int getPresetHeight(TargetHeight preset) {
        return targetHeightMap.get(preset);
    }

}
