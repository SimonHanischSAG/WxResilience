<h1>WxResilience</h1>
webMethods IntegrationServer package for implementing resilient and robust services using a standardized logging out-of-the-box.

WxResilience is hereby the "productive" package on which you may create a package dependency. The other packages are containing both tests and examples how to work with WxResilience.

It is designed for usage together with the official packages WxConfig (or the free alternative https://github.com/SimonHanischSAG/WxConfigLight) and optionally together with packages for logging like WxLog, WxLog2 or WxSplunk.

<b>MANY THANKS TO LIDL AND SCHWARZ IT, who kindly allowed to provide the template for this package and make it public.</b>

<h2>How to use (basic configuration)</h2>

<h3>Provide WxConfig or WxConfigLight</h3>
compare with above

<h3>Deploy/checkout WxResilience etc.</h3>

Check under releases for a proper release and deploy it. Otherwise you can check out the latest version from GIT and create a link like this:

mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience                F:\\GIT-Repos\\WxResilience\\packages\\WxResilience
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience_Test           F:\\GIT-Repos\\WxResilience\\packages\\WxResilience_Test
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilienceDemoFlow        F:\\GIT-Repos\\WxResilience\\packages\\WxResilienceDemoFlow
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilienceDemoInvokeChain F:\\GIT-Repos\\WxResilience\\packages\\WxResilienceDemoInvokeChain
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilienceDemoMetaData    F:\\GIT-Repos\\WxResilience\\packages\\WxResilienceDemoMetaData

<h4>Build & Reload</h4>

If you checkout the sources from GitHub you have to compile the source, e.g. with:

C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilienceDemoInvokeChain

Reload WxResilience and WxResilienceDemoInvokeChain

<h3>Validate</h3>

Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\server.log" for entries like:

2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Scanning all packages for ErrorHandling.xml files 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Validating the summarized error handling 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- ExceptionHandlingSummarized.xml is valid 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Initializing error handling framework... 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Successfully initialized error handling 

<h2>How to use WxResilience together with WxLog and/or WxLog2 (optional configuration)</h2>

Enable specific, different logfiles by usage of WxLog or WxLog2. Therefore:

<h3>Install WxLog and/or WxLog2</h3>

<h3>Configure specific logging</h3>
	
In case of WxLog2 WxResilience is already preconfigured **(but not enabled)**. In case of WxLog you can add the following to wxlog_default.xml:

<pre><code>
  &lt;appender name="WxResilience" class="ch.qos.logback.core.FileAppender"&gt;
    &lt;file&gt;logs/wxlog/WxResilience.log&lt;/file&gt;
    &lt;append&gt;true&lt;/append&gt;
    &lt;encoder&gt;
      &lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss} %logger{50} %3([%.-1level]): %msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;logger name="WxResilience" level="INFO" &gt;
	&lt;appender-ref ref="WxResilience"/&gt;
  &lt;/logger&gt;

  &lt;appender name="WxResilience_Test" class="ch.qos.logback.core.FileAppender"&gt;
    &lt;file&gt;logs/wxlog/WxResilience_Test.log&lt;/file&gt;
    &lt;append&gt;true&lt;/append&gt;
    &lt;encoder&gt;
      &lt;pattern&gt;%d{yyyy-MM-dd HH:mm:ss} %logger{50} %3([%.-1level]): %msg%n&lt;/pattern&gt;
    &lt;/encoder&gt;
  &lt;/appender&gt;

  &lt;logger name="WxResilience_Test" level="INFO" &gt;
	&lt;appender-ref ref="WxResilience_Test"/&gt;
  &lt;/logger&gt;

</code></pre>

<h3>Enable logging using WxLog/WxLog2</h3>

The fast/non-persistent way: Enable/comment related key(s) under "C:\SoftwareAG105\IntegrationServer\instances\default\packages\WxResilience\config\wxconfig.cnf"

The persistent way: Copy the related key(s) from "C:\SoftwareAG105\IntegrationServer\instances\default\packages\WxResilience\config\wxconfig.cnf" to "C:\SoftwareAG105\IntegrationServer\instances\default\config\packages\WxResilience\wxconfig-&lt;env&gt;.cnf" where &lt;env&gt; is the environment which you have configured in WxConfig(Light).

<h3>Validate</h3>

Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\WxResilience.log"

<h2>How to build resilient services</h2>

There are two options to use WxResilience in your top level services:
1. Automatically from InvokeChain
2. Per code in your top-level-services

In detail:
1. Set the key <pre><code>wxconfig-&lt;env&gt;.cnf/invokeChainProcessor.enabled=true</code></pre> and run the startup service of WxResilience. It will run the predefined services (see below) around of your top-level-service as it is registered in the InvokeChain of IntegrationServer before your top-level-service is invoked. This option can be choosen if WxResilience is introduced at once on a existing system. It will immediately work for all top-level-services and can be deactivated or configured.
Restrictions: The InvokeChain-technique is not working during debugging!
2. Implement try-catch-blocks in your top level services using the predefined services directly (see below). This option provides you more control for which services and how it will work. But this option requires code changes. To see the suggestion how to build a resilient service take a look on: WxResilienceDemoFlow/wx.resilienceDemoFlow.pub:test_top

It is possible to mix both options as there is a check not to run the services twice for a top-level-service call. 

<h4>correlationId - customContextId</h4>
preProcessForTopLevelService is reading the JMSCorrelationID (JMS) respectively the X-Correlation-ID (HTTP) from callers, stores it in wxMetaData/correlationId and use it for setCustomContextID (visible in Monitor/E2EM). Finally these IDs are forwarded via JMS and HTTP if you use the related wrapper services in WxResilience or autmatically, if you use the InvokeChain.
 
<h3>Predefined services for start / end logging and exception handling</h3>

There are the following 3 helper services which are executed automatically in option 1 or which you should use in every top level service in option 2:

<h3>wx.resilience.pub.resilience:preProcessForTopLevelService</h3>

Start logging and capturing the start timestamp

<h3>wx.resilience.pub.resilience:handleError</h3>

This service in the catch block has to decide if there is a need for throwing another (retry) exception or not. Therefore it is using a bunch of single predefined error scenarioes defined in WxResilience/config/ExceptionHandling.xml.

Furthermore the service is writing helpful information to the log for investigating the error.

Finally the service will set the standardized outputs "errors" respectively "error" if the top level service was called over REST or SOAP.

<h3>wx.resilience.pub.resilience:postProcessForTopLevelService</h3>

Measuring the duration, final logging and rethrowing an error if necessary (based on output of handleError).

<h3>Additional service for better logging</h3>

For better logging you should give the predefined service some generic insights into your data (some meta-data). The suggestion is to use the predefined wxMetaData for this. If you have already such a document lying on your pipeline you can provide WxResilience a service how it can convert your metaData into wxMetaData during runtime:

<h3>wx.resilience.pub.metaData:initializeWxMetaData</h3>

This service generates the so-called "wxMetaData". These wxMetaData will be reused for logging (also for errors) and can also help you to establish generic standards in your implementation if you forward wxMetaData as part of a so-called wxMessage to the next step. These are the fields:

<ul>
  <li>creationTimestamp: When the metaData were generated</li>
  <li>correlationId: Identifies a correlated data/message. Will be set automatically from **JMSMessageID** (JMS) or **X-Correlation-ID** (HTTP) if available. Otherwise it is generated</li>
  <li>uuid: A generated ID for each physical message. Different uuids can belong to one correlationId</li>
  <li>type: Defines the type of the data, e.g. order, masterDataUpdate, ...</li>
  <li>source: Where the handled data comes from</li>
  <li>destination: [optional] Where should the handled data go</li>
  <li>confidential: [optional] Is the data confidential -> data will not be written/sent somewhere else</li>
  <li>customProperties: [optional] Put your own key/value pairs for identifying your business message in logging or implementing generic handlings  based on these properties</li>
</ul>

<h3>metaData.conversion</h3>

Provide a service with output wxMetaData and configure it like this:
<pre><code>
metaData.conversion.service.ifcname=wx.resilienceDemoInvokeChain.pub
metaData.conversion.service.svcname=mapMyMetaDataToWxMetaData
</code></pre>
WxResilience will then use that service an map your metaData on demand to wxMetaData depending onk how you have implemented this service. (Compare with example service  WxResilienceDemoMetaData/wx.resilienceDemoMetaData.pub:mapMyMetaDataToWxMetaData)

<h2>Special features of error handling</h2>

<h3>ExceptionHandling.xml</h3>

Compare with
https://github.com/SimonHanischSAG/WxResilience/blob/main/packages/WxResilience/config/ExceptionHandling.xml
to see the predefined defaults of WxResilience for ExceptionHandling and
https://github.com/SimonHanischSAG/WxResilience/blob/main/packages/WxResilience_Test/config/ExceptionHandling.xml
to see what you can do additionally in your own package.

<h4>Tags:</h4>
<ul>
  <li>globalException: Define a handling independently from the location</li>
  <li>location: Define a (hierarchical) handling for specific folders or services</li>
  <li>exception: Define both some filter conditions and outcome for a handling</li>
</ul>

<h4>Attributes for filtering/more specific definition:</h4>
<ul>
  <li>type: The name of the exception class or "all"</li>
  <li>callerType: Over which protocal was the top level service invoked: REST, RESTv2, RAD, SOAP, HTTP, FTP, FILEPOLLING, JMS, STARTUP_SHUTDOWN, SCHEDULER, MFT, OTHER</li>
  <li>errorMessageContains: A string which must be part of the error message</li>
  <li>errorMessageRegex: A regex which matches the whole error message</li>
</ul>

<h4>Attributes for output:</h4>
<ul>
  <li>errorToBeThrown: Define if there should be an exception rethrown (FATAL) or not (NONE) after maybe finishing all retry attempts</li>
  <li>maxRetryAttempts: Define how many TRANSIENT errors shall be thrown before errorToBeThrown comes into action in the final retry attempt. Consider that this is helpfull only under triggers which have a higher value for "Max retry attempts" than defined here. Then the retries are under the control of the ExceptionHandling.xml files (configurable during runtime) and not hard coded (at the trigger). Furthermore we suggest to define "On retry failure"="Suspend and retry later" as you can disable such triggers without message lost during a retry loop. Normally "Suspend and retry later" without resource monitoring is a dubious option, but if "Max retry attempts" is high enough it does not come into action.</li>
  <li>exceptionHandlingId: Define a id which will be logged</li>
</ul>

<h4>Nested Exception Handling</h4>

You can use a nested construction of try-catch blocks like this:
<ul>
	<li>service1: top level service with try-catch</li>
	<li>service2: sub service with try-catch</li>
</ul>
You can now define an exception handling for service1 (rethrowing the error) and service2 (doing something else). Although service1 is in the callstack of the second error the service where the error is rethrown is excluded from the error handling above.
	
	
<h3>Break endless loop</h3>
On a running productive system it could be that you have a problem with a message in a endless retry loop of a trigger. If that exception for retry is thrown by WxResilience and because of a definition from a ExceptionHandling.xml file you can break this loop by adding such a key temporarily for a message with metaData/uuid=b86b6639-b026-49ca-ad01-3d19810c3cda:

<h4>With official WxConfig in WxResilience/config/wxconfig.cnf</h4>
break.retry.loop.for.id=b86b6639-b026-49ca-ad01-3d19810c3cda

<h4>With unofficial WxConfigLight via Global Varialbes</h4>
WxConfigLight__WxResilience__break.retry.loop.for.id=b86b6639-b026-49ca-ad01-3d19810c3cda

<h3>DocumentDiscardedException</h3>

Sometimes there is the need to drop a message within your code (maybe deep in a sub service). To do that easily and savely you can invoke the service without developing any other code, exits or whatever:

wx.resilience.pub.resilience:throwDocumentDiscardedException

WxResilience will catch this error, write a proper logging and do not rethrow any error.

<h3>Status messages</h3>

You can configure under wxconfig-&lt;env&gt;.cnf:
status.publishStatusError.enabled=true

Furthermore you have to setup a topic "StatusTopic" under DEFAULT_IS_JMS_CONNECTION.

Then all ExceptionHandlings which are using

<handling>wx.resilience.pub.resilience.errorHandling:publishErrorStatus</handling>

will publish a JMS messages containing the most important information about the error. It is published as WxMessage in JSON. 
HINT: In the containing wxMetaData <b>SOURCE AND DESTINATION ARE SWITCHED!</b> This can be used directly in external systems to retrieve a status about their original messages using JMSMessage/properties/destination (= original sender).

Furthermore you can use 

wx.resilience.pub.resilience:publishStatus

to publish your own status information using your desired type (Completed, Information, CompletedWithWarning, Retried, Failed, Discarded).

Finally you can allow the external systems to send such messages by there own as they are sent in JSON (and not IData). In that case the external system A can send a message over IS to the external system B. System B can acknowledge that by a status "Completed". System A can receive and process that. If the message is failing in IS system A would receive an status "Failed" from IS using the same technique.




