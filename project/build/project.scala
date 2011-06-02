import sbt._

trait Defaults {
  def androidPlatformName = "android-2.1"
}

class MainProject(info: ProjectInfo) extends AndroidLibraryProject(info) with Defaults with TypedResources {
}

