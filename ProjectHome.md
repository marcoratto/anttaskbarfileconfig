_`AntTaskBarFileConfig`_ is a task for [Apache Ant](http://ant.apache.org/) for customizing a BAR File generated with [IBM Websphere Broker Toolkit](http://www-01.ibm.com/software/integration/wbimessagebroker/).

First of all, why do you need to customize a BAR file ?

When you have to migrate from one environment to another (for example, from Development to Quality, from Quality to Production, etc), you have to customize some parameters of the BAR file for the target System (queue names, SAP username, path on file system, SAP password, SAP Client, SAP Gateway, etc.)

Actually, IBM has the tool _mqsiapplybaroverride.exe_ but you can just customize the variable defined in any flows and subflows; but, if you have also the SAP Adapter (Inbound or Outbound) you cannot customize it.

So, if you want to customize all you can use freely this Apache Ant Task.

Requirements:

  * [Sun JDK 1.5.x or 1.6.x](http://www.oracle.com/technetwork/java/javase/overview/index.html)

  * [Apache Ant 1.9.4](http://ant.apache.org/)

  * IBM Websphere Message Broker 6.x or 7.x (tested on 6.1.0.8 and 7.0.0.3)

For developers:

  * [Eclipse IDE](https://eclipse.org/)

Current customizations are:

  * File **META-INF/broker.xml**
    * All XML tag _`ConfigurableProperty`_

  * File **`*`.import**
    * applicationServerHost
    * BONamespace
    * client
    * gatewayHost
    * gatewayService
    * language
    * numberOfListeners
    * password
    * rfcProgramID
    * RFCTraceLevel
    * RFCTracePath
    * sncLib
    * sncQop
    * systemNumber
    * userName

  * File **`*`.export**
    * applicationServerHost
    * BONamespace
    * client
    * gatewayHost
    * gatewayService
    * language
    * numberOfListeners
    * password
    * rfcProgramID
    * RFCTraceLevel
    * RFCTracePath
    * sncLib
    * sncQop
    * systemNumber
    * userName

Current features are:

  * Read parameters (Flows, Sub-flows, SAP Adapter inbound, SAP Adapter outbound)

  * Export all parameters to a properties files

  * Write the customization

Below you can see the correct use of the parameters with the chosen action:

| **Parameter/Action** | **Read** | **Export** | **Write** | **Default** | **Note** |
|:---------------------|:---------|:-----------|:----------|:------------|:---------|
| **action**           | M        | M          | M         | n/a         | The action (read, write, export) |
| **sourcebarfile**    | M        | M          | M         | n/a         | The source BAR file |
| **targetbarfile**    | n/a      | n/a        | M         | n/a         | The target BAR file |
| **propertiesfile**   | n/a      | M          | M         | n/a         | The properties file |
| **addpropertiesfile** | n/a      | n/a        | M         | n/a         | Add parameters for SAP JCA |
| **removepropertiesfile** | n/a      | n/a        | M         | n/a         | Remove parameters for SAP JCA |
| **override**         | n/a      | O          | O         | n/a         | Override if the output file exists (targetbarfile or propertiesfile) |
| **tempdir**          | O        | O          | O         | the O.S. setting (%TEMP% on Windows, $TEMP on Unix) | The TEMP directory |

[![](http://www2.clustrmaps.com/stats/maps-no_clusters/code.google.com-p-anttaskbarfileconfig--thumb.jpg)](http://www2.clustrmaps.com/user/33710a84f)