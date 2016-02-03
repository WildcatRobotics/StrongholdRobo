package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
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
		
		drive = new RobotDrive(leftFront, leftRear, rightFront, rightRear);
		drive.setInvertedMotor(MotorType.kRearLeft, true);
		drive.setInvertedMotor(MotorType.kFrontLeft, true);
		
		
	}

}

