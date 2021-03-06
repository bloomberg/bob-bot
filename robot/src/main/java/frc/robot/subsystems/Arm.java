/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import frc.robot.Constants;
import frc.robot.commands.ArmDefaultCommand;
import frc.robot.util.drivers.SlaveVictor;
import frc.robot.util.drivers.TalonSRXFactory;

/**
 * Add your docs here.
 */
public class Arm extends Subsystem {

  private static Arm singleton;

  public static Arm getInstance() {
    if (singleton != null) {
      return singleton;
    }
    singleton = new Arm();
    return singleton;
  }

  // Hardware values
  private TalonSRX mElevatorMaster;
  private SlaveVictor mElevatorSlave;

  // Logical values
  private double goalPosition = 0;
  private Constants.TargetHeight goalHeight = Constants.TargetHeight.COLLECT;

  public Arm() {
    mElevatorMaster = TalonSRXFactory.createDefaultTalonSRX(Constants.Arm.kMasterId);
    mElevatorMaster.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative, Constants.Arm.kPIDLoopIdx,
        Constants.Arm.kTimeout);
    mElevatorMaster.setInverted(Constants.Arm.kInvertArmMotor);
    mElevatorMaster.setNeutralMode(Constants.Arm.kMotorBrakeModeOn ? NeutralMode.Brake : NeutralMode.Coast);

    boolean sensorPresent = mElevatorMaster.getSensorCollection().getPulseWidthRiseToRiseUs() != 0;
    if (!sensorPresent) {
      DriverStation.reportError("Could not detect elevator encoder", false);
    }

    // https://github.com/CrossTheRoadElec/Phoenix-Examples-Languages/blob/master/Java/MotionMagic/src/main/java/frc/robot/Robot.java

    mElevatorMaster.setSensorPhase(Constants.Arm.kInvertSensorPhase);
    mElevatorMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_13_Base_PIDF0, 10, Constants.Arm.kTimeout);
    mElevatorMaster.setStatusFramePeriod(StatusFrameEnhanced.Status_10_MotionMagic, 10, Constants.Arm.kTimeout);

    mElevatorMaster.configNominalOutputForward(0, Constants.Arm.kTimeout);
    mElevatorMaster.configNominalOutputReverse(0, Constants.Arm.kTimeout);
    mElevatorMaster.configPeakOutputForward(1, Constants.Arm.kTimeout);
    mElevatorMaster.configPeakOutputReverse(-1, Constants.Arm.kTimeout);

    /* Set Motion Magic gains in slot0 - see documentation */
    mElevatorMaster.selectProfileSlot(Constants.Arm.kRaiseSlotIdx, Constants.Arm.kPIDLoopIdx);
    mElevatorMaster.config_kF(Constants.Arm.kRaiseSlotIdx, Constants.Arm.PID.F, Constants.Arm.kTimeout);
    mElevatorMaster.config_kP(Constants.Arm.kRaiseSlotIdx, Constants.Arm.PID.P, Constants.Arm.kTimeout);
    mElevatorMaster.config_kI(Constants.Arm.kRaiseSlotIdx, Constants.Arm.PID.I, Constants.Arm.kTimeout);
    mElevatorMaster.config_IntegralZone(Constants.Arm.kRaiseSlotIdx, Constants.Arm.PID.IZ, Constants.Arm.kTimeout);
    mElevatorMaster.config_kD(Constants.Arm.kRaiseSlotIdx, Constants.Arm.PID.D, Constants.Arm.kTimeout);

    /* Set acceleration and vcruise velocity - see documentation */
    mElevatorMaster.configMotionCruiseVelocity(Constants.Arm.kMotionVelocityDown, Constants.Arm.kTimeout);
    mElevatorMaster.configMotionAcceleration(Constants.Arm.kMotionAccelerationDown, Constants.Arm.kTimeout);

    mElevatorMaster
        .setSelectedSensorPosition(mElevatorMaster.getSensorCollection().getPulseWidthPosition() - Constants.Arm.kBasePulseWidth);

    mElevatorSlave = new SlaveVictor(Constants.Arm.kSlaveId, Constants.Arm.kInvertArmMotor);
    mElevatorSlave.setMaster(mElevatorMaster, Constants.Arm.kMotorBrakeModeOn, null);

    Constants.initTargetHeights();
    updateDashboard();
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    setDefaultCommand(new ArmDefaultCommand());
  }

  public void setOpenLoop(double speed, boolean up) {
    speed = Math.abs(speed);
    if (mElevatorMaster.getSelectedSensorPosition() > Constants.Arm.kMaxPosition && up) {
      speed = 0;
    } else if (mElevatorMaster.getSelectedSensorPosition() < Constants.Arm.kMinPosition && !up) {
      speed = 0;
    }

    if (!up) {
      speed /= 3;
    }

    mElevatorMaster.set(ControlMode.PercentOutput, (up ? 1 : -1) * speed);
  }

  public void setPresetHeight(Constants.TargetHeight preset) {
    if (preset == Constants.TargetHeight.GROUND && (Claw.getInstance().isHatch() || Claw.getInstance().isCargo())) {
      goalHeight = Constants.TargetHeight.LOW;
    } else {
      goalHeight = preset;
    }
    setMotionMagicPosition(Constants.getPresetHeight(preset));
  }

  public void setMotionMagicPosition(double position) {
    this.goalPosition = ensurePositionInRange(position);
  }

  public void setMotorsToCurrentPosition() {
    this.goalPosition = ensurePositionInRange(this.goalPosition);

    // Slow down the velocities if lower
    if (mElevatorMaster.getSelectedSensorPosition() < this.goalPosition) {
      mElevatorMaster.configMotionCruiseVelocity(Constants.Arm.kMotionVelocityUp, Constants.Arm.kTimeout);
      mElevatorMaster.configMotionAcceleration(Constants.Arm.kMotionAccelerationUp, Constants.Arm.kTimeout);
    } else {
      mElevatorMaster.configMotionCruiseVelocity(Constants.Arm.kMotionVelocityDown, Constants.Arm.kTimeout);
      mElevatorMaster.configMotionAcceleration(Constants.Arm.kMotionAccelerationDown, Constants.Arm.kTimeout);
    }

    this.mElevatorMaster.set(ControlMode.MotionMagic, this.goalPosition);
  }

  public double getGoalPosition() {
    return this.goalPosition;
  }

  public Constants.TargetHeight getTargetHeight() {
    return goalHeight;
  }

  public void incrementTargetHeight(boolean increment) {
    int size = Constants.TargetHeight.values().length;
    int nextSize = (increment ? 1 : -1) + goalHeight.ordinal();

    if (nextSize < 0) {
      nextSize = size - 1;
    } else if (nextSize >= size) {
      nextSize = 0;
    }
    goalHeight = Constants.TargetHeight.values()[nextSize];
  }

  public void updateDashboard() {
    double position = mElevatorMaster.getSelectedSensorPosition();
    SmartDashboard.putNumber("Arm Encoder", position);
    SmartDashboard.putString("Arm Goal Name", goalHeight.name());
    SmartDashboard.putNumber("Arm Goal Position", goalPosition);
    SmartDashboard.putNumber("Arm Abs Position", mElevatorMaster.getSensorCollection().getPulseWidthPosition());
    // SmartDashboard.putNumber("Arm Goal Degrees",
    // convertToDegrees(mGoalPosition));
    // SmartDashboard.putNumber("Arm Degrees", convertToDegrees(position));
    SmartDashboard.putNumber("Arm Voltage", mElevatorMaster.getMotorOutputVoltage());
    SmartDashboard.putNumber("Arm Percent", mElevatorMaster.getMotorOutputPercent());
  }

  public double ensurePositionInRange(double desiredPosition) {
    return Math.min(Math.max(Constants.Arm.kMinPosition, desiredPosition), Constants.Arm.kMaxPosition);
  }
}
