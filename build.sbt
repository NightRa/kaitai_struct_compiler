import com.typesafe.sbt.packager.linux.LinuxSymlink
import sbt.Keys._

resolvers += Resolver.sonatypeRepo("public")

lazy val root = project.in(file(".")).
  aggregate(compilerJS, compilerJVM).
  settings(
    publish := {},
    publishLocal := {}
  )

lazy val compiler = crossProject.in(file(".")).
  enablePlugins(BuildInfoPlugin).
  enablePlugins(JavaAppPackaging).
  settings(
    name := "kaitai-struct-compiler",
    version := "0.2",
    licenses := Seq(("GPL-3.0", url("https://opensource.org/licenses/GPL-3.0"))),
    scalaVersion := "2.11.7",
    buildInfoKeys := Seq[BuildInfoKey](name, version, scalaVersion, sbtVersion),
    buildInfoPackage := "io.kaitai.struct",
    libraryDependencies ++= Seq(
      "com.lihaoyi" %%% "fastparse" % "0.3.7",
      "org.yaml" % "snakeyaml" % "1.16",
      "com.fasterxml.jackson.core" % "jackson-core" % "2.1.1",
      "com.fasterxml.jackson.core" % "jackson-annotations" % "2.1.1",
      "com.fasterxml.jackson.core" % "jackson-databind" % "2.1.1",
      "com.fasterxml.jackson.dataformat" % "jackson-dataformat-yaml" % "2.1.1"
    )
  ).
  jvmSettings(
    mainClass in Compile := Some("io.kaitai.struct.Main"),
    libraryDependencies ++= Seq(
      "org.scalatest" %% "scalatest" % "2.2.6" % "test",
      "com.github.scopt" %% "scopt" % "3.4.0"
    ),

    // Create symlink to allow calling compiler quickly as "ksc"
    linuxPackageSymlinks += LinuxSymlink("/usr/bin/ksc", s"/usr/bin/${name.value}"),

    // Remove all "maintainer scripts", such as prerm/postrm/preinst/postinst: default
    // implementations create per-package virtual user that we won't use anyway
    maintainerScripts in Debian := Map(),

    packageSummary in Linux := "compiler to generate binary data parsers in Java / JavaScript / Python / Ruby",
    packageSummary in Windows := "Kaitai Struct compiler",
    packageDescription in Linux :=
    """This is the reference implementation of a compiler for Kaitai Struct (.ksy)
    files. It allows to compile them into source code in Java / JavaScript /
    Python / Ruby.
    .ksy files describe binary data structures in declarative YAML-based
    language (in contrast to imperative parsing implementation written in a
    single programming language) and allow cross-language, cross-platform data
    formats description.""",
    packageDescription in Windows := "Compiler to translate Kaitai Struct (.ksy) files into Java / JavaScript / Python / Ruby source code",

    wixProductLicense := Some(new File("shared/src/windows/License.rtf")),

    maintainer in Windows := "Kaitai Project",
    maintainer in Debian := "Mikhail Yakshin <greycat@kaitai.io>"
  ).
  jsSettings(
    // Add JS-specific settings here
  )

lazy val compilerJVM = compiler.jvm
lazy val compilerJS = compiler.js
