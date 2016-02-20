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
	
	//public static final int MAX_RPM = 4000;
	//public static final int DESIRED_RPM = 1000;
	final static int SHOOT_DESIRED_RPM = 4250;
    final static int INTAKE_DESIRED_RPM = 1500;
	public static final double Kp = .1;
    public static final double Ki = .001;
    public static final double Kd = .01;
	//-20
	public Shooter(){
		leftTalon = new CANTalon(RobotMap.KLeftTalon);
        rightTalon = new CANTalon(RobotMap.KRightTalon);
		
		rightTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        rightTalon.reverseSensor(false);
        rightTalon.configNominalOutputVoltage(0.0, 0.0);
        rightTalon.configPeakOutputVoltage(12.0, -12.0);
        rightTalon.setProfile(0);
        rightTalon.setF(.0485);
        rightTalon.setP(.0823);
        rightTalon.setI(.000823);
        rightTalon.setD(0.823);
        
        leftTalon.setFeedbackDevice(FeedbackDevice.CtreMagEncoder_Relative);
        leftTalon.reverseSensor(false);
        leftTalon.configNominalOutputVoltage(0.0, 0.0);
        leftTalon.configPeakOutputVoltage(12.0, -12.0);
        leftTalon.setProfile(0);
        leftTalon.setF(.0485);
        leftTalon.setP(.0823);
        leftTalon.setI(.000823);
        leftTalon.setD(0.823);
        
        comp = new Compressor(0);
        comp.setClosedLoopControl(true);
        ds = new DoubleSolenoid(0, 1);
	}
	
	public void spinUp(){
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		leftTalon.set(SHOOT_DESIRED_RPM);
		rightTalon.set(-SHOOT_DESIRED_RPM);
	}
	
	public void intakeSpin(){
		leftTalon.changeControlMode(TalonControlMode.Speed);
		rightTalon.changeControlMode(TalonControlMode.Speed);
		leftTalon.set(-INTAKE_DESIRED_RPM);
		rightTalon.set(INTAKE_DESIRED_RPM);	
	}
	
	public void stop(){
		leftTalon.changeControlMode(TalonControlMode.PercentVbus);
		rightTalon.changeControlMode(TalonControlMode.PercentVbus);
		leftTalon.set(0);
		rightTalon.set(0);
	}
	
	public void shoot(){
		ds.set(Value.kForward);
	}
	
	public void retract(){
		ds.set(Value.kReverse);
	}
	
	public void log(){
		SmartDashboard.putNumber("Left Talon", leftTalon.getSpeed());
		SmartDashboard.putNumber("Right Talon", rightTalon.getSpeed());
	}

}
