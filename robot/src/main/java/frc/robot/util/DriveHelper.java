package frc.robot.util;

import frc.robot.Constants;

/**
 * Helper class to generate DriveCommand-s
 * 
 * Allows the use of either straight up tank style driving, or a variation of
 * Team 254's CheesyDrive, where the turning stick controls the curvature of the
 * robot's path rather than it's rate of heading change (like car steering).
 * This helps make the robot more controllable at high speeds.
 *
 */
public class DriveHelper {

  // Helper class to return values
  public static class SpeedControl {
    public double left = 0, right = 0;

    public SpeedControl(double left, double right) {
      this.left = left;
      this.right = right;
    }
  }

  private static final double kQuickStopDeadband = 0.2;
  private static final double kQuickStopWeight = 0.1;
  private static final double kQuickStopScalar = 5.0;

  private double mQuickStopAccumulator = 0.0;

  private double mThrottleDeadband = Constants.kDriveControllerDeadband;
  private double mWheelDeadband = Constants.kDriveControllerDeadband;

  /**
   * Generate a DriveCommand (left/right values) given stick inputs and other
   * drive properties
   * 
   * @param throttle    How much throttle to give [-1, 1]
   * @param wheel       How much to turn [-1, 1]
   * @param isQuickTurn If true, we are turning in place (override curvature
   *                    drive)
   * @param isHighGear  If true, we are running at high speed (placeholder)
   * @return DriveCommand to provide to Drive subsystem
   */
  public SpeedControl arcadeDrive(double throttle, double wheel, boolean isQuickTurn, boolean isHighGear) {
    throttle = Util.limit(throttle, 1.0);
    throttle = DriveHelper.handleDeadband(throttle, mThrottleDeadband);

    wheel = Util.limit(wheel, 1.0);
    wheel = handleDeadband(wheel, mWheelDeadband);

    double angularPower;
    boolean overPower;

    // Handle Quick Turn
    if (isQuickTurn) {
      if (Math.abs(throttle) < kQuickStopDeadband) {
        double alpha = kQuickStopWeight;
        mQuickStopAccumulator = (1 - alpha) * mQuickStopAccumulator + (alpha * wheel * kQuickStopScalar);
      }
      overPower = true;
      angularPower = wheel;
    } else {
      overPower = false;
      angularPower = Math.abs(throttle) * wheel - mQuickStopAccumulator;

      if (mQuickStopAccumulator > 1) {
        mQuickStopAccumulator -= 1;
      } else if (mQuickStopAccumulator < -1) {
        mQuickStopAccumulator += 1;
      } else {
        mQuickStopAccumulator = 0.0;
      }
    }

    double leftPwm = throttle - angularPower;
    double rightPwm = throttle + angularPower;

    return new SpeedControl(leftPwm, rightPwm);
  }

  public SpeedControl autoDrive(double distance, double angle) {

    distance = Util.limit(distance, 1.0);
    angle = Util.limit(angle, .4);

    double angularPower;

    angularPower = Math.abs(distance) * angle - mQuickStopAccumulator;

    if (mQuickStopAccumulator > 1) {
      mQuickStopAccumulator -= 1;
    } else if (mQuickStopAccumulator < -1) {
      mQuickStopAccumulator += 1;
    } else {
      mQuickStopAccumulator = 0.0;
    }

    double leftPwm = distance - angularPower;
    double rightPwm = distance + angularPower;

    return new SpeedControl(leftPwm, rightPwm);
  }

  public static double handleDeadband(double val, double deadband) {
    return (Math.abs(val) > Math.abs(deadband)) ? val : 0.0;
  }

  public void setThrottleDeadband(double deadband) {
    mThrottleDeadband = deadband;
  }

  public void setWheelDeadband(double deadband) {
    mWheelDeadband = deadband;
  }
}