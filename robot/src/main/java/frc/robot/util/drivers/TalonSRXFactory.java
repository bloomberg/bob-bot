package frc.robot.util.drivers;

import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.LimitSwitchNormal;
import com.ctre.phoenix.motorcontrol.LimitSwitchSource;
import com.ctre.phoenix.motorcontrol.NeutralMode;
import com.ctre.phoenix.motorcontrol.StatusFrame;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;
import com.ctre.phoenix.motorcontrol.VelocityMeasPeriod;
import com.ctre.phoenix.motorcontrol.can.TalonSRX;

/**
 * Creates CTRE TalonSRX objects and resets parameters to default settings. We
 * don't set closed loop and sensor params here. Individual robot code can set
 * it up.
 *
 */
public class TalonSRXFactory {
  /**
   * Configuration object for a TalonSRX. Pre-populated with default values but
   * can be changed.
   *
   */
  public static class Configuration {
    public boolean mLimitSwitchNormallyOpen = true;
    public double mNominalVoltagePct = 0.0;
    public double mPeakVoltagePct = 1.0;
    public boolean mEnableBrake = false;
    public boolean mEnableCurrentLimit = false;
    public boolean mEnableSoftLimit = false;
    public boolean mEnableLimitSwitch = false;
    public int mCurrentLimit = 0;
    public int mForwardSoftLimit = 0;
    public boolean mInverted = false;
    public double mNominalClosedLoopVoltagePct = 1.0;
    public int mReverseSoftLimit = 0;
    public boolean mSafetyEnabled = false;

    public int mControlFramePeriodMs = 5;
    public int mMotionControlFramePeriodMs = 100;
    public int mGeneralStatusFrameRateMs = 5;
    public int mFeedbackStatusFrameRateMs = 100;
    public int mQuadEncoderStatusFrameRateMs = 100;
    public int mAnalogTempVbatStatusFrameRateMs = 100;
    public int mPulseWidthStatusFrameRateMs = 100;

    public VelocityMeasPeriod mVelocityMeasurementPeriod = VelocityMeasPeriod.Period_100Ms;
    public int mVelocityMeasurementRollingAverageWindow = 64;

    public double mVoltageCompensationRampRate = 0;
    public double mVoltageRampRate = 0;
  }

  private static final Configuration kDefaultConfig = new Configuration();
  private static final Configuration kSlaveConfig = new Configuration();

  static {
    // Prepopulate slave values
    kSlaveConfig.mControlFramePeriodMs = 1000;
    kSlaveConfig.mMotionControlFramePeriodMs = 1000;
    kSlaveConfig.mGeneralStatusFrameRateMs = 1000;
    kSlaveConfig.mFeedbackStatusFrameRateMs = 1000;
    kSlaveConfig.mQuadEncoderStatusFrameRateMs = 1000;
    kSlaveConfig.mAnalogTempVbatStatusFrameRateMs = 1000;
    kSlaveConfig.mPulseWidthStatusFrameRateMs = 1000;
  }

  public static TalonSRX createDefaultTalonSRX(int canId) {
    return createTalonSRX(canId, kDefaultConfig);
  }

  public static TalonSRX createSlaveTalonSRX(int canId, int masterId) {
    TalonSRX talon = createTalonSRX(canId, kSlaveConfig);
    talon.set(ControlMode.Follower, masterId);
    return talon;
  }

  public static TalonSRX createTalonSRX(int canId, Configuration config) {
    TalonSRX talon = new TalonSRX(canId);
    talon.set(ControlMode.PercentOutput, 0);
    talon.setIntegralAccumulator(0, 0, 0);

    talon.clearMotionProfileHasUnderrun(0);
    talon.clearMotionProfileTrajectories();
    talon.clearStickyFaults(10);

    talon.configForwardLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
        (config.mLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed), 10);
    talon.configReverseLimitSwitchSource(LimitSwitchSource.FeedbackConnector,
        (config.mLimitSwitchNormallyOpen ? LimitSwitchNormal.NormallyOpen : LimitSwitchNormal.NormallyClosed), 10);
    talon.configPeakOutputForward(config.mPeakVoltagePct, 0);
    talon.configPeakOutputReverse(-config.mPeakVoltagePct, 0);
    talon.configNominalOutputForward(config.mNominalVoltagePct, 0);
    talon.configNominalOutputReverse(-config.mNominalVoltagePct, 0);

    talon.setNeutralMode((config.mEnableBrake ? NeutralMode.Brake : NeutralMode.Coast));
    talon.enableCurrentLimit(config.mEnableCurrentLimit);
    talon.configForwardSoftLimitEnable(config.mEnableSoftLimit, 0);
    talon.configReverseSoftLimitEnable(config.mEnableSoftLimit, 0);

    talon.overrideLimitSwitchesEnable(config.mEnableLimitSwitch);

    talon.setInverted(config.mInverted);
    talon.setSensorPhase(false);
    talon.getSensorCollection().setAnalogPosition(0, 0);

    talon.configContinuousCurrentLimit(config.mCurrentLimit, 0);

    talon.configForwardSoftLimitThreshold(config.mForwardSoftLimit, 0);
    talon.configReverseSoftLimitThreshold(config.mReverseSoftLimit, 0);

    talon.setSelectedSensorPosition(0, 0, 0);
    talon.selectProfileSlot(0, 0);
    talon.getSensorCollection().setPulseWidthPosition(0, 0);

    talon.configVelocityMeasurementPeriod(config.mVelocityMeasurementPeriod, 0);
    talon.configVelocityMeasurementWindow(config.mVelocityMeasurementRollingAverageWindow, 0);
    talon.configClosedloopRamp(config.mVoltageCompensationRampRate, 0);
    talon.configOpenloopRamp(config.mVoltageRampRate, 0);

    talon.setStatusFramePeriod(StatusFrame.Status_2_Feedback0, config.mFeedbackStatusFrameRateMs, 0);
    talon.setStatusFramePeriod(StatusFrameEnhanced.Status_3_Quadrature, config.mQuadEncoderStatusFrameRateMs, 0);
    talon.setStatusFramePeriod(StatusFrame.Status_4_AinTempVbat, config.mAnalogTempVbatStatusFrameRateMs, 0);
    talon.setStatusFramePeriod(StatusFrame.Status_1_General, config.mGeneralStatusFrameRateMs, 0);
    return talon;
  }
}