package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Winch {
	CANTalon winch;
	double setpoint;
	
	public static double WinchKp = .035;
	
	public Winch(){
		winch = new CANTalon(RobotMap.KWinch);
		winch.changeControlMode(TalonControlMode.PercentVbus);
		setpoint = 0;
		//Karthik is a pretty cool guy  but he asked me to code this thing that I dont know how to do		 		 
	 }
	 
	 public void controlWinch(double roll) {
			 //WinchKp = Math.abs(roll)<30 ? .04 : .02;
			double tempOutput = 0;
			double output = 0;
			if(Math.abs(setpoint - roll) > 1){
				/*if((setpoint - roll)*Kp>.3)
					winch.set(-.3);
				else if((setpoint - roll)*Kp>0){
					winch.set(-(setpoint - roll)*Kp);
				}
				else
					winch.set(0);*/
				tempOutput = -(setpoint - roll)*WinchKp;
			}
			output = Math.max(-.2, Math.min(.3, tempOutput));
			winch.set(output);	
	 }
	 
	 public void controlWinch(double joyInput, double angle){
		// if(!limitSwitch.get()){
			 SmartDashboard.putNumber("joyInput", joyInput);
			 if(angle>57)
				 winch.set(0);
			 else if(angle<-20)
			 	winch.set(-.1);
			 else
			 {
				 if(angle<-10)
					 winch.set(-joyInput*.25);
				 else
					 winch.set(-joyInput*.2);
			 }
		// } 
	 }
	 
	 public void changeAngle(double angle){
		 setpoint += angle;
		 if(setpoint>60)
		 {
			 setpoint = 60;
		 }
		 if(setpoint<-20)
		 {
			 setpoint = -20;
		 }
	 }
	 
	 public void setAngle(double angle) {
		 setpoint = angle;
	 }
 }
 

