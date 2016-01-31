
package org.usfirst.frc.team6171.robot;

import java.util.ArrayList;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.Encoder;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.RobotDrive.MotorType;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.VictorSP;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

/**
 * The VM is configured to automatically run this class, and to call the
 * functions corresponding to each mode, as described in the IterativeRobot
 * documentation. If you change the name of this class or the package after
 * creating this project, you must also update the manifest file in the resource
 * directory.
 */
public class Robot extends IterativeRobot {
    /**
     * This function is run when the robot is first started up and should be
     * used for any initialization code.
     */
	
	VictorSP leftFront, leftRear, rightFront, rightRear, test;
	RobotDrive drive;
	
	Encoder leftEnc, rightEnc;
	
	//these are the PID constants
	PIDController pid;
	public static final int Kp = 0;
	public static final int Ki = 0;
	public static final int Kd = 0;
	
	Timer time;
	OI oi;
	
	boolean tankDrive; // used to see if mode button is clicked
	
	//these store the data from teleop for use in autonomous replay
	ArrayList<Double> repL, repR;
	
	//these variables control the flow of the replay
	int replayCounter;
	int isReplay;
	
    public void robotInit() {
    	leftFront = new VictorSP(RobotMap.KleftFront);
    	leftRear = new VictorSP(RobotMap.KleftRear);
    	rightFront = new VictorSP(RobotMap.KrightFront);
    	rightRear = new VictorSP(RobotMap.KrightRear);
    	test = new VictorSP(9);
    	
    	drive = new RobotDrive(rightFront, rightRear, leftFront, leftRear);
    	drive.setInvertedMotor(MotorType.kRearLeft, true);
    	drive.setInvertedMotor(MotorType.kFrontLeft, true);
    	
    	//configure both Encoders
    	leftEnc = new Encoder(0, 1, true, EncodingType.k4X);
    	leftEnc.setPIDSourceType(PIDSourceType.kDisplacement);
    	leftEnc.setDistancePerPulse(.08726646);
    	leftEnc.setMinRate(1);
    	leftEnc.setSamplesToAverage(5);
    	
    	rightEnc = new Encoder(2, 3, false, EncodingType.k4X);
    	rightEnc.setPIDSourceType(PIDSourceType.kDisplacement);
    	rightEnc.setDistancePerPulse(.08726646);
    	rightEnc.setMinRate(1);
    	rightEnc.setSamplesToAverage(5);
    	
    	oi = new OI();
    	
    	time = new Timer();
    	
    	repL = new ArrayList<Double>();
    	repR = new ArrayList<Double>();
    	replayCounter = 0;
    }
    
    public void autonomousInit(){
    	time.reset();
    	time.start();
    	replayCounter = 0;
    }
    
    /**
     * This function is called periodically during autonomous
    */ 
    public void autonomousPeriodic(){
    	if(isReplay == 1){
    		drive.arcadeDrive(repL.get(replayCounter), repR.get(replayCounter));
    		replayCounter++;
    	}    	
    }
    

    public void teleopInit(){
    	repR.clear();
    	repL.clear();
    	tankDrive = false;
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	SmartDashboard.putNumber("Joystick", oi.joy.getRawAxis(1));
    	SmartDashboard.putData("Motor", leftFront);
    	SmartDashboard.putData("Motor", leftRear);
    	SmartDashboard.putData("Motor", rightFront);
    	SmartDashboard.putData("Motor", rightRear);
    	Double l, r;

    	/*boolean boost = false;
    	boolean boosted = false;
    	if(boost && !oi.A.get())
    	{
    		boost = true;
    		boosted = !boosted;
    		if(boosted)
    		{
    			drive.setMaxOutput(.75);
    		}
    		else
    			drive.setMaxOutput(.5);
    	}
    	if(oi.A.get())
    		boost = true;*/
    	
		if(oi.LB.get() && oi.RB.get())
			drive.setMaxOutput(1);
		else
			drive.setMaxOutput(.3);
    	
    	//Following code uses mode button to change from driving modes
		//pauses thread for half a second to give user time to release button
    	if(oi.A.get()){
    		tankDrive = !tankDrive;
    		Timer.delay(.5);
    	}
    	
		if(tankDrive)
    	{
    		l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		r = oi.joy.getRawAxis(OI.RIGHTY); // value added
    		   	
    		drive.tankDrive(l, r);
    		repL.add(l);
    		repR.add(r);
    	}
    	else
    	{
    		l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		r = oi.joy.getRawAxis(OI.RIGHTX); // value added 
    		   	
    		drive.arcadeDrive(l, r);
    		repL.add(l);
    		repR.add(r);
    	}
		if(oi.Y.get())
			test.set(1);
		else
			test.set(0);
		
		System.out.println(oi.joy.getPOV());
		SmartDashboard.putNumber("POV Value", oi.joy.getPOV());
  }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	LiveWindow.addActuator("LeftFront", 0, leftFront);
    	LiveWindow.addActuator("LeftRear", 0, leftRear);
    	LiveWindow.addActuator("RightFront", 0, rightFront);
    	LiveWindow.addActuator("RightRear", 0, rightRear);
    }
    
}
