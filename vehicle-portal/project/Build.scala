import sbt._
import Keys._
import play.Project._
import com.typesafe.sbt.SbtNativePackager._
import com.typesafe.sbt.packager.Keys._
import com.typesafe.sbt.packager.linux.LinuxPackageMapping
import com.typesafe.sbt.packager.linux.LinuxFileMetaData
import com.typesafe.sbt.packager.linux.LinuxSymlink

object Build extends Build {

  val frontendTest = taskKey[Unit]("Runs frontend toolkit to test the javascripts")

  val frontendTestTask = frontendTest := {
    val logger = streams.value.log
    Process("grunt test", new java.io.File(baseDirectory.value + "/public/frontend")) ! logger match {
      case 0 => // Success!
      case n => sys.error(s"Could not restart the project, exit code: $n")
    }
  }

  val appName = "vehicle-portal"
  val appVersion = "1.0-SNAPSHOT"

  val appDependencies = Seq(
    "org.apache.commons" % "commons-lang3" % "3.3.2",
    "joda-time" % "joda-time" % "2.2",
    "org.joda" % "joda-convert" % "1.3.1",
    "javax.validation" % "validation-api" % "1.0.0.GA",
    "net.logstash.logback" % "logstash-logback-encoder" % "2.0",
    "ch.qos.logback.contrib" % "logback-json-classic" % "0.1.2",
    "ch.qos.logback.contrib" % "logback-jackson" % "0.1.2",
    "com.yammer.metrics" % "metrics-core" % "2.2.0",
    "com.yammer.metrics" % "metrics-graphite" % "2.2.0",
    "nl.grons" % "metrics-scala_2.10" % "2.2.0",
    "com.github.sdb" %% "play2-metrics" % "0.1.0",
    "iep-vvr" % "domain" % "1.0-SNAPSHOT",
    "uk.gov.dvla.iep" % "govuk-web" % "1.0-SNAPSHOT" changing,
    "org.webjars" %% "webjars-play" % "2.2.1-2",
    "org.webjars" % "angularjs" % "1.3.0-beta.8",
    "org.scalatest" % "scalatest_2.10" % "2.1.5" % "test",
    "org.mockito" % "mockito-core" % "1.9.5" % "test",
    "org.scala-lang" % "scala-compiler" % "2.10.4" % "test",
    "com.novocode" % "junit-interface" % "0.10" % "test",
    "org.scalatestplus" % "play_2.10" % "1.0.1" % "test"
  )

  val rpm = rpmPlaySettings(sys.env.get("SERVICE_PORTAL_USERNAME").getOrElse("customer_portal"),
    sys.env.get("SERVICE_PORTAL_USERID").getOrElse("5000"), sys.env.get("SERVICE_PORTAL_GROUPNAME").getOrElse("customer_portal"),
    sys.env.get("BUILD_NUMBER").getOrElse("SNAPSHOT"))

  lazy val main = play.Project(appName, appVersion, appDependencies).settings(
    resolvers += Resolver.url("sbt-plugin-snapshots", new URL("http://repo.scala-sbt.org/scalasbt/sbt-plugin-snapshots/"))(Resolver.ivyStylePatterns),
    resolvers += "Coda Hale repo" at "http://repo.codahale.com/",
    resolvers += "Typesafe Releases" at "http://repo.typesafe.com/typesafe/releases",
    resolvers += "releases" at "http://oss.sonatype.org/content/repositories/releases",
    resolvers += "twitter-repo" at "http://maven.twttr.com",
    resolvers += "Morphia repository" at "http://morphia.googlecode.com/svn/mavenrepo",
    resolvers += "webjars" at "http://webjars.github.com/m2",
    resolvers += "Kainos Maven Repo" at "https://ci-iep-vehicles.kainos.com/nexus/content/repositories/snapshots",
    resolvers += "Local Maven Repo" at "file://" + Path.userHome.absolutePath + "/.m2/repository")
    .settings(rpm.flatten: _*)
    .settings(frontendTestTask)
    .settings(scalacOptions ++= Seq("-feature", "-deprecation"))
  //disabled until NODEJS will be installed on the CI
  //    .settings((test in Test) <<= (test in Test) dependsOn (frontendTest))

  def rpmPlaySettings(username: String, userId: String, groupName: String, release: String): Seq[Setting[_]] =
    Seq(version := "1.0",
      rpmGroup in Rpm := Some("Propriatary"),
      rpmVendor in Rpm := "Kainos",
      packageSummary in Rpm := "IEP Vehicles Portal",
      packageDescription in Rpm := "IEP Vehicles Portal",
      rpmRelease := release,
      rpmLicense in Rpm := Some("None"),
      rpmPre := Some(
        """restart_file=%{_localstatedir}/tmp/%{name}-restart
          |if [ $1 -gt 1 ] ; then
          |test -f $restart_file && rm $restart_file
          |service %{name} status >/dev/null
          |if [ $? -eq 0 ]; then
          |touch $restart_file
          |service %{name} stop >/dev/null
          |fi
          |else
          | """.stripMargin + String.format("/usr/bin/getent passwd %s >/dev/null || /usr/sbin/useradd -d /opt/%%{name} -u %s -s /sbin/nologin %s", username, userId, username) +
          """
            |fi""".stripMargin),
      rpmPost := Some(
        """restart_file=%{_localstatedir}/tmp/%{name}-restart
          |if [ $1 -gt 1 ]; then
          |if [ -f $restart_file ]; then
          |service %{name} start >/dev/null
          |rm $restart_file
          |fi
          |else
          |/sbin/chkconfig --add %{name}
          |fi
          |
        """.stripMargin),
      rpmRequirements := Seq("java-1.7.0-openjdk", "daemonize"),
      linuxPackageMappings in Rpm := Seq(
        LinuxPackageMapping(Array(IO.temporaryDirectory -> s"/var/log/${name.value}"), new LinuxFileMetaData(username, groupName, "0750", "false", false)),
        LinuxPackageMapping(Array(new File("../batch/demonize-play-classic-script") -> s"/etc/init.d/${name.value}")),
        LinuxPackageMapping(Array(new File("target/universal/stage/bin") -> s"/opt/${name.value}/bin")).withContents(),
        LinuxPackageMapping(Array(IO.temporaryDirectory -> s"/opt/${name.value}/bin/logs"), new LinuxFileMetaData(username, groupName, "0750", "false", false)),
        LinuxPackageMapping(Array(new File("target/universal/stage/conf") -> s"/etc/${name.value}"), new LinuxFileMetaData(username, groupName, "0770", "true", false)).withContents().withConfig("noreplace"),
        LinuxPackageMapping(Array(createLogbackXML("target/universal/stage/conf/logger.xml", s"/var/log/${name.value}", name.value) -> s"/etc/${name.value}/logger.xml"), new LinuxFileMetaData(username, groupName, "0770", "true", false)),
        LinuxPackageMapping(Array(new File("target/universal/stage/lib") -> s"/opt/${name.value}/lib"), new LinuxFileMetaData("root", "root", "0755", "false", false)).withContents(),
        LinuxPackageMapping(Array(IO.temporaryDirectory -> s"/opt/${name.value}"), new LinuxFileMetaData(username, groupName, "0750", "false", false)),
        LinuxPackageMapping(Array(
          {
            val file = scala.tools.nsc.io.File("sysconfigfile")
            file.writeAll(String.format("USER=%s", username))
            file.jfile
          } -> String.format("/etc/sysconfig/%s", name.value)
        ), new LinuxFileMetaData("root", "root", "0644", "true", false))),
      linuxPackageSymlinks in Rpm := Seq(
        LinuxSymlink(String.format("/opt/%s/conf", name.value), String.format("/etc/%s", name.value))
      ))

  private def createFile(name: String, content: String) = {
    val file = scala.tools.nsc.io.File(name)
    file.writeAll(content)
    file.jfile
  }

  private def createLogbackXML(name: String, logHome: String, logName: String) = {
    val xml = scala.io.Source.fromFile("../batch/genericLogback.xml").mkString
    val q = "\""

    createFile(name,
      xml
        .replace(s"<property name=${q}LOG_HOME${q}/>", s"<property name=${q}LOG_HOME${q} value=${q}$logHome${q}/>")
        .replace(s"<property name=${q}LOG_NAME${q}/>", s"<property name=${q}LOG_NAME${q} value=${q}$logName${q}/>"))
  }

}