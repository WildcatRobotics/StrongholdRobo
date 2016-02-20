
package org.usfirst.frc.team6171.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CounterBase.EncodingType;
import edu.wpi.first.wpilibj.DoubleSolenoid.Value;
import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;
import edu.wpi.first.wpilibj.Encoder;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.NamedSendable;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.PIDSourceType;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.SPI;
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
	
	Timer time;
	OI oi;
	Drivetrain driveTrain;
	Shooter shooter;
	Winch winch;
	AHRS ahrs;
	CameraServer server;
	
	//double setpoint;
	boolean tankDrive; // used to see if mode button is clicked
	boolean driveA;
	boolean isShooting, isStopping, isIntaking, push, pushed;
	boolean locked, lockHelp;
	//these store the data from teleop for use in autonomous replay
	//ArrayList<Double> repL, repR;
	
	//these variables control the flow of the replay
	//int replayCounter;
	//int isReplay;
	
    public void robotInit() {
    	
    	oi = new OI();
    	
    	time = new Timer();
    	
    	driveTrain = new Drivetrain();
    	
    	shooter = new Shooter();
    	
    	winch = new Winch();
    	
    	try {
	          ahrs = new AHRS(SPI.Port.kMXP); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	      }
    	
    	server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam0");
        
    	//repL = new ArrayList<Double>();
    	//repR = new ArrayList<Double>();
    	//replayCounter = 0;
    	driveA = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		locked = lockHelp = false;
    }
    
    public void autonomousInit(){
    	//time.reset();
    	//time.start();
    	//replayCounter = 0;
    }
    
    /**
     * This function is called periodically during autonomous
    */ 
    public void autonomousPeriodic(){
    	//if(isReplay == 1){
    	//	driveT.drive.arcadeDrive(repL.get(replayCounter), repR.get(replayCounter));
    	//	replayCounter++;
    	//}    	
    }
    

    public void teleopInit(){
    	//repR.clear();
    	//repL.clear();
    	winch.setAngle(0);
    	driveA = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		locked = lockHelp = false;
    }
    
    /**
     * This function is called periodically during operator control
     */
    public void teleopPeriodic() {
    	//SmartDashboard.putNumber("Joystick", oi.joy.getRawAxis(1));
    	//SmartDashboard.putData("Motor", leftFront);
    	//SmartDashboard.putData("Motor", leftRear);
    	//SmartDashboard.putData("Motor", rightFront);
    	//SmartDashboard.putData("Motor", rightRear);
    	//Double l, r;

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
			driveTrain.drive.setMaxOutput(1);
		else
			driveTrain.drive.setMaxOutput(.5);
    	
    	//Following code uses mode button to change from driving modes
		//pauses thread for half a second to give user time to release button
    	//if(oi.A.get()){
    	//	tankDrive = !tankDrive;
    	//	Timer.delay(.5);
    	//}
		
    	if(driveA && !oi.back.get())
    	{
    		driveA = false;
    		tankDrive = !tankDrive;
    	}
    	if(oi.back.get())
    		driveA = true;
    	
    	if(lockHelp && !oi.b12.get()){
    		lockHelp = false;
    		locked = !locked; 
    	}
    	if(oi.b12.get())
    		lockHelp = true;

		if(tankDrive)
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTY); // value added
    		   	
    		driveTrain.drive.tankDrive(oi.getLeftY(), -oi.getRightY()*.9);
    		//repL.add(l);
    		//repR.add(r);
    	}
    	else
    	{
    		//l = oi.joy.getRawAxis(OI.LEFTY); // value added
    		//r = oi.joy.getRawAxis(OI.RIGHTX); // value added 
    		   	
    		driveTrain.drive.arcadeDrive(-oi.getRightX(), -oi.getLeftY());
    		
    		//repL.add(l);
    		//repR.add(r);
    	}
		/*if(oi.Y.get())
			test.set(1);
		else
			test.set(0);*/
		if(oi.leftSmall.get())
    	{
    		isShooting = false;
    		isIntaking = true;
    	}
    	if(oi.rightSmall.get())
    	{
    		isShooting = true;
    		isIntaking = false;
    	}
    	if(oi.leftBig.get())
    	{
    		isShooting = false;
    		isIntaking = false;
    	}
    	
    	if(isShooting)
    	{
    		shooter.spinUp();
    	}
    	else if(isIntaking)
    	{
    		shooter.intakeSpin();
    	}
    	else
    	{
    		shooter.stop();
    	}
    	
    	if(push && !oi.trigger.get())
    	{
    		push = false;
    		pushed = !pushed;
    		if(pushed)
    		{
    			shooter.shoot();
    		}
    		else
    		{
    			shooter.retract();
    		}
    	}
    	if(oi.trigger.get())
    		push = true;
    	
    	/*
    	if(oi.joy.getPOV()==0)
    	{
    		winch.changeAngle(.5);
    	}
    	if(oi.joy.getPOV()==180)
    	{
    		winch.changeAngle(-.5);
    	}
    	*/
    	//winch.controlWinch(-ahrs.getPitch());
    	
//    	if(winchUp)
//    		winch.controlWinch(oi.joy.getRawAxis(3), ahrs.getRoll());
//    	else
    	if(oi.b9.get())
    		winch.setAngle(25);
    	if(oi.b7.get())
    		winch.setAngle(50);
    	if(oi.b8.get())
    		winch.setAngle(55);
    	if(oi.b11.get())
    		winch.setAngle(0);
    	if(oi.b10.get()){
    		winch.setAngle(-15);
    	}
    	
    	if(oi.thumb.get())
        	winch.controlWinch(-ahrs.getRoll());
       	else
       		winch.controlWinch(oi.flight.getRawAxis(1), -ahrs.getRoll());
    	
    	SmartDashboard.putNumber("Roll", ahrs.getRoll());
    	SmartDashboard.putNumber("Yaw", ahrs.getYaw());
    	SmartDashboard.putNumber("Pitch", ahrs.getPitch());
    	shooter.log();
    	driveTrain.log();
    	
		//System.out.println(oi.joy.getPOV());
		//SmartDashboard.putNumber("POV Value", oi.joy.getPOV());
  }
    
    /**
     * This function is called periodically during test mode
     */
    public void testPeriodic() {
    	//LiveWindow.addActuator("LeftFront", 0, leftFront);
    	//LiveWindow.addActuator("LeftRear", 0, leftRear);
    	//LiveWindow.addActuator("RightFront", 0, rightFront);
    	//LiveWindow.addActuator("RightRear", 0, rightRear);
    }
    
}
