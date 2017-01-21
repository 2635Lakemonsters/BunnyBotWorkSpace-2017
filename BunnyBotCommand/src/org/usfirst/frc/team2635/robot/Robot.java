package org.usfirst.frc.team2635.robot;

import com.kauailabs.navx.frc.AHRS;

//import edu.wpi.first.wpilibj.CANTalon;
import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.IterativeRobot;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.PIDController;
import edu.wpi.first.wpilibj.Relay;
import edu.wpi.first.wpilibj.RobotDrive;
import edu.wpi.first.wpilibj.buttons.Button;
import edu.wpi.first.wpilibj.buttons.JoystickButton;
import edu.wpi.first.wpilibj.command.Command;
//import edu.wpi.first.wpilibj.command.Scheduler;
import edu.wpi.first.wpilibj.livewindow.LiveWindow;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import edu.wpi.first.wpilibj.SerialPort;
import edu.wpi.first.wpilibj.Timer;

import com.ctre.CANTalon;


import com.lakemonsters2635.sensor.modules.SensorUnwrapper;

public class Robot extends IterativeRobot {
	private static final double COUNTPERDISTANCE = 0.2;
	int autoLoop = 0;
	Button button;
	Command autonomousCommand;
	Relay relay;
	CANTalon motorLeft;
	CANTalon motorRight;
	CANTalon flywheelLeft;
	CANTalon flywheelRight;
	CANTalon frontLeftMotor;
	CANTalon backLeftMotor;
	CANTalon frontRightMotor;
	CANTalon backRightMotor;
	Joystick leftJoystick;
	Joystick rightJoystick;
	Joystick shootJoystick;
	RobotDrive drive;
	ShooterModes shooterLeft;
	ShooterModes shooterRight;
	DigitalInput nerfSwitchLeft;
	DigitalInput nerfSwitchRight;
	Encoder turntableEncoder;
	ShooterEnabled shooterEnabled;
	ShooterEnabled ShooterEnabled;
	SendableChooser chooser;
	AHRS navx;
	SensorUnwrapper angleUnwrapper;
	CANTalon turntable;
	Turntable table;
	PIDDrive pidDrive;
	PIDController angleController;
	Bling bling;
	
	
	
	double wheel;
	boolean nerfSwLeft;
	boolean nerfSwRight;
	public static OI oi;
	private static final int NERF_MOTOR_LEFT = 6;
	private static final int NERF_FLYWHEEL_LEFT = 5;
	private static final int NERF_MOTOR_RIGHT = 3;
	private static final int NERF_FLYWHEEL_RIGHT = 4;
	private static final int NERF_SWITCH_LEFT = 2;
	private static final int NERF_SWITCH_RIGHT = 3;
	private static final int LEFT_JOYSTICK_CHANNEL = 1;
	private static final int RIGHT_JOYSTICK_CHANNEL = 0;
	private static final int SHOOT_JOYSTICK_CHANNEL = 2;
	private static final int TURNTABLE_MOTOR_CHANNEL = 7;
	private static final int FRONT_RIGHT_MOTOR_CHANNEL = 8;
	private static final int FRONT_LEFT_MOTOR_CHANNEL = 2;
	private static final int BACK_RIGHT_MOTOR_CHANNEL = 9;
	private static final int BACK_LEFT_MOTOR_CHANNEL = 1;
	private static final int ENCODER_CHANNEL_A = 0;
	private static final int ENCODER_CHANNEL_B = 1;

	public void robotInit() {
		shooterEnabled = new ShooterEnabled();
		shooterEnabled.isEnabled = false;
		motorLeft = new CANTalon(NERF_MOTOR_LEFT);
		flywheelLeft = new CANTalon(NERF_FLYWHEEL_LEFT);
		motorRight = new CANTalon(NERF_MOTOR_RIGHT);
		flywheelRight = new CANTalon(NERF_FLYWHEEL_RIGHT);
		
		leftJoystick = new Joystick(LEFT_JOYSTICK_CHANNEL);
		rightJoystick = new Joystick(RIGHT_JOYSTICK_CHANNEL);
		
		shootJoystick = new Joystick(SHOOT_JOYSTICK_CHANNEL);
		button = new JoystickButton(leftJoystick, 1);
		relay = new Relay(0);
		shooterLeft = new ShooterModes();
		shooterRight = new ShooterModes();
		nerfSwitchLeft = new DigitalInput(NERF_SWITCH_LEFT);
		nerfSwitchRight = new DigitalInput(NERF_SWITCH_RIGHT);
		frontLeftMotor = new CANTalon(FRONT_LEFT_MOTOR_CHANNEL);
		backLeftMotor = new CANTalon(BACK_LEFT_MOTOR_CHANNEL);
		frontRightMotor = new CANTalon(FRONT_RIGHT_MOTOR_CHANNEL);
		backRightMotor = new CANTalon(BACK_RIGHT_MOTOR_CHANNEL);
		drive = new RobotDrive(frontLeftMotor, backLeftMotor, frontRightMotor, backRightMotor);
		navx = new AHRS(SerialPort.Port.kMXP);
		angleUnwrapper = new SensorUnwrapper(180.0, new SensorNavxAngle(navx));
		// turntable = new CANTalon(TURNTABLE_MOTOR_CHANNEL);
		// turntableEncoder = new Encoder(ENCODER_CHANNEL_A, ENCODER_CHANNEL_B);
		table = new Turntable(ENCODER_CHANNEL_A, ENCODER_CHANNEL_B, TURNTABLE_MOTOR_CHANNEL,
				angleUnwrapper.sense(null));
		bling = new Bling();

		SmartDashboard.putNumber("Set P", 0.011);
		SmartDashboard.putNumber("Set I", 0.001);
		SmartDashboard.putNumber("Set D", 0);
		
		SmartDashboard.putNumber("Set PAuto", -0.015);
		SmartDashboard.putNumber("Set IAuto", -0.0007);
		SmartDashboard.putNumber("Set DAuto", -0.0);

		pidDrive = new PIDDrive(drive);
		angleController = new PIDController(-0.015, -0.0001, -0.0, angleUnwrapper, pidDrive);
		
		bling.set(0);
	}

	public void disabledInit() {
		System.out.println("Why did you do that? I just wanted to be your friend!");
	}

	public void disabledPeriodic() {
		// Scheduler.getInstance().run();
	}

	public void autonomousInit()
	{
		autoLoop = 0;
		angleController.setPID(SmartDashboard.getNumber("Set PAuto"), SmartDashboard.getNumber("Set IAuto"), SmartDashboard.getNumber("Set DAuto"));
		angleController.enable();
		
		forward(10);
		
/**		turnRight();
		
		forward(9);
		
		turnLeft();
		
		forward(25);
		
		turnLeft();
		
		forward(9);
		
		turnLeft();
		
		forward(20);
		
		turnLeft();
		
		forward(9); 
		
		turnRight();
		
		forward(20);
		
**/		stop();
		
		angleController.disable();
		angleController.reset();
	}
	//long prevTime = System.currentTimeMillis();
	
	public void autonomousPeriodic() 
	{
		
		int nextCount = 0;
		int startCount = 0;
		
		/*nextCount = forward(startCount, 20);
		nextCount = stop(nextCount);
		*/
//		nextCount = forward(nextCount, 9);
//		nextCount = stop(nextCount);
//		nextCount = turnLeft(nextCount);
//		nextCount = forward(nextCount, 20);
		
		autoLoop++;	
		SmartDashboard.putNumber("Angle", angleUnwrapper.sense(null));
		SmartDashboard.putNumber("Error", angleController.getError());
		SmartDashboard.putNumber("setpoint", angleController.getSetpoint());
	
		//nextCount = end(startCount);
		
		//autoLoop++;	
//		long currentTime = System.currentTimeMillis();
//		System.out.println("autoLoop: " + autoLoop);
//		System.out.println("Time delta: " + ((Long)currentTime-prevTime));
//		prevTime = currentTime;
	
	}
	
	

	
	@Override
	public void teleopInit() {
		if (autonomousCommand != null)
		{
			autonomousCommand.cancel();
		}
		angleController.disable();
		drive.arcadeDrive(0, 0);
	}

	// Scheduler.getInstance().run();
	public void teleopPeriodic() 
	{
		
		if (shootJoystick.getRawButton(4)){
			flywheelLeft.set(.4);
			flywheelRight.set(.4);
		} else if(shootJoystick.getRawButton(5)&&shootJoystick.getRawButton(6)){
			flywheelLeft.set(.5);
			flywheelRight.set(.5);
		} else{
			flywheelLeft.set(.3);
			flywheelRight.set(.3);
		}
		
		
		wheel = leftJoystick.getRawAxis(2);
		shooterLeft.setSwitch(nerfSwitchLeft.get());
		
		shooterLeft.modeChange(shootJoystick.getRawButton(3), shootJoystick.getRawButton(1), shootJoystick.getRawButton(2));
		shooterLeft.update(shootJoystick.getRawAxis(2) > .5);
		
		motorLeft.set(shooterLeft.getMotorSpeed() / 2);
		shooterRight.setSwitch(!nerfSwitchRight.get());
		shooterRight.modeChange(shootJoystick.getRawButton(3), shootJoystick.getRawButton(1), shootJoystick.getRawButton(2));
		shooterRight.update(shootJoystick.getRawAxis(3) > .5);
		motorRight.set(shooterRight.getMotorSpeed() / 2);
		
	    if (shooterLeft.getBling() == 1 || shooterRight.getBling() == 1)
	    {
	    	bling.set(1);
	    } else if(shooterLeft.getBling() == 2 || shooterRight.getBling() == 2) 
	    {
	    	bling.set(2);
	    }
	    else 
	    {
	    	bling.set(0);
	    }
		
		/*
		 * if(joystick.getRawButton(2)){ flywheel.set(0.4); } else
		 * if(joystick.getRawButton(8)){ flywheel.set(0.0); } else
		 * if(joystick.getRawButton(6)){
		 */
		//flywheel.set(0.3);
		/*
		 * } else if(joystick.getRawButton(7)){ flywheel.set(0.5); } else{
		 * flywheel.set(wheel/2); }
		 */
		//System.out.println(wheel);
		drive.arcadeDrive(-rightJoystick.getRawAxis(1), -rightJoystick.getRawAxis(0) * 0.75); //Uncomment this and comment the line below to use drive station controllers.
		
		//drive.arcadeDrive(rightJoystick.getRawAxis(1), rightJoystick.getRawAxis(0)); // This is if you're using xbox controller to control drive
		// turntable.set(shootJoystick.getRawAxis(0));

		table.setPID(SmartDashboard.getNumber("Set P"), SmartDashboard.getNumber("Set I"),
				SmartDashboard.getNumber("Set D"));
		table.update(shootJoystick.getRawAxis(0), angleUnwrapper.sense(null));

		SmartDashboard.putNumber("Raw Angle", navx.getAngle());
		SmartDashboard.putNumber("Angle", angleUnwrapper.sense(null));
		SmartDashboard.putBoolean("Left Switch", nerfSwitchLeft.get());
		SmartDashboard.putBoolean("Right Switch", nerfSwitchRight.get());
		
		// SmartDashboard.putNumber("Encoder Position",
		// turntableEncoder.getDistance());
		// SmartDashboard.putNumber("Encoder Rate", turntableEncoder.getRate());
	}

	public void testPeriodic() {
		LiveWindow.run();
	}
	Timer timer = new Timer();
	public void forward(/*int startCount,*/ double distance) {
		//CountPerDistance assumes a 20 ms loop time.
		int finishCount = (int)((distance / COUNTPERDISTANCE) * 20);
//		if (autoLoop < finishCount && autoLoop >= startCount) {
//			angleController.setSetpoint(angleUnwrapper.sense(null));
//			pidDrive.setForward(1);
//			System.out.println("forward: startCount: " + startCount + " finishCount: " + finishCount);
//
//			//autoLoop++;
//		}
//		
		angleController.setSetpoint(angleUnwrapper.sense(null));
		pidDrive.setForward(1);
		Timer timer = new Timer(); 


		timer.start();
		while(timer.get() * 1000 < finishCount) {}
		
		timer.stop();
		timer.reset();
		
		//return finishCount;
		
	}

	public void stop(/*int startCount*/) {

//		int finishCount = (int) (startCount+12);
//		if (autoLoop < finishCount && autoLoop >= startCount) {
//			pidDrive.setForward(-1);
//			System.out.println("stop: startCount: " + startCount + " finishCount: " + finishCount);
//
//			//autoLoop++;
//		}
//		return finishCount;
		angleController.setSetpoint(angleUnwrapper.sense(null));
		pidDrive.setForward(0);
	}

	public void turnLeft(/*int startCount*/) {
		
//		int finishCount = (int) (startCount+40);
//		if (autoLoop < finishCount && autoLoop >= startCount) {
//			angleController.setSetpoint(angleUnwrapper.sense(null) - 90); 
//			pidDrive.setForward(0);
//			System.out.println("left: startCount: " + startCount + " finishCount: " + finishCount);
//
//			//autoLoop++;
//		}
//		return finishCount;
		angleController.setSetpoint(angleUnwrapper.sense(null) - 90); 
		pidDrive.setForward(0.0);
		Timer timer = new Timer(); 


		timer.start();
		while(timer.get() * 1000 < 2000) {}
		
		timer.stop();
		timer.reset();
		//Wait untill the angle is within 5.0 degrees of the target angle.
//		while(Math.abs(angleController.getError()) > 5.0){}
	}

	public void turnRight(/*int startCount*/) {
//		int finishCount = (int) (startCount);
//		if (autoLoop < finishCount && autoLoop >= startCount) {
//			angleController.setSetpoint(angleUnwrapper.sense(null) + 90);
//			pidDrive.setForward(0);
//			System.out.println("right: startCount: " + startCount + " finishCount: " + finishCount);
//
//			//autoLoop++;
//		}
//		return finishCount;
		
		angleController.setSetpoint(angleUnwrapper.sense(null) + 90);
		pidDrive.setForward(0);
		Timer timer = new Timer();
		timer.start();
		while(timer.get() * 1000 < 2000) {}
		
		timer.stop();
		timer.reset();	

	}

	
//	public void end(int startCount)
//	{
//		double time = timer.get() * 1000;
//		
//		if(time > startCount)
//		{
//			drive.tankDrive(0, 0);
//		}
//	}
	
}
