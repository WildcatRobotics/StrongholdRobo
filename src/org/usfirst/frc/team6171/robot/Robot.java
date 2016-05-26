
package org.usfirst.frc.team6171.robot;

import com.kauailabs.navx.frc.AHRS;

import edu.wpi.first.wpilibj.CameraServer;
import edu.wpi.first.wpilibj.DriverStation;

//import javax.swing.JOptionPane;

import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.SPI;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.networktables.NetworkTable;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
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
	Intake intake;
	SendableChooser positionChooser;
	SendableChooser obstacleChooser;
	public static AHRS ahrs;
	public static AHRS driveGyro;
	CameraServer server;
	NetworkTable network;
	public static MyPIDController xPID;
	
	final String lowBar = "Low Bar";
	final String two = "Two";
	final String three = "Three";
	final String four = "Four";
	final String five = "Five";
	
	final String moat = "Moat";
	final String rockWall = "Rock Wall";
	final String roughTerrain = "Rough Terrain";
	final String ramparts = "Ramparts";
	
	boolean tankDrive; // used to see if mode button is clicked
	boolean driveModeHelper;
	boolean isShooting, isStopping, isIntaking, push, pushed, aPush, aPushed, xPush, xPushed, hatUp, hatDown;
	
	double sensitivity, yVal, calculatedAngle, xVal, calculatedAngle2;
	
	int step;
	int autoDistance;
	
	final int BIG_OBSTACLE_DISTANCE = 175;
	final int ROUGH_TERRAIN_DISTANCE = 125;
	
	String positionSelected;
	boolean cameraData;
	
    public void robotInit() {
    	
    	time = new Timer();
    	driveTrain = new Drivetrain();
    	shooter = new Shooter();
    	winch = new Winch();
    	intake = new Intake();
    	oi = new OI();
    	
    	positionChooser = new SendableChooser();
    	positionChooser.addDefault("Low Bar", lowBar);
    	positionChooser.addObject("Second Position", two);
    	positionChooser.addObject("Third Position", three);
    	positionChooser.addObject("Fourth Position", four);
    	positionChooser.addObject("Fifth Position", five);
    	SmartDashboard.putData("Autonomous Chooser", positionChooser);
    	
    	obstacleChooser = new SendableChooser();
    	obstacleChooser.addDefault("Moat", moat);
    	obstacleChooser.addObject("Ramparts", ramparts);
    	obstacleChooser.addObject("Rock Wall", rockWall);
    	obstacleChooser.addObject("Rough Terrain", roughTerrain);
    	SmartDashboard.putData("Auto Chooser 2", obstacleChooser);
    	
    	try {
	          ahrs = new AHRS(SPI.Port.kMXP); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	      }
    	/*
    	try {
	          driveGyro = new AHRS(SerialPort.Port.kUSB); 
	      } catch (RuntimeException ex ) {
	          DriverStation.reportError("Error instantiating navX MXP:  " + ex.getMessage(), true);
	          System.out.println("no");
	      }
	      */
    	try{
    	server = CameraServer.getInstance();
        server.setQuality(50);
        //the camera name (ex "cam0") can be found through the roborio web interface
        server.startAutomaticCapture("cam1");
    	}
    	catch(Exception e){}
        
        network = NetworkTable.getTable("SmartDashboard");
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		
		xVal = 0;
		yVal = 0;
		autoDistance = 0;
		//driveGyro.reset();
    }
    
    public void autonomousInit(){
    	
    	//xPID = new MyPIDController(.13, .2, 0);
    	//xPID = new MyPIDController(.17, .23, .008);
    	xPID = new MyPIDController(.09, .2, .004);
		xPID.setSetPoint(0);
		xPID.setTolerance(.5);
		xPID.setOutputRange(-.9, .9);
		xPID.setDivide(1.1);
    	winch.enable();
    	winch.setAngle(-10);
    	time.reset();
    	time.start();
    	driveTrain.resetEncoders();
    	ahrs.reset();
    	String positionSelected = (String) positionChooser.getSelected();
    	String obstacleSelected = (String) obstacleChooser.getSelected();
    	step = 1;
    	switch(positionSelected){
    	case lowBar:
    		winch.setAngle(-12);
    		driveTrain.setOutputRange(-.6, .6);
    		driveTrain.setDistanceSetpoint(-180);
    		driveTrain.pidEnable();
    		break;
    	case two:
    	case three:
    	case four:
    	case five:
    		switch(obstacleSelected){
    		case moat:
    		case rockWall:
    			//winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(BIG_OBSTACLE_DISTANCE);
    			autoDistance = BIG_OBSTACLE_DISTANCE;
    			driveTrain.setOutputRange(-.9,.9);
    			driveTrain.pidEnable();
    			shooter.shoot();
    			break;
    		case roughTerrain:
    			//winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(ROUGH_TERRAIN_DISTANCE);
    			autoDistance = ROUGH_TERRAIN_DISTANCE;
    			driveTrain.setOutputRange(-.8,.8);
    			driveTrain.pidEnable();
    			break;
    		case ramparts:
    			//winch.setAngle(15);
    			driveTrain.setDistanceSetpoint(-BIG_OBSTACLE_DISTANCE);
    			autoDistance = -BIG_OBSTACLE_DISTANCE;
    			driveTrain.setOutputRange(-.9,.9);
    			driveTrain.pidEnable();
    			break;
    		}
    		break;
    	}
    	
    }
    
    /**
     * This function is called periodically during autonomous
    */ 
    public void autonomousPeriodic(){
    	
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		yVal = (points[1]+points[3]+points[5]+points[7])/4;
//    		calculatedAngle = .1544349*yVal +  20.1191;
//    		if(yVal<135)
//    		{
//    			calculatedAngle = 41;
//    		}
    		//calculatedAngle = 22.77162165*Math.pow(1.0045814716809, yVal);
    		//calculatedAngle = .1182129115*yVal +  24.26737922189;
    		calculatedAngle = .1000664615*yVal +  25.31444167075;
    		cameraData = true;
    	}
    	catch(Exception e){
    		//System.out.println("No camera data");
    		cameraData = false;
    	}
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		xVal = (points[0]+points[2]+points[4]+points[6])/4;
    		cameraData = true;
    	}
    	catch(Exception e){
    		cameraData = false;
    	};
    	SmartDashboard.putNumber("Y Value", yVal);
    	SmartDashboard.putNumber("X Value", xVal);
    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	
    	String positionSelected = (String) positionChooser.getSelected();
    	String obstacleSelected = (String) obstacleChooser.getSelected();
    	switch(positionSelected){
    	case lowBar:
    		winch.controlWinch(-ahrs.getRoll());
    		if(step==1){
    			System.out.println("Step 1 "+driveTrain.leftEnc.getDistance());
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>180)
    			{
 
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(15);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		
    		if(step==2){
    			System.out.println("Step 2");
    			driveTrain.turnToAngle();
    			//System.out.println(ahrs.getYaw());
    			//System.out.println(driveTrain.pid.isEnabled());
    			if(driveTrain.getTurnDone())
    			{
    				step++;
    				driveTrain.resetEncoders();
    				ahrs.reset();
    				driveTrain.setDistanceSetpoint(-10);
    				driveTrain.pidEnable();
    				
    			}
    		}
    		
    		if(step==3)
    		{
    			System.out.println("Step 3");
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>10)
    			{
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(-130);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		if(step==4){
    			System.out.println(driveTrain.pid.isEnabled());
    			System.out.println(ahrs.getYaw());
    			System.out.println("Step 45");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone())
    			{
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setDistanceSetpoint(10);
    				driveTrain.pidEnable();
    				//winch.setAngle(35);
    			}
    		}
    		
    		if(step==5)
    		{
    			System.out.println("Step 5");
    			driveTrain.driveDistanceForwards();
    			
    			if(Math.abs(driveTrain.leftEnc.getDistance())>13 )
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    				winch.setAngle(calculatedAngle);
    				xPID.setSetPoint((xVal-140)/5);
    				xPID.enable();
    			}
    		}
    		/*
    		if(step==6)
    		{
    			System.out.print("Step 6");
    			
    			//winch.controlWinch(-ahrs.getRoll());
    			//if(!xPID.isEnabled())
	    		//	xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
				if(cameraData && time.get()>11)
				{
					xPID.setSetPoint((xVal-140)/5);
				}
				if(time.get()>12)
				{
					xPID.disable();
					step++;
				}
    		}
    		if(step==7)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			//winch.setAngle(-15);
    			//winch.controlWinch(-ahrs.getRoll());
    			winch.disable();
    			if(shooter.leftTalon.getSpeed()>4500 && time.get()>13)
    			{
    				step++;
    				shooter.shoot();
    			}
    		}
    		*/
    		/*
    		if(step==6)
    		{
    			winch.setAngle(56);
    			winch.changeAngle(-ahrs.getRoll());
    			if(Math.abs(-ahrs.getRoll()-56)<1)
    				step++;
    		}
    		if(step==7)
        	{
        		shooter.spinUp();
        		if(time.get()>13.5)
        			shooter.shoot();
        	}
        	*/
    		
    		
    		break;
    	case two:
    		winch.controlWinch(-ahrs.getRoll());
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>Math.abs(autoDistance))
    			{
    				if(obstacleSelected.equals("Ramparts"))
    					driveTrain.setAngleSetpoint(-140);
    				else
    					driveTrain.setAngleSetpoint(30);
    				ahrs.reset();
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    			}
    		}
    		/*
    		if(step==2){
    			System.out.println("Step 2");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone()){
    				step++;
    				ahrs.reset();
    				driveTrain.setTurnDone(true);
    				driveTrain.resetEncoders();
    				driveTrain.setDistanceSetpoint(50);
    				driveTrain.pidEnable();
    			}
    		}
    		if(step==3){
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>50){
    				step++;
    				ahrs.reset();
    				driveTrain.setAngleSetpoint(-30);
    				driveTrain.resetEncoders();
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    				winch.setAngle(40);
    			}
    		}
    		if(step==4)
    		{
    			System.out.println("Step 4");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone())
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(true);
    				*/
    				//winch.setAngle(calculatedAngle);
    				//xPID.setSetPoint((xVal-140)/5);
    				//xPID.enable();
    			
    		
    		
    		/*
    		if(step==5)
    		{
    			System.out.print("Step 5");
    			
    			//winch.controlWinch(-ahrs.getRoll());
    			//if(!xPID.isEnabled())
	    		//	xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
				
				if(time.get()>12)
				{
					xPID.disable();
					step++;
				}
    		}
    		if(step==6)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			//winch.setAngle(-15);
    			//winch.controlWinch(-ahrs.getRoll());
    			winch.disable();
    			if(shooter.leftTalon.getSpeed()>4500 && time.get()>13)
    			{
    				step++;
    				shooter.shoot();
    			}
    		}
    		if(step==7)
    		{
    			if(time.get()>14.5)
    			{
    				shooter.retract();
    				shooter.stop();
    			}
    		}
    		*/
    		break;
    	case three:
			winch.controlWinch(-ahrs.getRoll());
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>Math.abs(autoDistance))
    			{
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				if(obstacleSelected.equals("Ramparts")){
    					driveTrain.setAngleSetpoint(-145);
    				}
    				else{
    					driveTrain.setAngleSetpoint(15);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    				}
    			}
    		}
    		/*
    		if(step==2){
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone()){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setTurnDone(true);
    				driveTrain.setDistanceSetpoint(30);
    			}
    		}
    		if(step==3){
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>30){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setAngleSetpoint(-15);
    				driveTrain.setTurnDone(false);
    				winch.setAngle(40);
    			}
    		}
    		if(step==4)
    		{
    			System.out.println("Step 5");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone())
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(true);
    				*/
    				/*
    				winch.setAngle(calculatedAngle);
    				xPID.setSetPoint((xVal-140)/5);
    				xPID.enable();
    				*/
    			
    		
    		/*
    		if(step==5)
    		{
    			System.out.print("Step 6");
    			
    			//winch.controlWinch(-ahrs.getRoll());
    			//if(!xPID.isEnabled())
	    		//	xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
				
				if(time.get()>12)
				{
					xPID.disable();
					step++;
				}
    		}
    		if(step==6)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			//winch.setAngle(-15);
    			//winch.controlWinch(-ahrs.getRoll());
    			winch.disable();
    			if(shooter.leftTalon.getSpeed()>5000 && time.get()>13)
    			{
    				step++;
    				shooter.shoot();
    			}
    		}
    		if(step==7)
    		{
    			if(time.get()>14.5)
    			{
    				shooter.retract();
    				shooter.stop();
    			}
    		}
    		*/
    		break;
    	case four:
    		winch.controlWinch(-ahrs.getRoll());
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			
    			if(Math.abs(driveTrain.leftEnc.getDistance())>Math.abs(autoDistance))
    			{
    				ahrs.reset();
    				if(obstacleSelected.equals("Ramparts"))
    					driveTrain.setAngleSetpoint(160);
    				else
    					driveTrain.setAngleSetpoint(-10);
    				step++;
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    				shooter.retract();
    			}
    		}
    		/*
    		if(step==2){
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone()){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setTurnDone(true);
    				driveTrain.setDistanceSetpoint(30);
    				driveTrain.pidEnable();
    			}
    		}
    		if(step==3){
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>30){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.pidDisable();
    				driveTrain.setAngleSetpoint(10);
    				driveTrain.setTurnDone(false);
    				winch.setAngle(40);
    			}
    		}
    		if(step==4)
    		{
    			System.out.println("Step 5");
    			driveTrain.turnToAngle();  
    			if(driveTrain.getTurnDone())
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				//driveTrain.pidDisable();
    				driveTrain.setTurnDone(true);
    				*/
    				/*
    				winch.setAngle(calculatedAngle);
    				xPID.setSetPoint((xVal-140)/5);
    				xPID.enable();
    				*/
    			
    		
    		/*
    		if(step==5)
    		{
    			System.out.print("Step 6");
    			
    			//winch.controlWinch(-ahrs.getRoll());
    			//if(!xPID.isEnabled())
	    		//	xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
				
				if(time.get()>12)
				{
					xPID.disable();
					step++;
				}
    		}
    		if(step==6)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			//winch.setAngle(-15);
    			//winch.controlWinch(-ahrs.getRoll());
    			winch.disable();
    			if(shooter.leftTalon.getSpeed()>5000 && time.get()>13)
    			{
    				step++;
    				shooter.shoot();
    			}
    		}
    		if(step==7)
    		{
    			if(time.get()>14.5)
    			{
    				shooter.retract();
    				shooter.stop();
    			}
    		}
    		*/
    		break;
    	case five:
    		winch.controlWinch(-ahrs.getRoll());
    		if(step==1){
    			System.out.println("Step 1");
    			driveTrain.driveDistanceForwards();
    			
    			if(Math.abs(driveTrain.leftEnc.getDistance())>Math.abs(autoDistance))
    			{
    				ahrs.reset();
    				if(obstacleSelected.equals("Ramparts"))
    					driveTrain.setAngleSetpoint(140);
    				else
    					driveTrain.setAngleSetpoint(-30);
    				step++;
    				driveTrain.resetEncoders();
    				driveTrain.pidDisable();
    				driveTrain.setTurnDone(false);
    				shooter.retract();
    			}
    		}
    		/*
    		if(step==2){
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone()){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setTurnDone(true);
    				driveTrain.setDistanceSetpoint(30);
    				driveTrain.pidEnable();
    				winch.setAngle(15);
    				ahrs.reset();
    			}
    		}
    		if(step==3){
    			driveTrain.driveDistanceForwards();
    			if(Math.abs(driveTrain.leftEnc.getDistance())>30){
    				step++;
    				ahrs.reset();
    				driveTrain.resetEncoders();
    				driveTrain.setAngleSetpoint(15);
    				driveTrain.setTurnDone(false);
    				winch.setAngle(40);
    				driveTrain.pidDisable();
    			}
    		} 
    		if(step==4)
    		{
    			System.out.println("Step 5");
    			driveTrain.turnToAngle();
    			if(driveTrain.getTurnDone())
    			{
    				ahrs.reset();
    				//driveTrain.setAngleSetpoint(0);
    				step++;
    				//driveTrain.pidDisable();
    				driveTrain.setTurnDone(true);
    				/*
    				winch.setAngle(calculatedAngle);
    				xPID.setSetPoint((xVal-140)/5);
    				xPID.enable();
    				 
    			}
    		}
    		*/
    		/*
    		if(step==5)
    		{
    			System.out.print("Step 6");
    			
    			//winch.controlWinch(-ahrs.getRoll());
    			//if(!xPID.isEnabled())
	    		//	xPID.enable();
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
				
				if(time.get()>12)
				{
					xPID.disable();
					step++;
				}
    		}
    		if(step==6)
    		{
    			System.out.println("Step 7");
    			shooter.spinUp();
    			//winch.setAngle(-15);
    			//winch.controlWinch(-ahrs.getRoll());
    			winch.disable();
    			if(shooter.leftTalon.getSpeed()>5000 && time.get()>13)
    			{
    				step++;
    				shooter.shoot();
    			}
    		}
    		if(step==7)
    		{
    			if(time.get()>14.5)
    			{
    				shooter.retract();
    				shooter.stop();
    			}
    		}
    		*/
    		break;
    	}
    }
    

    public void teleopInit(){
    	shooter.stop();
    	driveTrain.pidDisable();
    	winch.setAngle(-ahrs.getRoll());
    	winch.enable();
    	driveModeHelper = false;
    	tankDrive = false;
    	isShooting = isStopping = isIntaking = push = pushed = false;
		pushed = false;
		sensitivity = 1;
		yVal = 0;
		xVal = 0;
		driveTrain.pidDisable();
		//driveGyro.reset();
		
		xPID = new MyPIDController(.09, .2, .004);
		xPID.setSetPoint(0);
		xPID.setTolerance(.5);
		xPID.setOutputRange(-.9, .9);
		xPID.setDivide(1.1);
		aPush = false;
		aPushed = false;
		xPush = false;
		xPushed = false;
		hatUp = hatDown = false;
		shooter.retract();
    }
    
    /**
     * This function is called periodically during operator control
     */
    @SuppressWarnings("deprecation")
	public void teleopPeriodic() {
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		yVal = (points[1]+points[3]+points[5]+points[7])/4;
//    		calculatedAngle = .1544349*yVal +  20.1191;
//    		if(yVal<135)
//    		{
//    			calculatedAngle = 41;
//    		}
    		//calculatedAngle = .1182129115*yVal +  24.26737922189;
    		calculatedAngle = .1000664615*yVal +  25.31444167075;
    	}
    	catch(Exception e){
    		//System.out.println("No camera data");
    	}
    	try{
    		double[] points = network.getNumberArray("BFR_COORDINATES");
    		xVal = (points[0]+points[2]+points[4]+points[6])/4;	
    	}
    	catch(Exception e){};
    	SmartDashboard.putNumber("Y Value", yVal);
    	SmartDashboard.putNumber("X Value", xVal);
    	SmartDashboard.putNumber("Calculated Angle", calculatedAngle);
    	
    	if(aPush && !oi.A.get())
    	{
    		aPush = false;
    		aPushed = !aPushed;
    		if(aPushed)
    		{
    			ahrs.reset();
    			xPID.setSetPoint((xVal-0-140)/5);
    			//xPID.setSetPoint(25);
//    			if(xVal<140)
//    			{
//    				xPID.setSetPoint((xVal-0-150)/5);
//    			}
//    			else
//    			{
//    				xPID.setSetPoint((xVal+0-150)/5);
//    			}
    				
    		}
    			
    	}
    	if(oi.A.get())
    		aPush = true;
    	
    	if(aPushed)
		{
    		try{
    			//double[] temp = network.getNumberArray("BFR_COORDINATES");
    			//double d = temp[1];
    			if(!xPID.isEnabled())
	    			xPID.enable();
    			if(Math.abs(xPID.getError(ahrs.getYaw()))<4)
    				xPID.setkD(0);
    			else
    				xPID.setkD(.008);
	    		double output = xPID.getOutput(ahrs.getYaw());
	    		//System.out.println("Output "+output);
	    		SmartDashboard.putNumber("xVal PID", output);
	    		SmartDashboard.putNumber("xPID Setpoint", xPID.getSetPoint());
				driveTrain.drive.arcadeDrive(0, output);
    		}
    		catch(Exception e){
    			driveTrain.drive.arcadeDrive(0, 0);
    			System.out.println("not seen");
    		}
	    		
		}
    	else
    	{
    		SmartDashboard.putNumber("xVal PID", 0);
    		xPID.disable();
    		
    	}
    	SmartDashboard.putNumber("Integral", xPID.getIntegral());
    	SmartDashboard.putBoolean("xVal PID Enabled", xPID.isEnabled());
    	
		if(oi.LB.get() && oi.RB.get())
		{
			//driveTrain.drive.setMaxOutput(Math.abs(oi.getSliderValue()-1));
			sensitivity = .7;
		}
		else if(oi.getRightTrigger()>.5 && oi.getLeftTrigger()>.5)
		{
			driveTrain.drive.setMaxOutput(1);
		}
		else
		{
			driveTrain.drive.setMaxOutput(.9);
			sensitivity = 1;
		}
		
    	if(driveModeHelper && !oi.back.get())
    	{
    		driveModeHelper = false;
    		tankDrive = !tankDrive;
    	}
    	if(oi.back.get())
    		driveModeHelper = true;
    	
    	if(xPush && !oi.X.get())
    	{
    		xPush = false;
    		xPushed = !xPushed;
    		if(xPushed)
    		{
    			ahrs.reset();
				driveTrain.setAngleSetpoint(-160);
				driveTrain.setTurnDone(false);
    		}
    		else
    		{
    			driveTrain.setTurnDone(true);
    		}
    	}
    	if(xPushed)
    	{
    		driveTrain.turnToAngle();
    		if(driveTrain.getTurnDone())
    		{
    			xPushed = false;
    		}
    	}
    	if(oi.X.get())
    		xPush = true;
    	
		if(!aPushed && !xPushed && tankDrive)
    	{	   	
    		driveTrain.drive.tankDrive(oi.getLeftY()*sensitivity, oi.getRightY()*sensitivity);
    	}
    	else if(!aPushed && !xPushed && !tankDrive)
    	{
    		driveTrain.drive.arcadeDrive(oi.getLeftY()*sensitivity, oi.getRightX()*sensitivity);
    	}

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
    		winch.enable();
    		xPID.enable();
    	}
   
    	if(isShooting)
    	{
    		shooter.spinUp();
    		winch.setWinchTolerance(1);
    		winch.disable();
    		xPID.disable();
    	}
    	else if(isIntaking)
    	{
    		shooter.intakeSpin();
    	}
    	else
    	{
    		shooter.stop();
    		//winch.enable();
    		winch.setWinchTolerance(.7);
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
    	
    	
    	if(oi.joy.getPOV()==0)
    	{
    		intake.intakeOut();
    		intake.spinIn();
    	}
    	if(oi.joy.getPOV()==180)
    	{
    		intake.intakeIn();
    	}
    	if(oi.joy.getPOV()==90)
    	{
    		intake.spinIn();
    	}
    	if(oi.joy.getPOV()==270)
    	{
    		intake.spinOut();
    	}
    	if(oi.Y.get())
    	{
    		intake.stop();
    	}
    	//this mapping sucks major ass!!!!! <---  ***** !!!!!
    	//SmartDashboard.putNumber("Flight POV", oi.flight.getPOV());
//    	if(oi.b12.get())
//    	{
//    		if(hatUp==true && oi.flight.getPOV()==-1)
//    		{
//    			hatUp = false;
//    			winch.changeAngle(1);
//    		}
//    		if(oi.flight.getPOV()==0)
//    			hatUp = true;
//    		if(hatDown==true && oi.flight.getPOV()==-1)
//    		{
//    			hatDown = false;
//    			winch.changeAngle(-1);
//    		}
//    		if(oi.flight.getPOV()==180)
//    			hatDown = true; 
//    	}
    	
    	/*
    	if(oi.rightBig.get())
    		winch.setAngle(calculatedAngle);
    	
    	if(oi.b9.get()){
    		winch.setAngle(25);
    		SmartDashboard.putNumber("Set Angle", 25);
    	}
    		
    	if(oi.b7.get()){
    		winch.setAngle(35);
    		SmartDashboard.putNumber("Set Angle", 35);
    	}
    		
    	if(oi.b8.get()){
    		winch.setAngle(45);
    		SmartDashboard.putNumber("Set Angle", 45);
    	}
    		
    	if(oi.b11.get()){
    		winch.setAngle(0);
    		SmartDashboard.putNumber("Set Angle", -10);
    	}
    		
    	if(oi.b10.get()){
    		winch.setAngle(-15);
    		SmartDashboard.putNumber("Set Angle", -15);
    	}
    	if(oi.b12.get()){
    		winch.setAngle(-ahrs.getRoll());
    		System.out.println(-ahrs.getRoll());
    		SmartDashboard.putNumber("Set Angle", ahrs.getRoll());
    	}
    	*/
    	
    	//if(oi.thumb.get())
        winch.controlWinch(-ahrs.getRoll());
       	//else
       		//winch.controlWinch(oi.flight.getRawAxis(1), -ahrs.getRoll());
        if(oi.thumb.get())
        	//winch.changeAngle(oi.flight.getRawAxis(1)*.6);

        
        
        
    	SmartDashboard.putNumber("Roll", ahrs.getRoll());
    	SmartDashboard.putNumber("Yaw", ahrs.getYaw());
    	SmartDashboard.putNumber("Pitch", ahrs.getPitch());
    	SmartDashboard.putNumber("Slider Value", oi.getSliderValue());
    	SmartDashboard.putBoolean("Piston", shooter.out);
    	SmartDashboard.putNumber("Seconds Remaining", 150 - time.get());
    	/*
    	SmartDashboard.putNumber("Drive Roll", driveGyro.getRoll());
    	SmartDashboard.putNumber("Drive Angle", driveGyro.getAngle());
    	SmartDashboard.putNumber("Drive Yaw", driveGyro.getYaw());
    	*/
    	shooter.log();
    	driveTrain.log();
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
