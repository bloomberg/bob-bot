/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.subsystems;

import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.command.Subsystem;
import frc.robot.Constants;
import frc.robot.util.drivers.SlaveVictor;
import frc.robot.util.drivers.TalonSRXFactory;

/**
 * Add your docs here.
 */
public class Arm extends Subsystem {
  private TalonSRX mElevatorMaster;
  private SlaveVictor mElevatorSlave;

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
    mElevatorMaster.configMotionCruiseVelocity(mProfile.getElevatorMotionVelocity(), Constants.Arm.kTimeout);
    mElevatorMaster.configMotionAcceleration(mProfile.getElevatorMotionAcceleration(), Constants.Arm.kTimeout);

    mElevatorSlave = new SlaveVictor(Constants.Arm.kSlaveId, Constants.Arm.kInvertArmMotor);
    mElevatorSlave.setMaster(mElevatorMaster, Constants.Arm.kMotorBrakeModeOn, null);
  }

  @Override
  public void initDefaultCommand() {
    // Set the default command for a subsystem here.
    // setDefaultCommand(new MySpecialCommand());
  }
}
