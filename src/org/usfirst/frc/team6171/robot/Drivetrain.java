package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDOutput;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.VictorSP;

public class Drivetrain {
	public VictorSP leftFront, leftRear, rightFront, rightRear;
	public RobotDrive drive;
	public Encoder leftEnc, rightEnc;
	PIDController pid;
    DriveOutput pidOut;
    
    double setPoint;
	
	public static final double Kp = .5;
    public static final double Ki = .005;
    public static final double Kd = .05;

	public Drivetrain() {
		leftFront = new VictorSP(0);
		leftRear = new VictorSP(1);
		rightFront = new VictorSP(2);
		rightRear = new VictorSP(3);
		
		leftEnc = new Encoder(0, 1, false, EncodingType.k4X);
		rightEnc = new Encoder(2, 3, false, EncodingType.k4X);
		leftEnc.setDistancePerPulse(.08726646);
        rightEnc.setDistancePerPulse(.08726646);
        leftEnc.setMinRate(1);
        rightEnc.setMinRate(1);
        leftEnc.setSamplesToAverage(5);
        rightEnc.setSamplesToAverage(5);
        leftEnc.setPIDSourceType(PIDSourceType.kDisplacement);
        rightEnc.setPIDSourceType(PIDSourceType.kDisplacement);
		drive = new RobotDrive(leftFront, leftRear, rightFront, rightRear);
		drive.setInvertedMotor(MotorType.kRearRight, true);
        drive.setInvertedMotor(MotorType.kFrontLeft, true);
        drive.setInvertedMotor(MotorType.kRearLeft, true);
        
        pidOut = new DriveOutput();
        pid = new PIDController(Kp, Ki, Kd, leftEnc, pidOut);
        pid.setOutputRange(-.4, .4);
        pid.setPercentTolerance(5);
        
        setPoint = 90;
	}

	public void resetEncoders()
	{
		leftEnc.reset();
    	rightEnc.reset();
	}
	
	public void setSetPoint(double setPoint)
	{
		this.setPoint = setPoint;
	}
	
	public void setAngle(double angle)
	{
		pidOut.setAngle(angle);
    	if(pid.onTarget())
    		pid.disable();
	}
	
	public void setAngleSetPoint(double angle)
	{
		pid.setSetpoint(angle);
	}
	public void go()
	{
		while(leftEnc.getDistance()<setPoint && rightEnc.getDistance()<setPoint)
		{
			drive.drive(.3, 0.0);
			setAngle(Robot.ahrs.getYaw() * .1);
		}
		drive.drive(0, 0);

	}
	
	
	
	public void log(){
		SmartDashboard.putNumber("Left Speed", leftEnc.getRate());
		SmartDashboard.putNumber("Right Speed", rightEnc.getRate());
	}
	private class DriveOutput implements PIDOutput {
		double angle;
		public DriveOutput()
		{
			angle = 0;
		}
		public void setAngle(double angle)
		{
			this.angle = angle;
		}
		
		public void pidWrite(double output) {
			drive.arcadeDrive(output+.3, angle);
		}
		
	}
}

