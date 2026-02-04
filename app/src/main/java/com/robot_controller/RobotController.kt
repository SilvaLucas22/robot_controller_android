package com.robot_controller

import com.robot_controller.autonomy.AutonomyHelper
import org.opencv.android.OpenCVLoader

class RobotController : android.app.Application() {
    override fun onCreate() {
        super.onCreate()
        OpenCVLoader.initLocal()
        AutonomyHelper.init(this)
    }
}
