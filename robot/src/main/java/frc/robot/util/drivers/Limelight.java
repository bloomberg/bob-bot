/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.util.drivers;

/***********************************************
 * NOTES TO THE IMPLEMENTOR
 * 
 * - Remember to document the pipeline types
 *      - These pipelines should match what is
 *        actually on the camera!
 ***********************************************/

/**
 * PIPELINE DOCUMENTATION
 * 
 * <id>: <Description>
 * 
 * 0: Close up (within 4 feet) detection of single hatch
 *  
 */

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import edu.wpi.first.networktables.NetworkTable;
import edu.wpi.first.networktables.NetworkTableEntry;
import edu.wpi.first.networktables.NetworkTableInstance;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * Class representing a configured Limelight camera
 */
public class Limelight {

    private static Limelight sInstance;

    private static Map<String, PipelineType> sPipelineTypes = new HashMap<>();

    static {
        sPipelineTypes.put(Pipeline.DRIVE_TO_TARGET.name(), 
                           new PipelineType(Pipeline.DRIVE_TO_TARGET.name(), false));
    }

    public static Limelight getInstance() {
        if (sInstance == null) {
            sInstance = new Limelight();
        }
        return sInstance;
    }

    private Limelight() {}

    public static enum Pipeline {
        // See pipeline documentation above
        DRIVE_TO_TARGET(0);

        private final int value;

        private Pipeline(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }

        public static Optional<Pipeline> valueOf(int value) {
            return Arrays.stream(values())
                .filter(pipeline -> pipeline.value == value)
                .findFirst();
        }
    }

    /**
     * Enum representing the state of the Limelight LEDs
     * PIPELINE - Use the light settings specified by the currently selected pipeline
     * OFF, BLINK, ON - Should be fairly self-explanatory
     */
    public static enum LedMode {
        UNK(-1),
        PIPELINE(0),
        OFF(1),
        BLINK(2),
        ON(3);

        private final int value;

        private LedMode(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }

        public static Optional<LedMode> valueOf(int value) {
            return Arrays.stream(values())
                .filter(ledMode -> ledMode.value == value)
                .findFirst();
        }
    }

    /**
     * Enum representing the streaming mode of the Limelight
     * STANDARD - Side-by-side streams if the webcam is attached to the limelight
     * PIP_MAIN - Secondary camera stream is placed in lower right corner of the primary cam stream
     * PIP_SECONDARY - Primary camera stream is placed in the lower right corner of the secondary camera stream
     */
    public static enum StreamingMode {
        UNK(-1),
        STANDARD(0),
        PIP_MAIN(1),
        PIP_SECONDARY(2);

        private final int value;

        private StreamingMode(int val) {
            value = val;
        }

        public int getValue() {
            return value;
        }

        public static Optional<StreamingMode> valueOf(int value) {
            return Arrays.stream(values())
                .filter(streamingMode -> streamingMode.value == value)
                .findFirst();
        }
    }

    public static class PipelineType {
        public String name;
        public boolean is3d;

        public PipelineType(String name, boolean is3d) {
            this.name = name;
            this.is3d = is3d;
        }
    }

    /**
     * Helper class that encapsulates a snapshot of values from Limelight
     */
    public class Values {
        /**
         * Whether or not a target is visible
         */
        public boolean targetInSight;
        public double horizontalOffset, verticalOffset, targetAreaInCamera;

        public boolean is3d;
        public double transX;
        public double transY;
        public double transZ;
        public double pitch;
        public double yaw;
        public double roll;

        /**
         * Pipeline identifier
         */
        public int pipelineNum;

        /**
         * Timestamp of this set of values
         */
        public long timestampMs = System.currentTimeMillis();

        /**
         * Loads the values from the Network table
         */
        public Values() {
            NetworkTable table = Limelight.getNetworkTableInstance();
            NetworkTableEntry tv = table.getEntry("tv"), 
                              tx = table.getEntry("tx"),
                              ty = table.getEntry("ty"),
                              ta = table.getEntry("ta"),
                              pipeline = table.getEntry("pipeline"),
                              camtran = table.getEntry("camtran");
            this.targetInSight = tv.getDouble(0.0) > 0;
            this.horizontalOffset = tx.getDouble(0.0);
            this.verticalOffset = ty.getDouble(0);
            this.targetAreaInCamera = ta.getDouble(0);
            this.pipelineNum = pipeline.getNumber(-1).intValue();

            Optional<Pipeline> pipelineVal = Pipeline.valueOf(this.pipelineNum);
            if (pipelineVal.isPresent()) {
                PipelineType pType = sPipelineTypes.get(pipelineVal.get().name());
                if (pType != null && pType.is3d) {
                    double[] camtranArray = camtran.getDoubleArray(new double[0]);
            
                    this.is3d = true;
                    this.transX = camtranArray[0];
                    this.transY = camtranArray[1];
                    this.transZ = camtranArray[2];
                    this.pitch = camtranArray[3];
                    this.yaw = camtranArray[4];
                    this.roll = camtranArray[5];
                }
            }
            
        }

        /**
         * Give exact values for the Limelight values
         */
        public Values(boolean targetInSight, double horizontalOffset, double verticalOffset, double targetAreaInCamera) {
            this.targetInSight = targetInSight;
            this.horizontalOffset = horizontalOffset;
            this.verticalOffset = verticalOffset;
            this.targetAreaInCamera = targetAreaInCamera;
        }

        public String toString() {
            return String.format("<Limelight tv: %b tx: %.4f ty: %.4f ta: %.4f>", this.targetInSight, this.horizontalOffset, this.verticalOffset, this.targetAreaInCamera);
        }
    }

    // ==== STATIC VALUES ====
    private static NetworkTable sLimelightTable;

    /**
     * Used internally to load the network tables class
     * @return NetworkTable instance connected to Limelight
     */
    private static NetworkTable getNetworkTableInstance() {
        if (sLimelightTable == null) {
            sLimelightTable = NetworkTableInstance.getDefault().getTable("limelight");
        }
        return sLimelightTable;
    }

    /**
     * Return a raw Limelight.Values object decoded
     * from the network tables
     */
    public Values getRawValues() {
        return new Values();
    }

    /**
     * Set the LED mode on the Limelight
     * @param mode Requested LED Mode
     */
    public void setLeds(LedMode mode) {
        getNetworkTableInstance().getEntry("ledMode").setNumber(mode.getValue());
    }

    /**
     * Gets the current LED mode
     * @return Current LED mode
     */
    public LedMode getLeds() {
        return LedMode.valueOf(getNetworkTableInstance().getEntry("ledMode").getNumber(-1).intValue()).get();
    }

    /**
     * Set the streaming mode on the Limelight
     * @param mode Requested streaming mode
     */
    public void setStreamingMode(StreamingMode mode) {
        getNetworkTableInstance().getEntry("stream").setNumber(mode.getValue());
    }

    /**
     * Get the current streaming mode
     * @return Current streaming mode
     */
    public StreamingMode getStreamingMode() {
        return StreamingMode.valueOf(getNetworkTableInstance().getEntry("stream").getNumber(-1).intValue()).get();
    }

    /**
     * Set the currently active vision pipeline ID
     * NOTE: Ensure that the pipeline(s) on the camera module match what you think they are
     * @param pipelineNum
     */
    public void setActivePipeline(Pipeline pipeline) {
        getNetworkTableInstance().getEntry("pipeline").setNumber(pipeline.getValue());
    }

    /**
     * Get the currently active vision pipeline ID
     * @return ID of the currently active pipeline
     */
    public Pipeline getActivePipeline() {
        return Pipeline.valueOf(getNetworkTableInstance().getEntry("pipeline").getNumber(-1).intValue()).get();
    }

    /**
     * Output the values to the SmartDashboard / Shuffleboard
     */
    public void outputToDashboard() {
        Values values = this.getRawValues();
        SmartDashboard.putBoolean("Limelight Has Target", values.targetInSight);
        SmartDashboard.putNumber("Limelight X Offets", values.horizontalOffset);
        SmartDashboard.putNumber("Limelight Y Offets", values.verticalOffset);
        SmartDashboard.putNumber("Limelight Area", values.targetAreaInCamera);

        SmartDashboard.putBoolean("Limelight is3d", values.is3d);
        SmartDashboard.putNumber("Limelight 3D transX", values.transX);
        SmartDashboard.putNumber("Limelight 3D transY", values.transY);
        SmartDashboard.putNumber("Limelight 3D transZ", values.transZ);
        SmartDashboard.putNumber("Limelight 3D pitch", values.pitch);
        SmartDashboard.putNumber("Limelight 3D yaw", values.yaw);
        SmartDashboard.putNumber("Limelight 3D roll", values.roll);
    }
}