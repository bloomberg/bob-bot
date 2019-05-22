/*----------------------------------------------------------------------------*/
/* Copyright (c) 2018 FIRST. All Rights Reserved.                             */
/* Open Source Software - may be modified and shared by FRC teams. The code   */
/* must be accompanied by the FIRST BSD license file in the root directory of */
/* the project.                                                               */
/*----------------------------------------------------------------------------*/

package frc.robot.commands;

public class ClimbToHab2 extends ClimbToHab {

  private static final double HAB_LEG_HEIGHT = 10.0f;
    //TODO: set appropriate height value

  public ClimbToHab2(){
    super(HAB_LEG_HEIGHT);
  }
}