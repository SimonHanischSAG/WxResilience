<h1>WxResilience</h1>
webMethods IntegrationServer package for implementing resilient and robust services.

WxResilience is hereby the "productive" package on which you may create a package dependency. WxResilience_Test contains both tests and examples how to work with WxResilience.

It is designed for usage together with the official packages WxConfig (or the free alternative https://github.com/SimonHanischSAG/WxConfigLight) and optionally together with the official packages WxLog or WxLog2.

<b>MANY THANKS TO LIDL AND SCHWARZ IT, who kindly allowed to provide the template for this package and make it public.</b>

<h2>How to use (basic configuration)</h2>

<h3>Provide WxConfig or WxConfigLight</h3>
compare with above

<h3>Deploy/checkout WxResilience/WxResilience_Test</h3>

Check under releases for a proper release and deploy it. Otherwise you can check out the latest version from GIT and create a link like this:

mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience F:\\GIT-Repos\\WxResilience\\packages\\WxResilience
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience_Test F:\\GIT-Repos\\WxResilience\\packages\\WxResilience_Test

<h4>Initialize in case of WxConfigLight</h4>

If you are using WxConfigLight you have to run

http://localhost:5555/invoke/wx.config.admin:replaceVariablesWithGlobalFile?wxConfigPkgName=WxResilience 

in order to load the keys of WxResilience into the necessary Global Variables. WxConfig will do that automatically.

<h4>Build & Reload</h4>

If you checkout the sources from GitHub you have to compile the source, e.g. with:

C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience_Test

Reload WxResilience

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
	
In case of WxLog2 WxResilience is already preconfigured. In case of WxLog you can add the following to wxlog_default.xml:

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

Enable/comment related key(s) under "C:\SoftwareAG105\IntegrationServer\instances\default\packages\WxResilience\config\wxconfig.cnf"

<h4>Initialize in case of WxConfigLight</h4>
If you are using WxConfigLight you have to run http://localhost:5555/invoke/wx.config.admin:replaceVariablesWithGlobalFile?wxConfigPkgName=WxResilience in order to load the keys of WxResilience into the necessary Global Variables. WxConfig will do that automatically.

Reload WxResilience (as the logging configuration is cached for better performance)

<h3>Validate</h3>

Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\WxResilience.log"


<h2>How to build resilient services</h2>

To see the suggestion how to build a resilient service take a look on:

WxResilience_Test/wx.resilienceTest.pub:genericTopLevelService

It contains 4 important helper services which you should use like this in every top level service:

<h3>wx.resilience.pub.metaData:initializeMetaData</h3>

This service generates the so-called "metaData". These metaData will be reused for logging (also for errors) and can also help you to establish generic standards in your implementation. These are the fields:

<ul>
  <li>creationTimestamp: When the metaData were generated</li>
  <li>uuid: Identifies the related data/message</li>
  <li>correlationUuid: [optional] Identifies a correlated data/message (e.g. a related previous message)</li>
  <li>type: Defines the type of the data, e.g. order, masterDataUpdate, ...</li>
  <li>source: Where the handled data comes from</li>
  <li>destination: [optional] Where should the handled data go</li>
  <li>confidential: [optional] Is the data confidential -> data will not be written/sent somewhere else</li>
  <li>customProperties: [optional] Put your own key/value pairs for identifying your business message in logging or implementing generic handlings  based on these properties</li>
</ul>

<h3>wx.resilience.pub.resilience:preProcessForTopLevelService</h3>

Start logging and capturing the start timestamp

<h3>wx.resilience.pub.resilience:handleError</h3>

This service in the catch block has to decide if there is a need for throwing another (retry) exception or not. Therefore it is using a bunch of single predefined error scenarioes defined in WxResilience/config/ExceptionHandling.xml.

Furthermore the service is writing helpful information to the log for investigating the error.

Finally the service will set the standardized outputs "errors" respectively "error" if the top level service was called over REST or SOAP.

<h3>wx.resilience.pub.resilience:postProcessForTopLevelService</h3>

Measuring the duration, final logging and rethrowing an error if necessary (based on output of handleError).


<h2>Special features of error handling</h2>


<h3>ExceptionHandling.xml</h3>

Compare with
https://github.com/SimonHanischSAG/WxResilience/blob/main/packages/WxResilience/config/ExceptionHandling.xml
to see the predefined defaults of WxResilience for ExceptionHandling and
https://github.com/SimonHanischSAG/WxResilience/blob/main/packages/WxResilience_Test/config/ExceptionHandling.xml
to see what you can do additionally in your own package.

<h4>Tags:</h4>
<ul>
  <li>location: </li>
  <li>globalException: Define a handling independently from the location</li>
  <li>location: Define a (hierarchical) handling for specific folders or services</li>
  <li>exception: Define both some filter conditions and outcome for a handling</li>
</ul>

nested exception handling, type, callerType, errorMessageContains, errorMessageRegex
	
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
