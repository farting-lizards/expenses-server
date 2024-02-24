## Dev install

You'll need java 11 jdk installed, on fedora:

> sudo dnf install java-11-openjdk-devel

### Installing java 11

In case you have multiple versions of java installed, you will have to configure your
system to use java 11. The following java tools will have to be updated:

```bash
# find java tools that need to be configured to use version 11
$ sudo alternatives --list | grep java | grep manual
java_sdk_openjdk        manual  /usr/lib/jvm/java-11-openjdk-11.0.19.0.7-1.fc38.x86_64
jre_openjdk             manual  /usr/lib/jvm/java-11-openjdk-11.0.19.0.7-1.fc38.x86_64
java                    manual  /usr/lib/jvm/java-11-openjdk-11.0.19.0.7-1.fc38.x86_64/bin/java
javac                   manual  /usr/lib/jvm/java-11-openjdk-11.0.19.0.7-1.fc38.x86_64/bin/javac
```
This is how you can configure the above java tools to use version 11
```bash
$ for alternative in $(sudo alternatives --list | grep java | awk '{print $1}' | grep -v '\(17\|11\)'); do sudo alternatives --config "$alternative"; done
```

### Running the server locally

1. Start the database locally - Simply run the following script to start the server. The script also populates the database with some dummy data:
> Make sure you are in the root directory of this project to run the following command.
```bash
$ sudo ./scripts/start_devdb.sh populate
```

2. Start the server locally - You can either use the "Run" button of your IDE (such as intellij) to run the server application or run the following command:
```bash
./gradlew bootRun
```
