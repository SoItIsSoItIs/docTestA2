package frc.robot;

import com.ctre.phoenix6.StatusCode;
import com.ctre.phoenix6.configs.TalonFXConfiguration;
import com.ctre.phoenix6.configs.TalonFXConfigurator;
import com.ctre.phoenix6.controls.DutyCycleOut;
import com.ctre.phoenix6.controls.MotionMagicDutyCycle;
import com.ctre.phoenix6.controls.StrictFollower;
import com.ctre.phoenix6.hardware.TalonFX;
import com.ctre.phoenix6.signals.InvertedValue;
import com.ctre.phoenix6.signals.NeutralModeValue;
import edu.wpi.first.wpilibj.DutyCycleEncoder;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Arm {
  private final TalonFX armMotor1 = new TalonFX(14); // One of the motors that controls the arm.
  private final TalonFX armMotor2 = new TalonFX(15); // The other motor that controls the arm.
  private final double motorCurrentLimit = 40.0; // Motor current limit in amps. Should be based on the breaker used in the PDP.
  private final int maxMotorFailures = 3; // The number of times a motor will attempt to reconfigure before declaring a failure and putting the device into a manual state.

  // Indicates whether the motor failed to configure on startup. Each motor will attempt to configure up to the number of times specified by maxMotorFailures
  private boolean armMotor1Failure = false; 
  private boolean armMotor2Failure = false; 

  private final DutyCycleEncoder armEncoder = new DutyCycleEncoder(2); // Keeps track of the angle of the arm.
  private double armEncoderZero = 0.0; // The initial arm position reading of the encoder in rotations. TODO: Find this value.
  private double armSetpoint = 50.0; // The last requested setpoint of the arm in degrees. 0 degrees is horizontal and 90 degrees is vertical. 
  private final double armTol = 0.5; // The acceptable error in the angle of the arm in degrees.
  private final double gearRatio = 1.0; // TODO: Find this value.

  private boolean manualControl = false; // Indicates whether the arm is under manual control. This can happen if there is a motor failure, or if the operator requests it via setManualControl().
  private double manualPower = 0.0; // Stores the desired output of the arm motor when it is under manual control.

  public Arm() {
    armMotor1Failure = !configMotor(armMotor1, armMotor1Failure, false);
    armMotor2Failure = !configMotor(armMotor2, armMotor2Failure, true);
    armMotor2.setControl(new StrictFollower(armMotor1.getDeviceID()));
    manualControl = getMotorFailure();
  }

  // Should be called once teleopPeriodic() and autoPeriodic() sections of the main robot code. Neccesary for the class to function.
  public void periodic() {
    updateDashboard();
    if (manualControl) {
      if (!armMotor1Failure) {
        armMotor1.setControl(new DutyCycleOut(manualPower));
      }
      if (!armMotor2Failure) {
        armMotor2.setControl(new DutyCycleOut(manualPower));
      }
    } else {
      manualPower = 0.0;
      double currentMotorPos = armMotor1.getRotorPosition().getValueAsDouble();
      double motorSetpoint = currentMotorPos + (armSetpoint - getArmEncoder())*gearRatio;
      armMotor1.setControl(new MotionMagicDutyCycle(motorSetpoint));
    }
  }

  // Returns true if the arm currently at the angle specified by armSetpoint, within the tolerance specified by armTol.
  public boolean atSetpoint() {
    return true; //Math.abs(getArmEncoder() - armSetpoint) < armTol;
  }

  // Changes the angle that the arm will move to. Units: degrees
  public void updateSetpoint(double _armSetpoint) {
    armSetpoint = _armSetpoint;
  }

  // Returns the position of the arm in degrees.
  public double getArmEncoder() {
    return armEncoder.getAbsolutePosition()*360.0 - armEncoderZero; 
  }

  public void setManualPower(double _manualPower) {
    manualPower = _manualPower;
  }

  // Toggles whether the arm is under manual control. Useful in the case of motor issues.
  public void toggleManualControl() {
    manualControl = !manualControl;
  }

  // Returns true if the arm is under manual control, and false if it is automated.
  public boolean getManualControl() {
    return manualControl;
  }
  
  // Attempts to reboot by reconfiguring the motors. Use if trying to troubleshoot during a match.
  public void reboot() {
    armMotor1Failure = !configMotor(armMotor1, armMotor1Failure, false);
    armMotor2Failure = !configMotor(armMotor2, armMotor2Failure, true);
    armMotor2.setControl(new StrictFollower(armMotor1.getDeviceID()));
    manualControl = getMotorFailure();
  }

  // Returns true if either of the motors failed to configure on startup or reboot.
  public boolean getMotorFailure() {
    return armMotor1Failure || armMotor2Failure;
  }

  // Sends information to the dashboard each period. This is handled automatically by the class.
  private void updateDashboard() {
    SmartDashboard.putBoolean("manualArmControl", manualControl);
    SmartDashboard.putBoolean("armFailure", getMotorFailure());
    SmartDashboard.putBoolean("atArmSetpoint", atSetpoint());
    SmartDashboard.putNumber("armSetpoint", armSetpoint);
    SmartDashboard.putNumber("armAngle", getArmEncoder());
  }

  // Sets PID constants, brake mode, inverts, and enforces a 40 A current limit. Returns true if the motor successfully configured.
  private boolean configMotor(TalonFX _motor, boolean motorFailure, boolean isInverted) {
    // Creates a configurator and config object to configure the motor.
    TalonFXConfigurator motorConfigurator = _motor.getConfigurator();
    TalonFXConfiguration motorConfigs = new TalonFXConfiguration();

    motorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Brake; // Motor brakes instead of coasting.
    motorConfigs.MotorOutput.Inverted = isInverted ? InvertedValue.Clockwise_Positive : InvertedValue.CounterClockwise_Positive; // Inverts the direction of positive motor velocity.

    // Setting current limits
    motorConfigs.CurrentLimits.SupplyCurrentLimitEnable = true;
    motorConfigs.CurrentLimits.SupplyCurrentLimit = motorCurrentLimit;
    motorConfigs.CurrentLimits.SupplyCurrentThreshold = motorCurrentLimit;
    motorConfigs.CurrentLimits.SupplyTimeThreshold = 0.5;

    // Velocity PIDV constants for reaching flywheel velocities
    motorConfigs.Slot0.kP = 0.008;
    motorConfigs.Slot0.kI = 0.06;
    motorConfigs.Slot0.kD = 0.0002;
    motorConfigs.Slot0.kV = 0.009;

    // Motion Magic Parameters for moving set distances
    motorConfigs.Slot1.kP = 0.8;
    motorConfigs.Slot1.kI = 2.0;
    motorConfigs.Slot1.kD = 0.006;
    motorConfigs.MotionMagic.MotionMagicAcceleration = 75.0;
    motorConfigs.MotionMagic.MotionMagicCruiseVelocity = 50.0;
    motorConfigs.MotionMagic.MotionMagicJerk = 400.0;
    
    // Attempts to repeatedly configure the motor up to the number of times indicated by maxMotorFailures
    int motorErrors = 0;
    while (motorConfigurator.apply(motorConfigs, 0.03) != StatusCode.OK) {
        motorErrors++;
      motorFailure = motorErrors > maxMotorFailures;
      if (motorFailure) {
        disableMotor(_motor);
        return false;
      }
    }
    return true;
  }   

  // Attempts to sets the motor to coast mode with 0 output. Used in the case of a motor failure.
  private void disableMotor(TalonFX motor) {
    TalonFXConfigurator motorConfigurator = motor.getConfigurator();
    TalonFXConfiguration motorConfigs = new TalonFXConfiguration();
    motorConfigurator.refresh(motorConfigs);
    motorConfigs.MotorOutput.NeutralMode = NeutralModeValue.Coast;
    motorConfigurator.apply(motorConfigs);

    motor.setControl(new DutyCycleOut(0));
  }
}