TESTCASE="${@:-middle}"

SCALA_DIR=/usr/share/scala-2.11
JDI_TOOLS=/usr/lib/jvm/java-8-openjdk-amd64/lib/tools.jar
SCALACHECK="${HOME}/.cache/coursier/v1/https/repo1.maven.org/maven2/org/scalacheck/scalacheck_2.11/1.14.1/scalacheck_2.11-1.14.1.jar"

export ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_OPTIONS="-Xmx256M -Xms32M -classpath ${SCALA_DIR}/lib/hawtjni-runtime.jar:${SCALA_DIR}/lib/jansi.jar:${SCALA_DIR}/lib/jline.jar:${SCALA_DIR}/lib/scala-actors.jar:${SCALA_DIR}/lib/scala-compiler.jar:${SCALA_DIR}/lib/scala-library.jar:${SCALA_DIR}/lib/scala-parser-combinators.jar:${SCALA_DIR}/lib/scala-reflect.jar:${SCALA_DIR}/lib/scala-xml.jar:${SCALA_DIR}/lib/scalap.jar:${SCALACHECK}:../scalacheck-integration/target/scala-2.11/scalacheck-integration_2.11-0.1.0-SNAPSHOT.jar:target/scala-2.11/classes/:target/scala-2.11/test-classes/ -Dscala.home=${SCALA_DIR} -Dscala.usejavacp=true -Denv.emacs="

export ENTOMOPHAGOUS_STATISTICAL_DEBUGGER_MAIN="scala.tools.nsc.MainGenericRunner PropertyTests -f ${TESTCASE}"

scala -cp "${JDI_TOOLS}" ../scalacheck-integration/target/scala-2.11/scalacheck-integration_2.11-0.1.0-SNAPSHOT.jar
