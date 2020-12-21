SCALA_DIR="${HOME}/.sbt/boot/scala-2.12.10"
SCALA_LIB="${SCALA_DIR}/lib"
JDI_TOOLS=/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar
SCALACHECK="${HOME}/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalacheck/scalacheck_2.12/1.14.1/scalacheck_2.12-1.14.1.jar"

TESTCASE="${@:-middle}"

export ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_OPTIONS="-Xmx256M -Xms32M -classpath ${SCALA_LIB}/jansi.jar:${SCALA_LIB}/jline.jar:${SCALA_LIB}/scala-compiler.jar:${SCALA_LIB}/scala-library.jar:${SCALA_LIB}/scala-reflect.jar:${SCALA_LIB}/scala-xml_2.12.jar:${SCALACHECK}:target/scala-2.12/classes:target/scala-2.12/test-classes -Dscala.home=${SCALA_DIR} -Dscala.usejavacp=true -Denv.emacs=emacs"

export ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_MAIN="scala.tools.nsc.MainGenericRunner PropertyTests -f ${TESTCASE}"

scala -cp "${JDI_TOOLS}" ../scalacheck-integration/target/scala-2.11/scalacheck-integration_2.11-0.1.0-SNAPSHOT.jar
