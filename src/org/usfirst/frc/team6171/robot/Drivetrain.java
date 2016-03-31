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
    
    double distanceSetpoint, angleSetpoint;
    
    private boolean turnDone;
	
	public static final double Kp = .5;
    public static final double Ki = .005;
    public static final double Kd = .05;

	public Drivetrain() {
		leftFront = new VictorSP(0);
		leftRear = new VictorSP(1);
		rightFront = new VictorSP(2);
		rightRear = new VictorSP(3);
		
		leftEnc = new Encoder(0, 1, true, EncodingType.k4X);
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
		drive.setInvertedMotor(MotorType.kFrontRight, true);
		drive.setInvertedMotor(MotorType.kRearRight, true);
        drive.setInvertedMotor(MotorType.kFrontLeft, true);
        drive.setInvertedMotor(MotorType.kRearLeft, true);
        
        pidOut = new DriveOutput();
        pid = new PIDController(Kp, Ki, Kd, leftEnc, pidOut);
        pid.setOutputRange(-.6, .6);
        //pid.setInputRange(-255, 5);
        pid.setPercentTolerance(10);
        pid.setContinuous();
        turnDone = false;
	}

	public void resetEncoders()
	{
		leftEnc.reset();
    	rightEnc.reset();
	}
	
	public void setOutput(double out){
		pid.setOutputRange(out, out);
	}
	
	public void setDistanceSetpoint(double setPoint)
	{
		this.distanceSetpoint = setPoint;
		pid.setSetpoint(distanceSetpoint);
	}
	
	public void pidEnable(){
		pid.enable();
	}
	public void pidDisable(){
		pid.disable();
	}
	
	public void setAngleSetpoint(double a){
		angleSetpoint = a;
	}
	
	public boolean getTurnDone(){
		return turnDone;
	}
	public void setTurnDone(boolean b){
		turnDone = b;
	}
	
	public boolean getDistanceDone(){
		return !pid.isEnabled();
	}
	public void setOutputRange(double a, double b)
	{
		pid.setOutputRange(a, b);
	}
	
	public void turnToAngle(){
		double temp = Robot.ahrs.getYaw() - angleSetpoint;
		//System.out.println(temp);
		double output = temp * .08;
		output = Math.max(-.8,Math.min(.8,output));
		//System.out.println(output);
		drive.arcadeDrive(0,-output);
		if(Math.abs(temp)<5)
			turnDone = true;
	}
	
	public void driveDistanceForwards()
	{
		log();
		SmartDashboard.putNumber("Yaw", Robot.ahrs.getYaw());
		//while(!pid.onTarget())
		//{
			//drive.drive(.3, 0.0);
		//	setAngle(Robot.ahrs.getYaw() * .1);
		//}
		//pid.disable();
		pidOut.setAngle(-Robot.ahrs.getYaw() * .2);
	    	if(pid.onTarget())
	    		pid.disable();

	}
	public void driveDistanceBackwards()
	{
		log();
		SmartDashboard.putNumber("Yaw", Robot.ahrs.getYaw());
		//while(!pid.onTarget())
		//{
			//drive.drive(.3, 0.0);
		//	setAngle(Robot.ahrs.getYaw() * .1);
		//}
		//pid.disable();
		pidOut.setAngle(Robot.ahrs.getYaw() * .2);
	    	if(pid.onTarget())
	    		pid.disable();

	}
	
	
	
	public void log(){
		SmartDashboard.putNumber("Left Speed", leftEnc.getRate());
		SmartDashboard.putNumber("Right Speed", rightEnc.getRate());
		SmartDashboard.putNumber("Left Distance", leftEnc.getDistance());
		SmartDashboard.putNumber("Right Distance", rightEnc.getDistance());
	}
	private class DriveOutput implements PIDOutput {
		double angle;
		public DriveOutput()
		{
			angle = 0;
		}
		public void setAngle(double angle)
		{
			this.angle = Math.max(-.3,Math.min(.3,angle));
		}
		
		public void pidWrite(double output) {
			drive.arcadeDrive(-output, angle);
		}
	}
}

