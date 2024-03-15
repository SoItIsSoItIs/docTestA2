package frc.robot;

import edu.wpi.first.math.MathUtil;
import edu.wpi.first.math.filter.SlewRateLimiter;
import edu.wpi.first.wpilibj.TimedRobot;
import edu.wpi.first.wpilibj.XboxController;
import edu.wpi.first.wpilibj.smartdashboard.SendableChooser;
import edu.wpi.first.wpilibj.smartdashboard.SmartDashboard;

public class Robot extends TimedRobot {
  private final XboxController driver = new XboxController(0); // Initializes the driver controller.
  private final XboxController operator = new XboxController(1); // Initializes the operator controller.

  // Limits the acceleration of controller inputs. 
  private final SlewRateLimiter xAccLimiter = new SlewRateLimiter(Drivetrain.maxAccTeleop/Drivetrain.maxVelTeleop);
  private final SlewRateLimiter yAccLimiter = new SlewRateLimiter(Drivetrain.maxAccTeleop/Drivetrain.maxVelTeleop);
  private final SlewRateLimiter angAccLimiter = new SlewRateLimiter(Drivetrain.maxAngularAccTeleop/Drivetrain.maxAngularVelTeleop);
  
  private double speedScaleFactor = 1.0; // Scales the speed of the robot that results from controller inputs. 1.0 corresponds to full speed. 0.0 is fully stopped. 

  // Initializes the different subsystems of the robot.
  private final Drivetrain swerve = new Drivetrain(); // Contains the Swerve Modules, Gyro, Path Follower, Target Tracking, Odometry, and Vision Calibration.
  private final Thrower thrower = new Thrower();
  private final Arm arm = new Arm();
  private final Climber climber = new Climber();

  // Auto Chooser Variables
  private final SendableChooser<String> autoChooser = new SendableChooser<>();
<<<<<<< Updated upstream
  private static final String auto1 = "Auto 1";
  private static final String auto2 = "Auto 2";
  private static final String auto3 = "Auto 3"; 
  private static final String auto4 = "Auto 4";
  private static final String auto5 = "Auto 5";  
=======
  private static final String auto1 = "1 Piece";
  private static final String auto2 = "2 Piece";
  private static final String auto3 = "3 Piece"; 
  private static final String auto4 = "Climber Reset";
  private static final String auto5 = "Auto 5";
  private static final String auto6 = "Auto 6";  
>>>>>>> Stashed changes
  private String autoSelected;
  private int autoStage = 1;

  public void robotInit() {
    // Allows the user to choose which auto to do
    autoChooser.setDefaultOption(auto1, auto1);
    autoChooser.addOption(auto2, auto2);
    autoChooser.addOption(auto3, auto3);
    autoChooser.addOption(auto4, auto4);
    autoChooser.addOption(auto5, auto5);
    SmartDashboard.putData("Autos", autoChooser);

    swerve.loadPath("moveBack", 0.0, 0.0, 0.0, 180.0); //0 Loads the path. All paths should be loaded in robotInit() because this call is computationally expensive.
    swerve.loadPath("moveBackGet", 0.0, 0.0, 0.0, 180.0); //1 Loads the path. All paths should be loaded in robotInit() because this call is computationally expensive.
    swerve.loadPath("moveBackGet2", 0.0, 0.0, 0.0, 180.0);//2
    swerve.loadPath("noteRushP1", 0.0, 0.0, 0.0, 180.0);//3
    swerve.loadPath("noteRushP2", 0.0, 0.0, 0.0, 180.0);//4
    swerve.loadPath("noteRushP3", 0.0, 0.0, 0.0, 180.0);//5
    createToggles();

    // Helps prevent loop overruns when the robot is first enabled. These calls cause the robot to initialize code in other parts of the program so it does not need to be initialized during autonomousInit() or teleopInit(), saving computational resources.
    swerve.resetPathController(0);
    swerve.followPath(0);
    swerve.atPathEndpoint(0);
    swerve.drive(0.1, 0.0, 0.0, false, 0.0, 0.0);
    swerve.resetOdometry(0, 0, 0);
    swerve.updateDash();
  }

  public void robotPeriodic() {
    updateVision(); // Checks to see ifs there are reliable April Tags in sight of the Limelight and updates the robot position on the field.
    getAim(); // Calculates the required robot heading and arm angle to make a shot from the current robot position.
    swerve.updateDash(); // Pushes drivetrain information to the Dashboard.
    swerve.updateOdometry(); // Keeps track of the position of the robot on the field. Must be called each period.
    updateToggles();

    // Re-zeros the angle reading of the gyro to the current angle of the robot. Should be called if the gyroscope readings are no longer well correlated with the field.
    if (driver.getRawButtonPressed(8)) {
      swerve.resetGyro();
    }
  }
  
  public void autonomousInit() {
    swerve.pushCalibration(); // Updates the robot's position on the field.
    thrower.init(); // Must be called during autoInit() and teleopInit() for the thrower to work properly.
    autoStage = 1;
    autoSelected = autoChooser.getSelected();
    switch (autoSelected) {
      case auto1:
        // AutoInit 1 code goes here.
        swerve.resetAimDriveController(aimHeading);
        break;

      case auto2:
        // AutoInit 2 code goes here.
        break;

      case auto3: 
        // AutoInit 3 code goes here.
        break;

      case auto4:
        // AutoInit 4 code goes here.
        break;

      case auto5:
        // AutoInit 5 code goes here.
        break;
    }
  }

  public void autonomousPeriodic() {
    thrower.periodic();
    arm.periodic();
    climber.periodic();
    switch (autoSelected) {
      case auto1:
        switch (autoStage) {
          case 1: 
            // Auto 1 code goes here.
<<<<<<< Updated upstream
            swerve.aimDrive(0.0, 0.0, aimHeading, true);
            arm.updateSetpoint(aimArmAngle);
=======
            swerve.driveTo(1.91, swerve.isBlueAlliance() ? 5.48 : Drivetrain.fieldWidth - 5.48, getAimHeading());
            arm.updateSetpoint(getAimArmAngle());
>>>>>>> Stashed changes

            if (swerve.atAimTarget() && arm.atSetpoint()) {
              thrower.commandThrow(30.0);
            }

            if (!thrower.isThrowing()) { // Condition to move to the next stage. The code in the if statement will execute once (like an autoStageInit()), then move on to the next stage.
              swerve.resetPathController(0);
              autoStage = 2;
            }
            break;

          default: 
            swerve.drive(0.0, 0.0, 0.0, false, 0.0, 0.0); // Stops the robot after auto is completed.
            break;
        }
        break;
        // Auto 2 code goes here.
      case auto2:
      switch (autoStage) {
        case 1: 
        swerve.aimDrive(0.0, 0.0, aimHeading, true);
            arm.updateSetpoint(aimArmAngle);

            if (swerve.atAimTarget() && arm.atSetpoint()) {
              thrower.commandThrow(30.0);
            }

            if (!thrower.isThrowing()) { 
              autoStage = 2;
            }
        break;
        case 2:
          if (!swerve.atPathEndpoint(0)) {
            swerve.resetPathController(0);
            swerve.followPath(0);
          } else {
            autoStage = 3;
          }
          break;

        default:
        swerve.drive(0.0, 0.0, 0.0, false, 0.0, 0.0);
        break;

      }
        break;
      case auto3: // Auto 3 code goes here.
        switch (autoStage) {
        case 1: 
        swerve.aimDrive(0.0, 0.0, aimHeading, true);
            arm.updateSetpoint(aimArmAngle);

            if (swerve.atAimTarget() && arm.atSetpoint()) {
              thrower.commandThrow(30.0);
            }

            if (!thrower.isThrowing()) {
              swerve.resetPathController(2);
              autoStage = 2;
            }
        break;
        case 2:
          if (!swerve.atPathEndpoint(2)) {
            swerve.followPath(2);
          } else {
            autoStage = 3;
          }
          break;

        default:
        swerve.drive(0.0, 0.0, 0.0, false, 0.0, 0.0);
        break;
      }    
      case auto4: 
      switch (autoStage) { // Auto 4 code goes here.
        case 1: 
        swerve.aimDrive(0.0, 0.0, aimHeading, true);
            arm.updateSetpoint(aimArmAngle);

            if (swerve.atAimTarget() && arm.atSetpoint()) {
              thrower.commandThrow(30.0);
            }

            if (!thrower.isThrowing()) { 
              swerve.resetPathController(1);
              autoStage = 2;
            }
        break;
        case 2:
          if (!swerve.atPathEndpoint(1)) {
            swerve.followPath(1);
          } else {
            autoStage = 3;
          }
          break;

        default:
        swerve.drive(0.0, 0.0, 0.0, false, 0.0, 0.0);
        break;
      }

      case auto5:
<<<<<<< Updated upstream
      switch (autoStage) { // Auto 5 code goes here.
        case 1: 
        swerve.aimDrive(0.0, 0.0, aimHeading, true);
            arm.updateSetpoint(aimArmAngle);
=======
        // Auto 5 code goes here.
        switch (autoStage) {
          case 1:
            swerve.driveTo(2.0, (swerve.isBlueAlliance() ? 7.5 : Drivetrain.fieldWidth - 7.5), (swerve.isBlueAlliance() ? -90.0 : 90.0));
            arm.updateSetpoint(armAmpSetpoint);
            thrower.setDisableFlywheel(true);
>>>>>>> Stashed changes

            if (swerve.atAimTarget() && arm.atSetpoint()) {
              thrower.commandThrow(30.0);
            }
<<<<<<< Updated upstream
=======
            break;
          
          case 2:
            swerve.driveTo(3.5, (swerve.isBlueAlliance() ? 7.5 : Drivetrain.fieldWidth - 7.5), 0.0);
            arm.updateSetpoint(armDriveSetpoint);
            if (swerve.atDriveGoal()) {
              swerve.resetDriveController(0.0);
              autoStage = -1;
            }
            break;
>>>>>>> Stashed changes

            if (!thrower.isThrowing()) { 
              swerve.resetPathController(3);
              autoStage = 2;
            }
        break;
        case 2:
          if (!swerve.atPathEndpoint(3)) {
            swerve.followPath(3);
          } else {
            autoStage = 3;
          }
          break;
        case 3:



        if (thrower.getSensor1()) {
          autoStage = 4;
        }

         break;
         case 4:





         break;
         case 5:





        break;

        default:
        swerve.drive(0.0, 0.0, 0.0, false, 0.0, 0.0);
        break;
      }
    }

    
  }

  public void teleopInit() {
    swerve.pushCalibration(); // Updates the robot's position on the field.
    thrower.init(); // Must be called during autoInit() and teleopInit() for the thrower to work properly.
<<<<<<< Updated upstream
=======
    climber.init();
    arm.init();
    rumbleTimer.restart();
    noteFiredTimer.restart();

    
>>>>>>> Stashed changes
  }

  public void teleopPeriodic() {
    if (driver.getRawButtonPressed(4)) { // Y Button
      speedScaleFactor = 0.15;
    }
    if (driver.getRawButtonPressed(2)) { // B button
      speedScaleFactor = 0.6;
    }
    if (driver.getRawButtonPressed(1)) { // A button
      speedScaleFactor = 1.0;
    }

    // Applies a deadband to controller inputs. Also limits the acceleration of controller inputs.
    double xVel = xAccLimiter.calculate(MathUtil.applyDeadband(-driver.getLeftY(), 0.05)*speedScaleFactor)*Drivetrain.maxVelTeleop;
    double yVel = yAccLimiter.calculate(MathUtil.applyDeadband(-driver.getLeftX(), 0.05)*speedScaleFactor)*Drivetrain.maxVelTeleop;
    double angVel = angAccLimiter.calculate(MathUtil.applyDeadband(-driver.getRightX(), 0.05)*speedScaleFactor)*Drivetrain.maxAngularVelTeleop;

    // Auto Rotate to Aim Heading
    if (driver.getRawButtonPressed(6)) { // Right Bumper
      swerve.resetAimDriveController(aimHeading);
    }
    if (driver.getRawButton(6)) { // Right Bumper
<<<<<<< Updated upstream
      swerve.aimDrive(xVel, yVel, aimHeading, true);
    } else {
=======
      swerve.driveTo(1.89, (swerve.isBlueAlliance() ? 5.56 : Drivetrain.fieldWidth - 5.56), getAimHeading()); // Snap to speaker.
    } else if (driver.getRawButton(5)) { // Left Bumper
      swerve.driveTo(1.8, (swerve.isBlueAlliance() ? 7.33 : Drivetrain.fieldWidth - 7.33), swerve.isBlueAlliance() ? -90 : 90); // Snap to amp.
    }
    else {
>>>>>>> Stashed changes
      swerve.drive(xVel, yVel, angVel, true, 0.0, 0.0); // Drives the robot at a certain speed and rotation rate. Units: meters per second for xVel and yVel, radians per second for angVel.
    }

    // The following 3 calls allow the user to calibrate the position of the robot based on April Tag information. Should be called when the robot is stationary.
    if (driver.getRawButtonPressed(7)) {
      swerve.resetCalibration(); // Begins calculating the position of the robot on the field based on visible April Tags.
    }
    if (driver.getRawButton(7)) {
      swerve.addCalibrationEstimate(); // Collects additional data to calculate the position of the robot on the field based on visible April Tags.
    }
    if (driver.getRawButtonReleased(7)) {
      swerve.pushCalibration(); // Updates the position of the robot on the field based on previous calculations.
    }

    thrower.periodic(); // Should be called in teleopPeriodic() and autoPeriodic(). Handles the internal logic of the thrower.
    if (thrower.getManualControl()) {
      thrower.setManualSpeeds(0.0, 0.0); // TODO: Change the inputs to this function to their appropriate keybinds.
    } else {
      if (operator.getRawButton(1)) { // A Button
        thrower.commandThrow(30.0); // Commands the thrower to throw a note with a flywheel velocity of 30 rotations per second.
      }
    }

<<<<<<< Updated upstream
    arm.periodic(); // Should be called in teleopPeriodic() and autoPeriodic(). Handles the internal logic of the arm.
    if (arm.getManualControl()) {
      arm.setManualPower(0.0); // TODO: Change the inputs to this function to their appropriate keybinds.
    } else {
      arm.updateSetpoint(aimArmAngle); // Changes the setpoint of the arm to the calculated arm angle needed to make a shot.
=======
    if (operator.getRawButtonPressed(7) && arm.getArmEncoderLeft() < 10.0) { // Mode Button
      climber.disableLockout();
>>>>>>> Stashed changes
    }

    climber.periodic();
    climber.set(MathUtil.applyDeadband(-operator.getLeftY(), 0.1), MathUtil.applyDeadband(-operator.getRightY(), 0.1));
  }
  
  public void disabledInit() {
    swerve.resetCalibration(); // Begins calculating the position of the robot on the field based on visible April Tags.
  }
  
  public void disabledPeriodic() {
    swerve.addCalibrationEstimate(); // Collects additional data to calculate the position of the robot on the field based on visible April Tags.
  }

  // Sends April Tag data to the drivetrain to update the position of the robot on the field. Filters data based on the number of tags visible and their size.
  public void updateVision() {
    boolean isSquare = isSquare();
    SmartDashboard.putBoolean("isSquare", isSquare);
    double[] presentDistanceArray = LimelightHelpers.getLimelightNTTableEntry("limelight", "botpose_targetspace").getDoubleArray(new double[6]);
    double presentDistance = -presentDistanceArray[2];
    SmartDashboard.putNumber("Distance to Tag", presentDistance);
    double ta = LimelightHelpers.getTA("");
    double tid = LimelightHelpers.getFiducialID("");
    if (!isSquare && ta > 1.5 && ((tid == 8 && swerve.isBlueAlliance()) || (tid == 4 && swerve.isRedAlliance()))) {
      swerve.addVisionEstimate(0.04, 0.04);
    }
  }
  
  // Determines whether a Limelight target is square. Useful for identifying whether multiple April Tages are detected.
  public boolean isSquare() {
    double thor = LimelightHelpers.getLimelightNTTableEntry("limelight", "thor").getDouble(0);
    double tvert = LimelightHelpers.getLimelightNTTableEntry("limelight", "tvert").getDouble(0);
    if (Math.abs(tvert/thor-1.0) < 0.2) {
      return true;
    } else {
      return false;
    }
  }

  // This function calculates the required robot heading and arm angle to make a shot into the speaker from the current robot position on the field.
  double headingTol = 0.5; // The acceptable error in the heading of the robot when tracking headings calculated by the getAim() function.
  boolean aimShotAvailable = false; // Whether it is possible to make it into the speaker from the current robot position.
  double aimHeading = 0.0; // The robot heading that is required to make the shot.
  double aimArmAngle = 0.0; // The arm angle that is required to make the shot.
  public void getAim() {
    double robotX = swerve.getXPos(); // x-coordinate of the center of rotation of the robot in meters.
    double robotY = swerve.getYPos();  // y-coordinate of the center of rotation of the robot in meters.
    double speakerZ = 2.045; // The height of the center of the speaker slot above the field carpet in meters. 
    double speakerY = swerve.isBlueAlliance() ? 5.548 : Drivetrain.fieldWidth - 5.548; // The y-coordinate of the center of the speaker slot in meters, adjusted for alliance. 
    double armL = 0.6; // The length of the arm between the pivot and the point where the note loses contact with the flywheel in meters.
    double armPivotZ = 0.1; // The height of the arm pivot above the field carpet in meters.
    double armPivotX = 0.2; // The distance from the center of rotation of the robot to the arm pivot in meters. A pivot behind the center of rotation is positive.
    double g = 9.81; // The gravitational acceleration in meters per second squared.
    double noteVel = 8.0; // The velocity of the note as it leaves the thrower in meters per second.
    double minAngle = 10.0; // The lowest angle the arm can expect to shoot at in degrees. 0 degrees is paralell to the floor and 90 degrees is pointing straight up.
    double maxAngle = 80.0; // The highest angle the arm can expect to shoot at in degrees. 0 degrees is paralell to the floor and 90 degrees is pointing straight up.
    int totalAngles = 70; // The total number of angles that should be checked. A higher number takes more computational resources, but is more accurate.
    double[] noteZErrors = new double[totalAngles]; // An array that stores the z-level that the note will impact the speaker at relative to the center of the speaker slot in meters. This represents the overshoot or undershoot error of each angle tested.

    // Calculates the position of the note just as it leaves the thrower. This calculation relies on the previous loops solution to approximate the heading and arm angle. If no shot was available, it will default to using middle of the range values for the heading and arm angle.
    if (!aimShotAvailable) {
      aimArmAngle = 50.0;
      aimHeading = 180.0;
    } 
    double lastNoteX = robotX - armPivotX*Math.cos(aimHeading*Math.PI/180.0) + armL*Math.cos(aimArmAngle*Math.PI/180.0)*Math.cos(aimHeading*Math.PI/180.0); // The trig accounts for the effect of the arm angle and robot heading on the note's position.
    double lastNoteY = robotY + armPivotX*Math.sin(aimHeading*Math.PI/180.0) + armL*Math.cos(aimArmAngle*Math.PI/180.0)*Math.sin(aimHeading*Math.PI/180.0);

    // Calculates the angle the robot should be facing to make the shot in degrees. Based on the position of the note as it leaves the shooter.
    double nextAimHeading = 180.0; // The angle the robot should be facing to make the shot in degrees.
    if (lastNoteY == speakerY) { // The robot is aligned with the speaker in the y-dimension. This prevents calls to atan() which would result in undefined returns.
        nextAimHeading = 180.0;
    } else if (lastNoteY < speakerY) {
        nextAimHeading = Math.atan(lastNoteX/(speakerY-lastNoteY))*180.0/Math.PI + 90.0; // The robot has a positive heading.
    } else {
        nextAimHeading = Math.atan(lastNoteX/(speakerY-lastNoteY))*180.0/Math.PI - 90.0; // The robot has a negative heading. 
    }
    
    // Calculates the Z-error for several angles to see which angle is the best.
    for (int index = 0; index < totalAngles; index++) {
      double currentAngle = minAngle + index*(maxAngle-minAngle)/totalAngles; // Converts from the array index to degrees.
      double noteX = robotX - armPivotX*Math.cos(nextAimHeading*Math.PI/180.0) + armL*Math.cos(currentAngle*Math.PI/180.0)*Math.cos(nextAimHeading*Math.PI/180.0); // The x-coordinate of the note as it leaves contact with the thrower in meters. The trig accounts for the effect of the arm angle and robot heading on the note's position.
      double noteY = robotY + armPivotX*Math.sin(nextAimHeading*Math.PI/180.0) + armL*Math.cos(currentAngle*Math.PI/180.0)*Math.sin(nextAimHeading*Math.PI/180.0); // The y-coordinate of the note as it leaves contact with the thrower in meters. The trig accounts for the effect of the arm angle and robot heading on the note's position.
      double noteZ = armL*Math.sin(currentAngle*Math.PI/180.0) + armPivotZ; // The height of the note above the carpet just as it loses contact with the thrower.
      double noteR = Math.sqrt(Math.pow(noteX, 2) + Math.pow(noteY-speakerY, 2)); // The distance between the note's initial position and the speaker slot center.
      double noteRVel = noteVel*Math.cos(currentAngle*Math.PI/180.0); // The radial velocity of the note, as if the speaker slot center was the origin of a polar coordinate system.
      double noteTime = noteR/noteRVel; // The airtime of the note before it impacts the speaker.
      double noteFinalZ =  noteZ + noteVel*Math.sin(currentAngle*Math.PI/180.0)*noteTime - g*Math.pow(noteTime, 2)/2.0; // The kinematics calculated z-position of the note as it impacts the speaker, accounting for intial z velocity and gravity.
      noteZErrors[index] = noteFinalZ - speakerZ; // The error (either undershoot or overshoot) of this arm angle in the z-dimension.
    }
    
    // Looks through the calcuated z-errors to find the correct arm angle to throw the note.
    boolean nextAimShotAvailable = false; // Stores whether it is physically possible to shoot the note into the speaker from the robot's current position.
    double nextAimArmAngle = 50.0; // Stores the optimal arm angle to make the shot, or -1 if it is impossible to make the shot.
    for (int index = 0; index < totalAngles-1; index++) {
      if (noteZErrors[index] < 0 && noteZErrors[index+1] > 0) { // There can be two solutions. To identify the correct solution, as the angle increases the Z-error should transition from - to +. The other solution will transition from + to -. If this condition is not met, a shot cannot be made from the robot's current position.
        double negativeAngleZError = -noteZErrors[index];
        double positiveAngleZError = noteZErrors[index+1];
        nextAimArmAngle = minAngle + (index + negativeAngleZError/(positiveAngleZError + negativeAngleZError))*(maxAngle-minAngle)/totalAngles; // Linear interpolation to approximate the arm angle that results in 0 z-error. 
        nextAimShotAvailable = true;
      }
    }

    // Stores this periods solution for the next period to iterate on.
    aimShotAvailable = nextAimShotAvailable;
    aimHeading = nextAimHeading;
    aimArmAngle = nextAimArmAngle;

    // Publishes solution to the dashboard.
    SmartDashboard.putNumber("aim robot angle", nextAimHeading);
    SmartDashboard.putBoolean("aim shotAvailable", nextAimShotAvailable);
    SmartDashboard.putNumber("aim arm angle", nextAimArmAngle);
  }

  // Initializes toggle booleans to the dashboard.
  boolean moduleToggleFR = false;
  boolean moduleToggleFL = false;
  boolean moduleToggleBL = false;
  boolean moduleToggleBR = false;
  boolean gyroToggle = false;
  boolean visionToggle = false;
  boolean throwerManual = false;
  boolean throwerReboot = false;
  boolean armManual = false;
  boolean armReboot = false;
  boolean climberReboot = false;
  public void createToggles() {
    SmartDashboard.putBoolean("FR Module Toggle", moduleToggleFR);
    SmartDashboard.putBoolean("FL Module Toggle", moduleToggleFL);
    SmartDashboard.putBoolean("BL Module Toggle", moduleToggleBL);
    SmartDashboard.putBoolean("BR Module Toggle", moduleToggleBR);
    SmartDashboard.putBoolean("Gyro Toggle", gyroToggle);
    SmartDashboard.putBoolean("Vision Toggle", visionToggle);
    SmartDashboard.putBoolean("Thrower Manual Toggle", throwerManual);
    SmartDashboard.putBoolean("Thrower Reboot", throwerReboot);
    SmartDashboard.putBoolean("Arm Manual Toggle", armManual);
    SmartDashboard.putBoolean("Arm Reboot", armReboot);
    SmartDashboard.putBoolean("Climber Reboot", climberReboot);
  }

  // Reads toggles from dashboard and executes the corresponding code.
  public void updateToggles() {
    boolean currModuleToggleFR = SmartDashboard.getBoolean("FR Module Toggle", false);
    if (currModuleToggleFR ^ moduleToggleFR) {
      swerve.toggleFR();
    }
    moduleToggleFR = currModuleToggleFR;

    boolean currModuleToggleFL = SmartDashboard.getBoolean("FL Module Toggle", false);
    if (currModuleToggleFL ^ moduleToggleFL) {
      swerve.toggleFL();
    }
    moduleToggleFL = currModuleToggleFL;

    boolean currModuleToggleBL = SmartDashboard.getBoolean("BL Module Toggle", false);
    if (currModuleToggleBL ^ moduleToggleBL) {
      swerve.toggleBL();
    }
    moduleToggleBL = currModuleToggleBL;

    boolean currModuleToggleBR = SmartDashboard.getBoolean("BR Module Toggle", false);
    if (currModuleToggleBR ^ moduleToggleBR) {
      swerve.toggleBR();
    }
    moduleToggleBR = currModuleToggleBR;

    boolean currGyroToggle = SmartDashboard.getBoolean("Gyro Toggle", false);
    if (currGyroToggle ^ gyroToggle) {
      swerve.toggleGyro();
    }
    gyroToggle = currGyroToggle;

    boolean currVisionToggle = SmartDashboard.getBoolean("Vision Toggle", false);
    if (currVisionToggle ^ visionToggle) {
      swerve.toggleVision();
    }
    visionToggle = currVisionToggle;

    boolean currThrowerManual = SmartDashboard.getBoolean("Thrower Manual Toggle", false);
    if (currThrowerManual ^ throwerManual) {
      thrower.toggleManualControl();
    }
    throwerManual = currThrowerManual;

    boolean currThrowerReboot = SmartDashboard.getBoolean("Thrower Reboot", false);
    if (currThrowerReboot ^ throwerReboot) {
      thrower.reboot();
    }
    throwerReboot = currThrowerReboot;

    boolean currArmManual = SmartDashboard.getBoolean("Arm Manual Toggle", false);
    if (currArmManual ^ armManual) {
      arm.toggleManualControl();
    }
    armManual = currArmManual;

    boolean currArmReboot = SmartDashboard.getBoolean("Arm Reboot", false);
    if (currArmReboot ^ armReboot) {
      arm.reboot();
    }
    armReboot = currArmReboot;

    boolean currClimberReboot = SmartDashboard.getBoolean("Climber Reboot", false);
    if (currClimberReboot ^ climberReboot) {
      climber.reboot();
    }
    climberReboot = currClimberReboot;
  }
}