package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;

public class Drivetrain {
	public VictorSP leftFront, leftRear, rightFront, rightRear;
	public RobotDrive drive;
	public Encoder leftEnc, rightEnc;

	public Drivetrain() {
		leftFront = new VictorSP(RobotMap.KleftFront);
		leftRear = new VictorSP(RobotMap.KleftRear);
		rightFront = new VictorSP(RobotMap.KrightFront);
		rightRear = new VictorSP(RobotMap.KrightRear);
		
		leftEnc = new Encoder(0, 1, false, EncodingType.k4X);
		rightEnc = new Encoder(2, 3, false, EncodingType.k4X);
		leftEnc.setDistancePerPulse(.08726646);
        rightEnc.setDistancePerPulse(.08726646);
        leftEnc.setMinRate(1);
        rightEnc.setMinRate(1);
        leftEnc.setSamplesToAverage(5);
        rightEnc.setSamplesToAverage(5);
		
		drive = new RobotDrive(leftFront, leftRear, rightFront, rightRear);
		drive.setInvertedMotor(MotorType.kRearLeft, true);
		drive.setInvertedMotor(MotorType.kFrontLeft, true);	
	}

	
	public void log(){
		SmartDashboard.putNumber("Left Speed", leftEnc.getRate());
		SmartDashboard.putNumber("Right Speed", rightEnc.getRate());
	}
}

