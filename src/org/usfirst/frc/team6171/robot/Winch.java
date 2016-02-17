package org.usfirst.frc.team6171.robot;
import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.CANTalon.TalonControlMode;

public class Winch {
 //public VictorSP winch;
 //public  Encoder encOne;
 //public static final int MAX_RPM = 3000;
 //public static final int DESIRED_RPM = 1000;
	CANTalon winch;
	double setpoint;
	
	public static double WinchKp = .02;
	
	public Winch(){
		winch = new CANTalon(3);
		winch.changeControlMode(TalonControlMode.PercentVbus);
		setpoint = -15;
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
		//else
		//	output = 0;
		output = Math.max(-.3, Math.min(.1, tempOutput));
		winch.set(output);
	 }
	 
	 public void changeAngle(double angle){
		 setpoint += angle;
	 }
	 
	 public void setAngle(double angle) {
		 setpoint = angle;
	 }
	 
 }
 

