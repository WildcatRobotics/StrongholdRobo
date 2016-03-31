package org.usfirst.frc.team6171.robot;

import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.CANTalon.FeedbackDevice;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;
import edu.wpi.first.wpilibj.Compressor;
import edu.wpi.first.wpilibj.DoubleSolenoid;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Shooter {
	//public CANTalon talonLeft, talonRight;
	CANTalon leftTalon, rightTalon;
	Compressor comp;
    DoubleSolenoid ds;
    boolean out;
	
	//public static final int MAX_RPM = 4000;
	//public static final int DESIRED_RPM = 1000;
	final static int SHOOT_DESIRED_RPM = 5600;
    final static int INTAKE_DESIRED_RPM = 2500;
	public static final double Kp = .1;
    public static final double Ki = .001;
    public static final double Kd = .01;

	public Shooter(){
		leftTalon = new CANTalon(RobotMap.KLeftTalon);
        rightTalon = new CANTalon(RobotMap.KRightTalon);
		
//      Calibration for right talon motor
		rightTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        rightTalon.reverseSensor(false);
        rightTalon.configNominalOutputVoltage(0.0, 0.0);
        rightTalon.configPeakOutputVoltage(12.0, -12.0);
        rightTalon.setProfile(0);
        rightTalon.setF(.01);
        rightTalon.setP(.05);
        rightTalon.setI(.0005);
        rightTalon.setD(0.5);
        
//        Calibration for left talon motor
        leftTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        leftTalon.reverseSensor(true);
        leftTalon.configNominalOutputVoltage(0.0, 0.0);
        leftTalon.configPeakOutputVoltage(12.0, -12.0);
        leftTalon.setProfile(0);
        leftTalon.setF(.01);
        //leftTalon.setP(.0823);  
        leftTalon.setP(.05);
        leftTalon.setI(.0005);
        leftTalon.setD(0.5);
        
        comp = new Compressor(1);
        comp.setClosedLoopControl(true);
        ds = new DoubleSolenoid(0, 1);
        out = false;
	}
	
	//Spins flywheels to shoot
	public void spinUp(){
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		leftTalon.set(SHOOT_DESIRED_RPM);
		rightTalon.set(SHOOT_DESIRED_RPM);
	}
	
	//Spins flywheels to intake
	public void intakeSpin(){
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		leftTalon.set(-INTAKE_DESIRED_RPM);
		rightTalon.set(-INTAKE_DESIRED_RPM);	
	}
	
	//Stops any current flywheel movement
	public void stop(){
		leftTalon.changeControlMode(TalonControlMode.PercentVbus);
		rightTalon.changeControlMode(TalonControlMode.PercentVbus);
		leftTalon.set(0);
		rightTalon.set(0);
	}
	
	//Pushes solenoid piston out
	public void shoot(){
		ds.set(Value.kForward);
		out = true;
	}
	
	//Retracts solenoid piston
	public void retract(){
		ds.set(Value.kReverse);
		out = false;
	}
	
	public void log(){
		SmartDashboard.putNumber("Left Talon", leftTalon.getSpeed());
		SmartDashboard.putNumber("Right Talon", rightTalon.getSpeed());
		SmartDashboard.putBoolean("Piston", out);
	}
}
