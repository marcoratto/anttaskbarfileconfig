# Download #

  1. Open the browser on your desktop and go to the [Downloads](http://code.google.com/p/anttaskbarfileconfig/downloads/list) area
  1. Download the binary archive (.zip for Windows, .tar.gz for `*`nix or Mac]

# Install #

  1. Extract the .zip file (or .tar.gz file) on your desktop

# Configuration #

  1. Configure your _build.xml_

Define a classpath (`AntTaskBarFileConfig.jar`):
```
	<path id="AntTaskSapAdapter.classpath">
		<fileset dir="./lib">
			<include name="**/AntTaskBarFileConfig.jar" />
		</fileset>
	</path>
```

Define the task `AntTaskBarFileConfig:`
```
	<taskdef name="AntTaskBarFileConfig" classname="uk.co.marcoratto.ant.ibm.broker.BarFileConfig" classpathref="AntTaskSapAdapter.classpath" />
```

Define all the tasks you need:
```
	<target name="test.read">
	 	<AntTaskBarFileConfig 
	 		action="read"
	 		sourcebarfile="./filename.bar"
	 		tempdir="./tmp"
	 	/>
	</target>		

	<target name="test.export">
	 	<AntTaskBarFileConfig 
	 		action="export"
	 		sourcebarfile="./filename.bar"
	 		tempdir="./tmp"
	 		propertiesfile="./filename.properties"
	 		override="true"
	 	/>
	</target>
	
<target name="test.write">
	 	<AntTaskBarFileConfig 	 		
	 		action="write"
	 		sourcebarfile="./filename.bar"
	 		targetbarfile="./filename_new.bar"
	 		propertiesfile="./filename.properties"
	 		override="true"
	 		tempdir="./tmp"
	 	/>
	</target>
```

Run the task:
```
ant -v -d test.read
```
```
ant test.export
```

```
ant test.write
```