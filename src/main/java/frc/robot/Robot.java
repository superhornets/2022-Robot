// Copyright (c) FIRST and other WPILib contributors.
// Open Source Software; you can modify and/or share it under the terms of
// the WPILib BSD license file in the root directory of this project.

package frc.robot;

import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.Timer;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;

import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
//import com.kauailabs.navx.frc.AHRS;


import java.sql.Time;
import javax.swing.plaf.TextUI;

import com.ctre.phoenix.motorcontrol.can.TalonSRX;

//import com.ctre.phoenix.motorcontrol.can.VictorSPX;

import edu.wpi.first.wpilibj.DigitalInput;
import edu.wpi.first.wpilibj.Joystick;
import edu.wpi.first.wpilibj.Servo;
import edu.wpi.first.wpilibj.Encoder;
import edu.wpi.first.wpilibj.drive.DifferentialDrive;
import edu.wpi.first.wpilibj.motorcontrol.MotorController;
import edu.wpi.first.wpilibj.motorcontrol.PWMSparkMax;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonFX;
import edu.wpi.first.wpilibj.motorcontrol.PWMTalonSRX;
import edu.wpi.first.wpilibj.motorcontrol.PWMVictorSPX;
import edu.wpi.first.wpilibj.motorcontrol.Talon;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;
import com.ctre.phoenix.motorcontrol.can.*;
import com.ctre.phoenix.motorcontrol.ControlMode;
import com.ctre.phoenix.motorcontrol.FeedbackDevice;
import com.ctre.phoenix.motorcontrol.StatusFrameEnhanced;

/**
 * The VM is configured to automatically run this class, and to call the functions corresponding to
 * each mode, as described in the TimedRobot documentation. If you change the name of this class or
 * the package after creating this project, you must also update the build.gradle file in the
 * project.
 */
public class Robot extends TimedRobot {
  private static final String kDefaultAuto = "Default";
  private static final String kCustomAuto = "My Auto";
  private String m_autoSelected;
  private final SendableChooser<String> m_chooser = new SendableChooser<>();
  private Joystick m_leftStick;
  private Joystick m_rightStick;


  private final MotorController m_leftMotor = new Talon(0);
  private final MotorController m_rightMotor = new Talon(1);
  private final DifferentialDrive m_robotDrive = new DifferentialDrive(m_leftMotor, m_rightMotor);
  private final MotorController intakeMotor = new Talon(2);
  private final MotorController intakeRasingMotor = new PWMVictorSPX(5);
  private final MotorController loaderMotor = new PWMTalonSRX(3);
  private final MotorController shooterMotor = new PWMTalonSRX(4);
  private final MotorController hangerMotor = new PWMTalonSRX(6);
  private final DigitalInput lowerIntakeLimitSwitch = new DigitalInput(4);
  private final DigitalInput upperIntakeLimitSwitch = new DigitalInput(5);
  private final DigitalInput slowModeSwitch = new DigitalInput(9);
  //private final TalonSRX encoderMotor = new TalonSRX(6);
  private final WPI_TalonSRX encoderMotor = new WPI_TalonSRX(6);
  //private final AHRS ahrs;
  private final Encoder shooterEncoder = new Encoder(0,1);

  private double shooterSpeed = 0;
  private int autoStage = 0;
  private double lastTime = 15;
  private Servo hangerServo = new Servo(7);
  private double driveSpeed = 0;
  private double drivePos = 0;
  private double driveVelocity = 0;
  private double shooterEncoderValue = 0;
  
  
  /**
   * This function is run when the robot is first started up and should be used for any
   * initialization code.
   */
  @Override
  public void robotInit() {
    m_chooser.setDefaultOption("Default Auto", kDefaultAuto);
    m_chooser.addOption("My Auto", kCustomAuto);
    SmartDashboard.putData("Auto choices", m_chooser);
    m_leftStick = new Joystick(0);
    m_rightStick = new Joystick(1);
    m_rightMotor.setInverted(true);
    intakeMotor.setInverted(true);
    intakeRasingMotor.setInverted(true);
    loaderMotor.setInverted(true);
    hangerMotor.setInverted(true);
    hangerServo.setAngle(0);
    encoderMotor.configSelectedFeedbackSensor(FeedbackDevice.CTRE_MagEncoder_Relative);
    shooterEncoder.setDistancePerPulse(1);
  }

  /**
   * This function is called every 20 ms, no matter the mode. Use this for items like diagnostics
   * that you want ran during disabled, autonomous, teleoperated and test.
   *
   * <p>This runs after the mode specific periodic functions, but before LiveWindow and
   * SmartDashboard integrated updating.
   */
  @Override
  public void robotPeriodic() {

  }
  /**
   * This autonomous (along with the chooser code above) shows how to select between different
   * autonomous modes using the dashboard. The sendable chooser code works with the Java
   * SmartDashboard. If you prefer the LabVIEW Dashboard, remove all of the chooser code and
   * uncomment the getString line to get the auto name from the text box below the Gyro
   *
   * <p>You can add additional auto modes by adding additional comparisons to the switch structure
   * below with additional strings. If using the SendableChooser make sure to add them to the
   * chooser code above as well.
   */
  @Override
  public void autonomousInit() {
    m_autoSelected = m_chooser.getSelected();
    m_autoSelected = SmartDashboard.getString("Auto Selector", kDefaultAuto);
    System.out.println("Auto selected: " + m_autoSelected);
  }

  /** This function is called periodically during autonomous. */
  @Override
  public void autonomousPeriodic() {
    switch (m_autoSelected) {
      case kDefaultAuto:
        // Put custom auto code here
        if (autoStage == 0){
          intakeRasingMotor.set(.5);
          intakeMotor.set(-.5);
          if(Timer.getMatchTime()<12 || lowerIntakeLimitSwitch.get()){
            lastTime = Timer.getMatchTime();
            autoStage = 1;
            intakeRasingMotor.set(0);
          }
        }else if (autoStage == 1){
          shooterMotor.set(.5);
          if(lastTime - Timer.getMatchTime() > .5){
            lastTime = Timer.getMatchTime();
            autoStage = 2;
          }
        }else if (autoStage == 2){
          loaderMotor.set(.5);
          if (lastTime - Timer.getMatchTime() > 1.5){
            lastTime = Timer.getMatchTime();
            autoStage = 3;
            intakeMotor.set(0);
            shooterMotor.set(0);
            loaderMotor.set(0);
          }
        }else if (autoStage == 3){
          m_robotDrive.arcadeDrive(-.61, 0);
          if(lastTime - Timer.getMatchTime() > 2){
            m_robotDrive.arcadeDrive(0, 0);
            autoStage = 4;

          }
        }
        break;
      case kCustomAuto:
      default:
        // Put default auto code here
        break;
    }
  }

  /** This function is called once when teleop is enabled. */
  @Override
  public void teleopInit() {}

  /** This function is called periodically during operator control. */
  @Override
  public void teleopPeriodic() {
   /** takes input from the joystick and controls the drive motors using arcade drive */
    System.out.println(encoderMotor);
    if (m_leftStick.getRawButton(1) || slowModeSwitch.get()){
      m_robotDrive.arcadeDrive(((-m_leftStick.getY())/2), ((m_leftStick.getX())/2));
       //System.out.println("y");
      //System.out.println(-m_leftStick.getY()/2);
      //System.out.println("x");
      //System.out.println(-m_leftStick.getX()/2);
      //System.out.println("r");
      //System.out.println(m_rightMotor.get());
      //System.out.println("l");
      //System.out.println(m_leftMotor.get());
      //driveSpeed = encoderMotor.
      drivePos = encoderMotor.getSelectedSensorPosition();
      driveVelocity = encoderMotor.getSelectedSensorVelocity();
      
      //System.out.println("speed" + driveSpeed);
      System.out.println("pos" + drivePos);
      System.out.println("velocity" + driveVelocity);
      
      
     
    }
    else{
      m_robotDrive.arcadeDrive(-m_leftStick.getY(), m_leftStick.getX());
    }
    /** Controls the speed of the intake */
    if(m_leftStick.getRawButton(3)){
      intakeMotor.set(1);
    }
    else if(m_leftStick.getRawButton(2)){
      intakeMotor.set(-1);
    }
    else{
      intakeMotor.set(0);
    }
    
    /** raises and lowers the intake */
    if((m_leftStick.getRawButton(6) || m_leftStick.getRawButton(11)) && upperIntakeLimitSwitch.get()){
      intakeRasingMotor.set(-.5);
    }
    else if((m_leftStick.getRawButton(7) || m_leftStick.getRawButton(10)) && lowerIntakeLimitSwitch.get()){
      intakeRasingMotor.set(.5);
    }
    else{
      intakeRasingMotor.set(0);
    }
    
    /** controls the speed of the loader */
    if(m_rightStick.getRawButton(1)){
      loaderMotor.set(.5);
    }
    else if(m_rightStick.getRawButton(9)){
      loaderMotor.set(-.5);
    }
    else{
      loaderMotor.set(0);
    }
    
    /** toggles shooter speed */
    if(m_rightStick.getRawButton(4)){
      shooterSpeed = 0;
    }
    else if(m_rightStick.getRawButton(3)){
      shooterSpeed = .4;
    }
    else if(m_rightStick.getRawButton(5)){
      shooterSpeed = -.5;
    }
    /** sets the shooter speed each time through the loop. This is nessesary if the motor shuts off without an input. */
    shooterMotor.set(shooterSpeed);
    shooterEncoderValue = shooterEncoder.getRate();
    System.out.println("Shooter encoder rate" + shooterEncoderValue);
  

    /** controls the hanger motor */
    if(m_rightStick.getRawButton(11)){
      hangerMotor.set(.5);
    }
    else if(m_rightStick.getRawButton(10)){
      hangerMotor.set(-.5);
    }
    else{
      hangerMotor.set(0);
    }

    /** controls the hanger servo */
    if(m_rightStick.getRawButton(6)){
      hangerServo.setAngle(90);
    }
    else if(m_rightStick.getRawButton(7)){
      hangerServo.setAngle(0);
    }
  }

  /** This function is called once when the robot is disabled. */
  @Override
  public void disabledInit() {}

  /** This function is called periodically when disabled. */
  @Override
  public void disabledPeriodic() {}

  /** This function is called once when test mode is enabled. */
  @Override
  public void testInit() {}

  /** This function is called periodically during test mode. */
  @Override
  public void testPeriodic() {}

  /** This function is called once when the robot is first started up. */
  @Override
  public void simulationInit() {}

  /** This function is called periodically whilst in simulation. */
  @Override
  public void simulationPeriodic() {}
}
