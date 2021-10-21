<h1>WxResilience</h1>
webMethods IntegrationServer package for implementing resilient and robust services.

It is designed for usage together with the official packages WxConfig (or the free alternative https://github.com/SimonHanischSAG/WxConfigLight) and optionally together with the official packages WxLog or WxLog2.

<b>MANY THANKS TO LIDL AND SCHWARZ IT, who kindly allowed to provide the template for this package and make it public.</b>

<h2>How to use (basic configuration)</h2>

<h3>Provide WxConfig or WxConfigLight</h3>
compare with above

<h3>Deploy/checkout WxResilience/WxResilience_Test</h3>

When checked out you can create a link like this:

mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience F:\\GIT-Repos\\WxResilience\\packages\\WxResilience
mklink /J F:\\SoftwareAG\\IntegrationServer\\instances\\default\\packages\\WxResilience_Test F:\\GIT-Repos\\WxResilience\\packages\\WxResilience_Test

<h4>Initialize in case of WxConfigLight</h4>

If you are using WxConfigLight you have to run http://localhost:5555/invoke/wx.config.admin:replaceVariablesWithGlobalFile?wxConfigPkgName=WxResilience in order to load the keys of WxResilience. WxConfig will do that automatically

<h4>Build & Reload</h4>

If you checkout the sources from GitHub you have to compile the source, e.g. with:
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience
C:\SoftwareAG\IntegrationServer\instances\default\bin\jcode.bat makeall WxResilience_Test

Reload WxResilience

<h4>Validate</h4>

Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\server.log" for entries like:

2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Scanning all packages for ErrorHandling.xml files 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Validating the summarized error handling 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- ExceptionHandlingSummarized.xml is valid 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Initializing error handling framework... 
2021-10-12 10:47:45 MESZ [ISP.0090.0004I] WxResilience -- Successfully initialized error handling 

<h2>How to use (extended configuration)</h2>

Enable specific, different logfiles by usage of WxLog or WxLog2. Therefore:

1. install such a package:

2. Configure the related logging configuration. In case of WxLog2 WxResilience is already preconfigured. In case of WxLog you can add the following to wxlog_default.xml:

  <appender name="WxResilience" class="ch.qos.logback.core.FileAppender">
    <file>logs/wxlog/WxResilience.log</file>
    <append>true</append>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss} %logger{50} %3([%.-1level]): %msg%n</pattern>
    </encoder>
  </appender>

  <logger name="WxResilience" level="INFO" >
	<appender-ref ref="WxResilience"/>
  </logger>

  <appender name="WxResilience_Test" class="ch.qos.logback.core.FileAppender">
    <file>logs/wxlog/WxResilience_Test.log</file>
    <append>true</append>
    <encoder>
      <pattern>%d{yyyy-MM-dd HH:mm:ss} %logger{50} %3([%.-1level]): %msg%n</pattern>
    </encoder>
  </appender>

  <logger name="WxResilience_Test" level="INFO" >
	<appender-ref ref="WxResilience_Test"/>
  </logger>


3. Enable/comment related key under "C:\SoftwareAG105\IntegrationServer\instances\default\packages\WxResilience\config\wxconfig.cnf"

3.b. If you are using WxConfigLight you have to run http://localhost:5555/invoke/wx.config.admin:replaceVariablesWithGlobalFile?wxConfigPkgName=WxResilience in order to load the keys of WxResilience. The official WxConfig will do that automatically

4. Reload WxResilience

3. Check "C:\SoftwareAG\IntegrationServer\instances\default\logs\WxResilience.log"
