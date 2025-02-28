// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot.subsystems;

import com.revrobotics.CANSparkMax;
import com.revrobotics.CANSparkBase.IdleMode;
import com.revrobotics.CANSparkLowLevel.MotorType;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj2.command.SubsystemBase;
import frc.robot.BeamBreak;
import frc.robot.Vision;

public class Pivot extends SubsystemBase {

  private double voltsMax = 0.5; 
  private double feedforwardPercentage =0.9;
  public static final double kP_rotate = 0.0008;
  public static final double kF_rotate = 0.27; //volts to hold arm rotation at a fixed position when arm is @ 180/0 deg (flat). multiple by sin(angle)
  public static final int armRotateMotorID = 22;

  // create neo for pivot
  CANSparkMax armRotationNeo = new CANSparkMax(armRotateMotorID, MotorType.kBrushless);
  //create absolute encoder (rev through-bore)
  DutyCycleEncoder encoder = new DutyCycleEncoder(9); 

  private Vision m_Vision;
  private BeamBreak m_BeamBreak;

  /** Creates a new Pivot. */
  public Pivot(Vision vision, BeamBreak beambreak) {
    m_Vision = vision;
    m_BeamBreak=beambreak;
    armRotationNeo.setIdleMode(IdleMode.kBrake);
    armRotationNeo.setInverted(true);
    encoder.setPositionOffset(0.21);
   
    
  }
  

  public double getAngle(){
    return (encoder.getAbsolutePosition() - encoder.getPositionOffset()) * 360;
  }


  public double calculateRotationVoltage(double goalAngle){ 
    return ((MathUtil.clamp((kP_rotate * (getAngle() - goalAngle) * -12),-voltsMax,voltsMax)) + feedforwardPercentage * (kF_rotate * Math.cos(Math.toRadians(getAngle()))));
  }
  public void setRotationVoltage(double volts){
    armRotationNeo.setVoltage(volts);
  }


  @Override
  public void periodic() {
  }

  //from vision:
  public double calculateGoalAngle(){
    return m_Vision.calculateGoalAngle();
  }

  // from beambreak:
  public boolean hasNote(){
    return m_BeamBreak.hasNote();
  }


}
