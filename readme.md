# Genesis project templating tool

The genesis project templating tool is an extremely powerful project templating tool, similar to Maven Archetypes but without all
it's limitations.

It can be used both standalone as a command line tool, or through maven (both as a project plugin in a pom, as well as standalone
like maven archetypes)

Note: The command line tool isn't currently working this will be fixed in a future release, so for now just use maven

## Usage

# Maven plugin (with pom)

This is generally used to create a "auto-configurable" maven project, which is basically a minimal maven pom which will
ask questions used to generate the project, which will then generate all the project files.

In order to use this method, create a project with a single pom.xml with the following content:

```xml
<?xml version="1.0" encoding="UTF-8"?>
<project xmlns="http://maven.apache.org/POM/4.0.0"
         xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
         xsi:schemaLocation="http://maven.apache.org/POM/4.0.0 http://maven.apache.org/xsd/maven-4.0.0.xsd">
    <modelVersion>4.0.0</modelVersion>

    <groupId>com.test</groupId>
    <artifactId>genesistest</artifactId>
    <version>1.0-SNAPSHOT</version>

    <build>
        <plugins>
            <plugin>
                <groupId>com.kloudtek.genesis</groupId>
                <artifactId>genesis-maven</artifactId>
                <version>1.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <id>create-project</id>
                        <configuration>
                            <template>simplemaven</template>
                            <target>.</target>
                        </configuration>
                        <phase>initialize</phase>
                        <goals>
                            <goal>template</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
</project>
```

In this case you'll notice we're using the 'simplemaven' template, but you could instead use any other template.

You then can just execute any maven command (the initialize will make it run automatically), for example:

`maven compile`

It will then request questions like the artifact id and group id, and then it will generate all the files and the pom.xml will be replaced with a new one.
